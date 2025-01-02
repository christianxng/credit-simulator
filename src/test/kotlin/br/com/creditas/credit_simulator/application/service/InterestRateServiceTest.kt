package br.com.creditas.credit_simulator.application.service

import br.com.creditas.credit_simulator.application.domain.InterestRate
import br.com.creditas.credit_simulator.infrastructure.configuration.CreditSimulatorProperties
import br.com.creditas.credit_simulator.infrastructure.exceptions.InvalidMarketIndex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class InterestRateServiceTest {

    private lateinit var creditSimulatorProperties: CreditSimulatorProperties
    private lateinit var interestRateService: InterestRateService

    @BeforeEach
    fun setup() {
        creditSimulatorProperties = CreditSimulatorProperties()
        interestRateService = InterestRateService(creditSimulatorProperties)
    }

    @Test
    fun `deve retornar taxa de juros de 5% para idade menor que 26 anos`() {
        // cenario
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.FIXED
        val yearsOld = 25

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.05"), result.annualInterestRate)
        assertEquals(InterestRate.InterestRateType.FIXED, result.interestRateType)
        assertNull(result.marketIndexName)
        assertNull(result.marketIndexAnnualInterestRate)
        assertNull(result.spread)
    }

    @Test
    fun `deve retornar taxa de juros de 3% para idade entre 26 e 40 anos`() {
        // cenario
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.FIXED
        val yearsOld = 35

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.03"), result.annualInterestRate)
        assertEquals(InterestRate.InterestRateType.FIXED, result.interestRateType)
    }

    @Test
    fun `deve retornar taxa de juros de 2% para idade entre 41 e 60 anos`() {
        // cenario
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.FIXED
        val yearsOld = 50

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.02"), result.annualInterestRate)
        assertEquals(InterestRate.InterestRateType.FIXED, result.interestRateType)
    }

    @Test
    fun `deve retornar taxa de juros de 4% para idade acima de 60 anos`() {
        // cenario
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.FIXED
        val yearsOld = 61

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.04"), result.annualInterestRate)
        assertEquals(InterestRate.InterestRateType.FIXED, result.interestRateType)
    }

    @Test
    fun `deve calcular corretamente a taxa variável para índice SELIC`() {
        // cenario
        setupVariableInterestRate()
        creditSimulatorProperties.marketIndexEnabled = CreditSimulatorProperties.MarketIndexName.SELIC
        val yearsOld = 30 //  yearsOld não importa para taxa variável

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.1575"), result.annualInterestRate) // 0.1375 + 0.02
        assertEquals(InterestRate.InterestRateType.VARIABLE, result.interestRateType)
        assertEquals(InterestRate.MarketIndexName.SELIC, result.marketIndexName)
        assertEquals(BigDecimal("0.1375"), result.marketIndexAnnualInterestRate)
        assertEquals(BigDecimal("0.02"), result.spread)
    }

    @Test
    fun `deve calcular corretamente a taxa variável para índice CDI`() {
        // cenario
        setupVariableInterestRate()
        creditSimulatorProperties.marketIndexEnabled = CreditSimulatorProperties.MarketIndexName.CDI
        val yearsOld = 30

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.1415"), result.annualInterestRate) // 0.1215 + 0.02
        assertEquals(InterestRate.InterestRateType.VARIABLE, result.interestRateType)
        assertEquals(InterestRate.MarketIndexName.CDI, result.marketIndexName)
        assertEquals(BigDecimal("0.1215"), result.marketIndexAnnualInterestRate)
        assertEquals(BigDecimal("0.02"), result.spread)
    }

    @Test
    fun `deve calcular corretamente a taxa variável para índice IPCA`() {
        // cenario
        setupVariableInterestRate()
        creditSimulatorProperties.marketIndexEnabled = CreditSimulatorProperties.MarketIndexName.IPCA
        val yearsOld = 30

        // acao
        val result = interestRateService.invoke(yearsOld)

        // validação
        assertEquals(BigDecimal("0.0239"), result.annualInterestRate) // 0.0039 + 0.02
        assertEquals(InterestRate.InterestRateType.VARIABLE, result.interestRateType)
        assertEquals(InterestRate.MarketIndexName.IPCA, result.marketIndexName)
        assertEquals(BigDecimal("0.0039"), result.marketIndexAnnualInterestRate)
        assertEquals(BigDecimal("0.02"), result.spread)
    }

    @Test
    fun `deve lançar IllegalStateException quando índice de mercado não for encontrado`() {
        // cenario
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.VARIABLE
        creditSimulatorProperties.marketIndexEnabled = CreditSimulatorProperties.MarketIndexName.SELIC
        creditSimulatorProperties.marketIndex = emptyList()

        // acao/resultado
        assertThrows<InvalidMarketIndex> {
            interestRateService.invoke(30)
        }.also { exception ->
            assertEquals("Market index SELIC not found in the configured list.", exception.message)
        }
    }

    private fun setupVariableInterestRate() {
        creditSimulatorProperties.interestRateType = CreditSimulatorProperties.InterestRateType.VARIABLE
        creditSimulatorProperties.marketIndex = listOf(
            CreditSimulatorProperties.MarketIndex(
                name = CreditSimulatorProperties.MarketIndexName.SELIC,
                spread = BigDecimal("0.02"),
                interestRate = BigDecimal("0.1375")
            ),
            CreditSimulatorProperties.MarketIndex(
                name = CreditSimulatorProperties.MarketIndexName.CDI,
                spread = BigDecimal("0.02"),
                interestRate = BigDecimal("0.1215")
            ),
            CreditSimulatorProperties.MarketIndex(
                name = CreditSimulatorProperties.MarketIndexName.IPCA,
                spread = BigDecimal("0.02"),
                interestRate = BigDecimal("0.0039")
            )
        )
    }
}