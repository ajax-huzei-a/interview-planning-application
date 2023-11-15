package intellistart.interviewplanning.config

import com.google.protobuf.GeneratedMessageV3
import intellistart.interviewplanning.KafkaTopic
import intellistart.interviewplanning.output.pubsub.slot.SlotUpdatedEvent
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String
) {

    @Bean
    fun kafkaSenderSLotUpdatedEvent(): KafkaSender<String, SlotUpdatedEvent> =
        createKafkaSender(producerProperties())

    @Bean
    fun kafkaReceiverDeviceUpdatedEvent(): KafkaReceiver<String, SlotUpdatedEvent> {
        val customProperties: MutableMap<String, Any> = mutableMapOf(
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to SlotUpdatedEvent::class.java.name
        )
        return createKafkaReceiver(
            consumerProperties(customProperties)
        )
    }

    private fun <T : GeneratedMessageV3> createKafkaReceiver(
        properties: MutableMap<String, Any>
    ): KafkaReceiver<String, T> {
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "slot-group"
        val options =
            ReceiverOptions.create<String, T>(properties).subscription(setOf(KafkaTopic.UPDATED_SLOT_EVENT))
        return KafkaReceiver.create(options)
    }

    private fun consumerProperties(
        customProperties: MutableMap<String, Any> = mutableMapOf()
    ): MutableMap<String, Any> {
        val baseProperties: MutableMap<String, Any> = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )
        baseProperties.putAll(customProperties)
        return baseProperties
    }

    private fun <T : GeneratedMessageV3> createKafkaSender(
        properties: MutableMap<String, Any>
    ): KafkaSender<String, T> =
        KafkaSender.create(SenderOptions.create(properties))

    private fun producerProperties(): MutableMap<String, Any> = mutableMapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
        "schema.registry.url" to schemaRegistryUrl
    )
}
