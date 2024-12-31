package br.com.creditas.credit_simulator.application.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class CreditSimulationTest {

    @Test
    fun `deve criar simulação de crédito com sucesso`() {
        val simulationId = UUID.randomUUID()
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 12,
            email = "teste@teste.com",
            simulationId = simulationId
        )

        assertEquals(simulationId, simulation.simulationId)
        assertEquals(BigDecimal("10000.00"), simulation.presentValue)
        assertEquals(12, simulation.numberOfPayments)
        assertEquals("teste@teste.com", simulation.email)
        assertNull(simulation.monthlyPayment)
        assertNull(simulation.annualInterestRate)
        assertNull(simulation.status)
    }

    @Test
    fun `deve realizar simulação com sucesso`() {
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 12,
            email = "teste@teste.com",
            simulationId = UUID.randomUUID()
        )

        simulation.process(BigDecimal("0.12")) // 12% taxa de juros anual

        assertTrue(simulation.isSimulationWithSuccess())
        assertFalse(simulation.isSimulationWithError())
        assertEquals(BigDecimal("0.12"), simulation.annualInterestRate)
        assertEquals(BigDecimal("888.49"), simulation.monthlyPayment)
    }

    @Test
    fun `deve criar a simulação com erro quando o cálculo falha`() {
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 0, // numero de pagamentos inválidos para forçar erro
            email = "teste@teste.com",
            simulationId = UUID.randomUUID()
        )

        assertThrows<ArithmeticException> {
            simulation.process(BigDecimal("0.12"))
        }

        assertTrue(simulation.isSimulationWithError())
        assertFalse(simulation.isSimulationWithSuccess())
    }

    @Test
    fun `deve fazer o cálculo do PMT corretamente para diferentes cenários`() {
        val testCases = listOf(
            Triple(BigDecimal("10000.00"), 12, BigDecimal("0.12")), // 12% taxa de juros anual
            Triple(BigDecimal("20000.00"), 24, BigDecimal("0.10")), // 10% taxa de juros anual
            Triple(BigDecimal("50000.00"), 36, BigDecimal("0.08"))  //  8% taxa de juros anual
        )

        testCases.forEach { (presentValue, numberOfPayments, annualRate) ->
            val simulation = CreditSimulation.create(
                presentValue = presentValue,
                numberOfPayments = numberOfPayments,
                email = "teste@teste.com",
                simulationId = UUID.randomUUID()
            )

            simulation.process(annualRate)

            assertTrue(simulation.isSimulationWithSuccess())
            assertNotNull(simulation.monthlyPayment)
            assertTrue(simulation.monthlyPayment!! > BigDecimal.ZERO)
        }
    }
}