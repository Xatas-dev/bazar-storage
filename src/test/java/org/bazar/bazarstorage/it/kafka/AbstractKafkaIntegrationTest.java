package org.bazar.bazarstorage.it.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bazar.bazarstorage.it.AbstractIntegrationTest;
import org.bazar.bazarstorage.it.testutil.TestDataTransformUtil;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbstractKafkaIntegrationTest extends AbstractIntegrationTest {
    protected final static String FILE_EVENTS_TOPIC = "file.events";

    private final ObjectMapper mapper = TestDataTransformUtil.getTestObjectMapper();

    protected Consumer<String, String> getKafkaConsumer(EmbeddedKafkaBroker embeddedKafka, String topicName) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put("bootstrap.servers", embeddedKafka.getBrokersAsString());
        consumerProps.put("group.id", "testGroup");
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);
        consumerProps.put("auto.offset.reset", "earliest");

        DefaultKafkaConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = factory.createConsumer();

        consumer.subscribe(Collections.singletonList(topicName));

        return consumer;
    }

    protected Producer<String, String> getKafkaProducer(EmbeddedKafkaBroker embeddedKafka) {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        return new KafkaProducer<>(
                producerProps,
                new StringSerializer(),
                new StringSerializer()
        );
    }

    protected void sendMessage(Consumer<String, String> consumer, Producer<String, String> producer, String topicName,
                               String key, Object message) throws JsonProcessingException {
        producer.send(new ProducerRecord<>(topicName, key, mapper.writeValueAsString(message)));
        producer.flush();
        producer.close();
        consumer.poll(Duration.ofSeconds(5));
    }
}
