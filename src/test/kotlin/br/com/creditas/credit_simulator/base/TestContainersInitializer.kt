package br.com.creditas.credit_simulator.base

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.ClassPathResource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SES
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName
import java.io.IOException

class TestContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        val POSTGRES = PostgreSQLContainer(
            DockerImageName.parse("postgres:16.1")
        )

        val LOCALSTACK: LocalStackContainer = LocalStackContainer(
            DockerImageName.parse("localstack/localstack")
        )


        init {
            Startables.deepStart(POSTGRES, LOCALSTACK).join()
        }
    }

    override fun initialize(ctx: ConfigurableApplicationContext) {
        try {

            val scriptResource = ClassPathResource("localstack/create-resources.sh")
            val scriptContent = scriptResource.inputStream.bufferedReader().use { it.readText() }


            LOCALSTACK.execInContainer("/bin/sh", "-c", scriptContent)

        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        TestPropertyValues.of(
            "spring.datasource.url=${POSTGRES.jdbcUrl}",
            "spring.datasource.username=${POSTGRES.username}",
            "spring.datasource.password=${POSTGRES.password}",
            "spring.cloud.aws.region.static=${LOCALSTACK.region}",
            "spring.cloud.aws.ses.endpoint=${LOCALSTACK.getEndpointOverride(SES)}",
        ).applyTo(ctx.environment)
    }
}