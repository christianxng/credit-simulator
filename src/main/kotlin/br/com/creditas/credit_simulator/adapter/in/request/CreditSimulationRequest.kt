package br.com.creditas.credit_simulator.adapter.`in`.request

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreditSimulationRequest(
    @field:NotNull @field:Positive val presentValue: BigDecimal?,
    @field:NotNull @field:Past val dateOfBirth: LocalDate?,
    @field:NotNull @field:Min(value = 1) val numberOfPayments: Int?,
    @field:NotBlank @field:Email val email: String?,
){
    fun toModel(simulationId: UUID): CreditSimulation {
        return CreditSimulation.create(
            simulationId = simulationId,
            email = email!!,
            numberOfPayments = numberOfPayments!!,
            presentValue= presentValue!!
        )
    }
}