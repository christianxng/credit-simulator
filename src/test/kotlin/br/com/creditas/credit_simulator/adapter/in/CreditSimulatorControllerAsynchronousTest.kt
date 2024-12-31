package br.com.creditas.credit_simulator.adapter.`in`

import br.com.creditas.credit_simulator.adapter.`in`.request.CreditSimulationRequest
import br.com.creditas.credit_simulator.adapter.`in`.response.AsyncResponse
import br.com.creditas.credit_simulator.adapter.out.notification.event.CreditSimulationEvent
import br.com.creditas.credit_simulator.adapter.out.repository.CreditSimulationRepository
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


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CreditSimulatorControllerAsynchronousTest : IntegrationBaseTest() {

    @Autowired
    private lateinit var creditSimulationRepository: CreditSimulationRepository

    @SpyBean
    private lateinit var creditSimulatorPort: CreditSimulatorService

    private lateinit var consumer: Consumer<String, CreditSimulationEvent>

    @BeforeEach
    fun setUp() {
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

        //cenario
        val simulate = CreditSimulationRequest(
            presentValue = BigDecimal("100000.00"),
            dateOfBirth = LocalDate.of(1996, 3, 23),
            numberOfPayments = 50,
            email = "teste@teste.com"
        )
        val creditSimulation = simulate.toModel(UUID.randomUUID())
        creditSimulation.process(BigDecimal("0.03"))

        doAnswer { invocation ->
            launch {
                delay(2000L)
                invocation.callRealMethod()
            }
        }.`when`(creditSimulatorPort).invoke(LocalDate.of(1996, 3, 23), creditSimulation)

        val request = MockMvcRequestBuilders.post("/v1/credit/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(simulate))

        //acao
        val response = mockMvc.perform(request).andReturn().asyncResult as ResponseEntity<AsyncResponse>

        //validacao
        assertEquals(202, response.statusCode.value())
        assertEquals("Request is processing and will finish asynchronously", response.body!!.message)
        assertNotNull(response.body!!.simulationId)

        await
            .timeout(5, SECONDS)
            .failFast(
                "Deve existir um item cadastrado",
                Callable { creditSimulationRepository.count() == 0L }
            )
            .until { creditSimulationRepository.count() == 1L }

        val records: ConsumerRecords<String, CreditSimulationEvent> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertThat(records)
            .hasSize(1)

    }
}