package br.com.creditas.credit_simulator.application.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class CreditSimulationTest {

    @Test
    fun `deve criar simulação de crédito com sucesso`() {
        // cenario 
        val simulationId = UUID.randomUUID()

        // ação
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 12,
            email = "teste@teste.com",
            simulationId = simulationId
        )

        // validação
        assertEquals(simulationId, simulation.simulationId)
        assertEquals(BigDecimal("10000.00"), simulation.presentValue)
        assertEquals(12, simulation.numberOfPayments)
        assertEquals("teste@teste.com", simulation.email)
        assertNull(simulation.monthlyPayment)
        assertNull(simulation.interestRate)
        assertNull(simulation.status)
    }

    @Test
    fun `deve realizar simulação com sucesso`() {
        // cenario 
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 12,
            email = "teste@teste.com",
            simulationId = UUID.randomUUID()
        )

        val intersRate = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.12")
        )
        // ação
        simulation.process(intersRate) // 12% taxa de juros anual

        // validação
        assertTrue(simulation.isSimulationWithSuccess())
        assertFalse(simulation.isSimulationWithError())
        assertEquals(BigDecimal("0.12"), simulation.interestRate!!.annualInterestRate)
        assertEquals(BigDecimal("888.49"), simulation.monthlyPayment)
    }

    @Test
    fun `deve criar a simulação com erro quando o cálculo falha`() {
        // cenario 
        val simulation = CreditSimulation.create(
            presentValue = BigDecimal("10000.00"),
            numberOfPayments = 0, // numero de pagamentos inválidos para forçar erro
            email = "teste@teste.com",
            simulationId = UUID.randomUUID()
        )

        val intersRate = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.12")
        )

        // ação
        assertThrows<ArithmeticException> {
            simulation.process(intersRate)
        }

        // validação
        assertTrue(simulation.isSimulationWithError())
        assertFalse(simulation.isSimulationWithSuccess())
    }

    @Test
    fun `deve fazer o cálculo do PMT corretamente para diferentes cenários`() {

        // cenario 
        val intersRate1 = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.12") // 12% taxa de juros anual
        )

        val intersRate2 = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.10") // 10% taxa de juros anual
        )

        val intersRate3 = InterestRate(
            interestRateType = InterestRate.InterestRateType.FIXED,
            annualInterestRate = BigDecimal("0.08") //  8% taxa de juros anual
        )

        val testCases = listOf(
            Triple(BigDecimal("10000.00"), 12, intersRate1),
            Triple(BigDecimal("20000.00"), 24, intersRate2),
            Triple(BigDecimal("50000.00"), 36, intersRate3)
        )

        testCases.forEach { (presentValue, numberOfPayments, intersRate) ->
            
            val simulation = CreditSimulation.create(
                presentValue = presentValue,
                numberOfPayments = numberOfPayments,
                email = "teste@teste.com",
                simulationId = UUID.randomUUID()
            )

            // ação
            simulation.process(intersRate)

            // validação
            assertTrue(simulation.isSimulationWithSuccess())
            assertNotNull(simulation.monthlyPayment)
            assertTrue(simulation.monthlyPayment!! > BigDecimal.ZERO)
        }
    }
}