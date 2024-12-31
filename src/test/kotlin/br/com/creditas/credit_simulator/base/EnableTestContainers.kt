package br.com.creditas.credit_simulator.base

import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
annotation class EnableTestContainers