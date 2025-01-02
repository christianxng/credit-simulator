package br.com.creditas.credit_simulator.adapter.`in`.docs

import br.com.creditas.credit_simulator.adapter.`in`.request.CreditSimulationRequest
import br.com.creditas.credit_simulator.adapter.`in`.response.AsyncResponse
import br.com.creditas.credit_simulator.adapter.`in`.response.CreditSimulationResponse
import br.com.creditas.credit_simulator.adapter.`in`.response.MultipleAsyncResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity

@Tag(name = "Credit Simulation", description = "APIs to credit simulate")
interface CreditSimulationApi {
    @Operation(
        summary = "Simple Credit Simulation",
        description = "Initiate a credit simulation process"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Simulation completed successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreditSimulationResponse::class),
                    examples = [
                        ExampleObject(
                            name = "SimulationSuccessExample",
                            summary = "Successful simulation example",
                            value = """
                            {
                                "simulationId": "45de8d90-f2a2-4238-bee8-e729ca928572",
                                "presentValue": 100000.00,
                                "numberOfPayments": 50,
                                "interestRate": {
                                    "interestRateType": "FIXED",
                                    "annualInterestRate": 0.03
                                },
                                "monthlyPayment": 2130.10
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "202",
            description = "Simulation is being processed asynchronously",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AsyncResponse::class),
                    examples = [
                        ExampleObject(
                            name = "AsyncProcessingExample",
                            summary = "Asynchronous processing example",
                            value = """
                            {
                                "simulationId": "a3f8c056-2fbe-41a5-9fbe-034d3b56ab6b",
                                "message": "Request is processing and will finish asynchronously"
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data, with field constraint violations",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ConstraintViolationResponse::class),
                    examples = [
                        ExampleObject(
                            name = "ValidationErrorExample",
                            summary = "Validation error example",
                            value = """
                            {
                                "type": "https://zalando.github.io/problem/constraint-violation",
                                "status": 400,
                                "violations": [
                                    { "field": "dateOfBirth", "message": "não deve ser nulo" },
                                    { "field": "email", "message": "não deve estar em branco" },
                                    { "field": "numberOfPayments", "message": "não deve ser nulo" },
                                    { "field": "presentValue", "message": "não deve ser nulo" }
                                ],
                                "title": "Constraint Violation"
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = InternalServerErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "ServerErrorExample",
                            summary = "Internal server error example",
                            value = """
                            {
                                "title": "Internal Server Error",
                                "status": 500,
                                "detail": "An unexpected error occurred. Please contact the system administrator."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    @RequestBody(
        description = "Request to simulate credit",
        required = true,
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CreditSimulationRequest::class),
                examples = [
                    ExampleObject(
                        name = "CreditSimulationRequestExample",
                        summary = "Example of a credit simulation request",
                        value = """
                        {
                            "presentValue": 100000.00,
                            "dateOfBirth": "1980-01-01",
                            "numberOfPayments": 50,
                            "email": "example@example.com"
                        }
                        """
                    )
                ]
            )
        ]
    )
    suspend fun simulate(
        @RequestBody(required = true) @Valid creditSimulationRequest: CreditSimulationRequest
    ): ResponseEntity<Any>


    @Operation(
        summary = "Multiple Credit Simulation",
        description = "Initiate a multiple credit simulation process"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Multiple credit simulation completed successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreditSimulationResponse::class),
                    examples = [
                        ExampleObject(
                            name = "MultipleCreditSimulationSuccessExample",
                            summary = "Successful simulation example",
                            value = """
                            [
                                {
                                    "simulationId": "5521a13e-da8d-45b3-ab62-f624cc21e490",
                                    "presentValue": 100000.00,
                                    "numberOfPayments": 50,
                                    "interestRate": {
                                        "interestRateType": "FIXED",
                                        "annualInterestRate": 0.03
                                    },
                                    "monthlyPayment": 2130.10
                                },
                                {
                                    "simulationId": "12b5b444-59d4-4ca8-841f-8a680898faa0",
                                    "presentValue": 100001.00,
                                    "numberOfPayments": 50,
                                    "interestRate": {
                                        "interestRateType": "FIXED",
                                        "annualInterestRate": 0.03
                                    },
                                    "monthlyPayment": 2130.12
                                }
                            ]
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "202",
            description = "Simulation is being processed asynchronously",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MultipleAsyncResponse::class),
                    examples = [
                        ExampleObject(
                            name = "AsyncProcessingExample",
                            summary = "Asynchronous processing example",
                            value = """
                            {
                                "simulationIds":
                                 [
                                    "ebaa63d1-bd49-450f-8ad5-e5f5c09ffda4",
                                    "5c44ccf4-d9d5-4323-bd1e-f1047f17d881"
                                 ],
                                "message": "Request is processing and will finish asynchronously"
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request data, with field constraint violations",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ConstraintViolationResponse::class),
                    examples = [
                        ExampleObject(
                            name = "ValidationErrorExample",
                            summary = "Validation error example",
                            value = """
                            {
                                "type": "https://zalando.github.io/problem/constraint-violation",
                                "status": 400,
                                "violations": [
                                    { "field": "dateOfBirth", "message": "não deve ser nulo" },
                                    { "field": "email", "message": "não deve estar em branco" },
                                    { "field": "numberOfPayments", "message": "não deve ser nulo" },
                                    { "field": "presentValue", "message": "não deve ser nulo" }
                                ],
                                "title": "Constraint Violation"
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = InternalServerErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "ServerErrorExample",
                            summary = "Internal server error example",
                            value = """
                            {
                                "title": "Internal Server Error",
                                "status": 500,
                                "detail": "An unexpected error occurred. Please contact the system administrator."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    @RequestBody(
        description = "Request to multiple simulate credit",
        required = true,
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = CreditSimulationRequest::class),
                examples = [
                    ExampleObject(
                        name = "CreditSimulationRequestExample",
                        summary = "Example of a credit simulation request",
                        value = """
                        [
                            {
                                "presentValue": 100000.00,
                                "dateOfBirth": "1996-03-23",
                                "numberOfPayments": 50,
                                "email": "teste@teste.com"
                            },
                            {
                                "presentValue": 100001.00,
                                "dateOfBirth": "1996-03-23",
                                "numberOfPayments": 50,
                                "email": "teste@teste.com"
                            }
                        ]
                        """
                    )
                ]
            )
        ]
    )
    suspend fun multipleSimulate(
        @RequestBody(required = true) @Valid  multipleCreditSimulationRequest: List<CreditSimulationRequest>
    ): ResponseEntity<Any>

}

data class ConstraintViolationResponse(
    val type: String = "https://zalando.github.io/problem/constraint-violation",
    val status: Int,
    val violations: List<Violation>,
    val title: String = "Constraint Violation"
) {
    data class Violation(
        val field: String,
        val message: String
    )
}

data class InternalServerErrorResponse(
    val title: String = "Internal Server Error",
    val status: Int = 500,
    val detail: String = "An unexpected error occurred. Please contact the system administrator."
)
