package br.com.creditas.credit_simulator.adapter.`in`

import br.com.creditas.credit_simulator.adapter.`in`.request.CreditSimulationRequest
import br.com.creditas.credit_simulator.adapter.`in`.response.AsyncResponse
import br.com.creditas.credit_simulator.adapter.out.notification.event.CreditSimulationEvent
import br.com.creditas.credit_simulator.adapter.out.repository.CreditSimulationRepository
import br.com.creditas.credit_simulator.application.domain.InterestRate
import br.com.creditas.credit_simulator.application.service.CreditSimulatorService
import br.com.creditas.credit_simulator.base.IntegrationBaseTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CreditSimulatorControllerAsynchronousTest : IntegrationBaseTest() {

    @Autowired
    private lateinit var creditSimulationRepository: CreditSimulationRepository

    @SpyBean
    private lateinit var creditSimulatorPort: CreditSimulatorService

    private lateinit var consumer: Consumer<String, CreditSimulationEvent>

    @BeforeEach
    fun setUp() {
        deleteAllEmail()
        creditSimulationRepository.deleteAll()
        consumer = createConsumer(CreditSimulationEvent::class.java)
        consumer.subscribe(listOf(topic))
    }

    @AfterEach
    fun tearDown() {
        consumer.close()
    }

    @Test
    fun `deve realizar uma simulação com sucesso de forma assíncrona`(): Unit = runBlocking {

        // cenario
        val simulation = CreditSimulationRequest(
            presentValue = BigDecimal("100000.00"),
            dateOfBirth = LocalDate.of(1996, 3, 23),
            numberOfPayments = 50,
            email = "assincrono@teste.com"
        )
        val creditSimulation = simulation.toModel(UUID.randomUUID())
        val intersRate = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.03")
        )
        creditSimulation.process(intersRate)

        doAnswer { invocation ->
            launch {
                delay(2000L)
                invocation.callRealMethod()
            }
        }.`when`(creditSimulatorPort).invoke(LocalDate.of(1996, 3, 23), creditSimulation)

        val request = MockMvcRequestBuilders.post("/v1/credit/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(simulation))

        // ação
        val response = mockMvc.perform(request).andReturn().asyncResult as ResponseEntity<AsyncResponse>

        // validação
        assertEquals(202, response.statusCode.value())
        assertEquals("Request is processing and will finish asynchronously", response.body!!.message)
        assertNotNull(response.body!!.simulationId)

        // verificando mensagem no banco de dados
        await
            .timeout(5, SECONDS)
            .failFast(
                "Deve existir um item cadastrado",
                Callable { creditSimulationRepository.count() == 0L }
            )
            .until { creditSimulationRepository.count() == 1L }

        // verificando mensagem kafka
        val records: ConsumerRecords<String, CreditSimulationEvent> =
            KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertThat(records)
            .hasSize(1)

        assertNotNull(records.first().value().simulationId)
        assertEquals(simulation.presentValue, records.first().value().presentValue)
        assertEquals(simulation.numberOfPayments, records.first().value().numberOfPayments)
        assertEquals(intersRate.interestRateType, records.first().value().interestRate!!.interestRateType)
        assertEquals(intersRate.annualInterestRate, records.first().value().interestRate!!.annualInterestRate)
        assertNull(records.first().value().interestRate!!.spread)
        assertNull(records.first().value().interestRate!!.marketIndexName)
        assertNull(records.first().value().interestRate!!.marketIndexAnnualInterestRate)
        assertEquals(BigDecimal("2130.10"), records.first().value().monthlyPayment)

        // Verificação do email
        val result = checkEmailSent()
        assertTrue(result.messages.size == 1)
        val email = result.messages.first()
        assertEquals(simulation.email!!, email.destination.toAddresses.first())
    }
}