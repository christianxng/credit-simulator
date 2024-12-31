package br.com.creditas.credit_simulator.infrastructure.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JsonMapper {

    private val objectMapper = ObjectMapper()

    fun <T> toJson(obj: T): String {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException("Error converting object to JSON", e)
        }
    }
}