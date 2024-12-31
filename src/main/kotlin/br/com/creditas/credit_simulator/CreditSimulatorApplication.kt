package br.com.creditas.credit_simulator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreditSimulatorApplication

fun main(args: Array<String>) {
	runApplication<CreditSimulatorApplication>(*args)
}
