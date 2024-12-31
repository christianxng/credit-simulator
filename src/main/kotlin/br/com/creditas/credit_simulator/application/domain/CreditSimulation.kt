package br.com.creditas.credit_simulator.application.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

class CreditSimulation private constructor(
    val simulationId: UUID,
    val presentValue: BigDecimal,
    val numberOfPayments: Int,
    val email: String
) {

    var monthlyPayment: BigDecimal? = null
        private set

    var annualInterestRate: BigDecimal? = null
        private set

    var status: CreditSimulationStatus? = null
        private set

    companion object {
        fun create(
            presentValue: BigDecimal,
            numberOfPayments: Int,
            email: String,
            simulationId: UUID
        ): CreditSimulation = CreditSimulation(
            simulationId = simulationId,
            presentValue = presentValue,
            numberOfPayments = numberOfPayments,
            email = email
        )
    }

    fun isSimulationWithError(): Boolean = this.status == CreditSimulationStatus.ERROR
    fun isSimulationWithSuccess(): Boolean = this.status == CreditSimulationStatus.SUCCESS

    fun process(annualInterestRate: BigDecimal) {
        runCatching {
            this.annualInterestRate = annualInterestRate
            this.monthlyPayment = calculatePMT()
            this.status = CreditSimulationStatus.SUCCESS
        }.onFailure { ex ->
            this.status = CreditSimulationStatus.ERROR
            throw ex
        }
    }


    private fun calculatePMT(): BigDecimal {

        // Calcula a taxa mensal (taxa anual / 12)
        val monthlyRate = this.annualInterestRate!!.divide(BigDecimal("12"), 8, RoundingMode.HALF_EVEN)

        // Calcula (1 + r)^n
        val base = BigDecimal.ONE.add(monthlyRate)
        val exponent = base.pow(this.numberOfPayments)

        // Calcula 1 / (1 + r)^n
        // Convertendo a expressão (1 + r)^-n, pois a função do BigDecimal não aceita expoente negativo
        val powerTerm = BigDecimal.ONE.divide(exponent, 8, RoundingMode.HALF_EVEN)

        // Calcula 1 - (1 / (1 + r)^n)
        val denominator = BigDecimal.ONE.subtract(powerTerm)

        // Calcula PV * r
        val numerator = presentValue.multiply(monthlyRate)

        // Calcula o resultado final: (PV * r) / (1 - (1 / (1 + r)^n))
        return numerator.divide(denominator, 2, RoundingMode.HALF_EVEN)
    }
}