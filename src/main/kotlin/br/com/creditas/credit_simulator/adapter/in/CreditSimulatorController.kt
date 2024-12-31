package br.com.creditas.credit_simulator.adapter.`in`

import br.com.creditas.credit_simulator.adapter.`in`.docs.CreditSimulationApi
import br.com.creditas.credit_simulator.adapter.`in`.request.CreditSimulationRequest
import br.com.creditas.credit_simulator.adapter.`in`.response.AsyncResponse
import br.com.creditas.credit_simulator.adapter.`in`.response.CreditSimulationResponse
import br.com.creditas.credit_simulator.adapter.`in`.response.MultipleAsyncResponse
import br.com.creditas.credit_simulator.application.ports.`in`.CreditSimulatorPort
import br.com.creditas.credit_simulator.infrastructure.utils.Constants.ASYNCHRONOUSLY_PROCESSING_MESSAGE
import jakarta.validation.Valid
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/v1/credit")
class CreditSimulatorController(
    val creditSimulatorPort: CreditSimulatorPort,
    @Value("\${credit.simulator.synchronous.processing.timeout:1000}")
    val synchronousProcessingTimeout: Long
) : CreditSimulationApi {

    @PostMapping(
        path = ["/simulate"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun simulate(
        @RequestBody(required = true) @Valid
        creditSimulationRequest: CreditSimulationRequest
    ): ResponseEntity<Any> {
        val simulationId = UUID.randomUUID()

        // Lança o processamento em uma coroutine separada
        val coroutine = CoroutineScope(Dispatchers.IO).async {
            creditSimulatorPort(
                dateOfBirth = creditSimulationRequest.dateOfBirth!!,
                creditSimulation = creditSimulationRequest.toModel(simulationId = simulationId)
            )
        }

        // Aguarda o resultado com timeout configurado
        return try {
            val result = withTimeout(synchronousProcessingTimeout) {
                coroutine.await()
            }
            ResponseEntity.ok(CreditSimulationResponse(result))
        } catch (e: TimeoutCancellationException) {
            // Continua executando em background
            ResponseEntity.accepted().body(
                AsyncResponse(
                    simulationId = simulationId,
                    message = ASYNCHRONOUSLY_PROCESSING_MESSAGE
                )
            )
        }
    }


    @PostMapping(
        path = ["/simulate-multiple"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun multipleSimulate(
        @RequestBody(required = true) @Valid
        multipleCreditSimulationRequest: List<CreditSimulationRequest>
    ): ResponseEntity<Any> {
        // Gera uma lista de UUIDs, um para cada item na requisição
        val simulationIds = multipleCreditSimulationRequest.map { UUID.randomUUID() }

        // Processa cada item em paralelo, usando os UUIDs gerados
        val coroutines = multipleCreditSimulationRequest.mapIndexed { index, item ->
            CoroutineScope(Dispatchers.IO).async {
                creditSimulatorPort(
                    dateOfBirth = item.dateOfBirth!!,
                    creditSimulation = item.toModel(simulationId = simulationIds[index])
                )
            }
        }

        return try {
            // Aguarda todos os resultados com timeout configurado
            val results = withTimeout(synchronousProcessingTimeout) {
                coroutines.map { coroutine -> CreditSimulationResponse(coroutine.await()) }
            }
            ResponseEntity.ok(results)
        } catch (e: TimeoutCancellationException) {
            // Continua executando em background
            ResponseEntity.accepted().body(
                MultipleAsyncResponse(
                    simulationIds = simulationIds,
                    message = ASYNCHRONOUSLY_PROCESSING_MESSAGE
                )
            )
        }
    }
}