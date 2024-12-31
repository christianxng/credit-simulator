package br.com.creditas.credit_simulator.adapter.`in`

import br.com.creditas.credit_simulator.adapter.`in`.request.CreditSimulationRequest
import br.com.creditas.credit_simulator.adapter.`in`.response.CreditSimulationResponse
import br.com.creditas.credit_simulator.adapter.out.notification.event.CreditSimulationEvent
import br.com.creditas.credit_simulator.adapter.out.repository.CreditSimulationRepository
import br.com.creditas.credit_simulator.base.IntegrationBaseTest
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertNotNull


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CreditSimulatorControllerSynchronousTest : IntegrationBaseTest() {

    @Autowired
    private lateinit var creditSimulationRepository: CreditSimulationRepository

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
    fun `deve realizar uma simulação com sucesso de forma síncrona`() {

        //cenario
        val simulate = CreditSimulationRequest(
            presentValue = BigDecimal("100000.00"),
            dateOfBirth = LocalDate.of(1996, 3, 23),
            numberOfPayments = 50,
            email = "teste@teste.com"
        )

        val request = MockMvcRequestBuilders.post("/v1/credit/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(simulate))

        //acao
        val response = mockMvc.perform(request).andReturn().asyncResult as ResponseEntity<CreditSimulationResponse>

        //validacao
        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body!!.simulationId)
        assertEquals(50, response.body!!.numberOfPayments)
        assertEquals(BigDecimal("100000.00"), response.body!!.presentValue)
        assertEquals(BigDecimal("0.03"), response.body!!.annualInterestRate)
        assertEquals(BigDecimal("2130.10"), response.body!!.monthlyPayment)

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

    @Test
    fun `não deve realizar a simulação quando é informado dados de entrada inválidos`() {

        //cenario
        val novaSimulacao = CreditSimulationRequest(
            presentValue = null,
            dateOfBirth = null,
            numberOfPayments = null,
            email = null
        )

        val request = MockMvcRequestBuilders.post("/v1/credit/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(novaSimulacao))

        //acao
        val response = mockMvc.perform(request)

        //validacao
        response.andExpectAll(
            status().isBadRequest,
            jsonPath("$.type").value("https://zalando.github.io/problem/constraint-violation"),
            jsonPath("$.status").value(400),
            jsonPath("$.title").value("Constraint Violation"),
            jsonPath(
                "$.violations[*].field", Matchers.containsInAnyOrder(
                    "dateOfBirth",
                    "email",
                    "numberOfPayments",
                    "presentValue",
                )
            ),
            jsonPath(
                "$.violations[*].message", Matchers.containsInAnyOrder(
                    "não deve ser nulo",
                    "não deve estar em branco",
                    "não deve ser nulo",
                    "não deve ser nulo"
                )
            )
        )

        await
            .timeout(5, SECONDS)
            .failFast(
                "Deve existir um item cadastrado",
                Callable { creditSimulationRepository.count() == 1L }
            )
            .until { creditSimulationRepository.count() == 0L }

        val records: ConsumerRecords<String, CreditSimulationEvent> = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5))
        assertThat(records)
            .hasSize(0)
    }
}