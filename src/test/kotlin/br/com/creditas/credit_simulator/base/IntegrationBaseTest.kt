package br.com.creditas.credit_simulator.base

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ActiveProfiles("test")
@EnableTestContainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@EmbeddedKafka(
    topics = ["credit-simulator-topic"],
    partitions = 1,
    bootstrapServersProperty = "spring.kafka.producer.bootstrap-servers",
)
abstract class IntegrationBaseTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var embeddedKafka: EmbeddedKafkaBroker

    protected val topic: String = "credit-simulator-topic"


    protected fun <T> toJson(target: T): String =
        mapper.writeValueAsString(target)


    protected fun <V> createConsumer(classType: Class<V>): Consumer<String, V> {
        val consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka)
        consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java

        val consumerFactory: DefaultKafkaConsumerFactory<String, V> = DefaultKafkaConsumerFactory(
            consumerProps,
            StringDeserializer(),
            JsonDeserializer(classType)
        )

        return consumerFactory.createConsumer()
    }
}