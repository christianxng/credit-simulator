package br.com.creditas.credit_simulator.adapter.`in`.response

import java.util.*

data class MultipleAsyncResponse(
    val message: String,
    val simulationIds: List<UUID>
)
