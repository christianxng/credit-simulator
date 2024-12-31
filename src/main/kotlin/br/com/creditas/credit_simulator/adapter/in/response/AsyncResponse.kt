package br.com.creditas.credit_simulator.adapter.`in`.response

import java.util.*

data class AsyncResponse(
    val simulationId: UUID,
    val message: String
)
