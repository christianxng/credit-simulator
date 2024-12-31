package br.com.creditas.credit_simulator

import br.com.creditas.credit_simulator.base.TestContainersInitializer
import org.springframework.boot.builder.SpringApplicationBuilder

fun main(args: Array<String>) {
	SpringApplicationBuilder()
		.sources(CreditSimulatorApplication::class.java)
		.initializers(TestContainersInitializer())
		.profiles("test")
		.run(*args)
}
