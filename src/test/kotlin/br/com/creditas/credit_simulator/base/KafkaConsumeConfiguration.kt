//package br.com.creditas.credit_simulator.base
//
//import br.com.creditas.credit_simulator.base.TestContainersInitializer.Companion.KAFKA
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.KafkaConsumer
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.annotation.EnableKafka
//
//@EnableKafka
//@Configuration
//class KafkaConsumeConfiguration {
//
//    @Value("\${spring.kafka.template.default-topic}")
//    lateinit var topic: String
//
//    @Bean
//    fun kafkaConsumer(): KafkaConsumer<String, String> {
//        val props = mapOf(
//            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to KAFKA.bootstrapServers,
//            ConsumerConfig.GROUP_ID_CONFIG to "my-group-id",
//            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
//        )
//        return KafkaConsumer<String, String>(props).apply {
//            subscribe(listOf(topic))
//        }
//    }
//}