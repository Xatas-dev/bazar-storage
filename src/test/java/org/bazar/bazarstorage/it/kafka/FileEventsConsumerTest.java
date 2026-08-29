package org.bazar.bazarstorage.it.kafka;

import builder.FileUploadResultCommandBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.bazar.bazarstorage.it.kafka.AbstractKafkaIntegrationTest.FILE_EVENTS_TOPIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EmbeddedKafka(partitions = 1, topics = {FILE_EVENTS_TOPIC})
public class FileEventsConsumerTest extends AbstractKafkaIntegrationTest {
    private final static Long BIG_SIZE = 1000000000L;
    private final static String LONG_FILE_NAME = "name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1name1.txt";
    private final static String INVALID_CONTENT_TYPE = "application/bat";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private Consumer<String, String> kafkaConsumer;
    private Producer<String, String> kafkaProducer;

    @BeforeEach
    void setUp() {
        kafkaConsumer = getKafkaConsumer(embeddedKafka, FILE_EVENTS_TOPIC);
        kafkaProducer = getKafkaProducer(embeddedKafka);
    }

    @Test
    @DisplayName("Обработка успешного статуса файла")
    void handle_successFile() throws JsonProcessingException {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(
                UUID.fromString(FileUploadResultCommandBuilder.DEFAULT_FILE_UUID), StorageNodeStatus.IN_PROGRESS);

        sendMessage(kafkaConsumer, kafkaProducer, FILE_EVENTS_TOPIC, "", FileUploadResultCommandBuilder.buildDefault());

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> {
                    StorageNode storageNodeResult =
                            storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).get();
                    return StorageNodeStatus.UPLOADED == storageNodeResult.getStatus();
                });
        StorageNode result = storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).orElseThrow();
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_SIZE, result.getSize());
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_FILE_NAME, result.getNodeName());
    }

    @Test
    @DisplayName("Обработка файла с ошибочным статусом")
    void handle_errorFile() throws JsonProcessingException {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(
                UUID.fromString(FileUploadResultCommandBuilder.DEFAULT_FILE_UUID), StorageNodeStatus.IN_PROGRESS);

        sendMessage(kafkaConsumer, kafkaProducer, FILE_EVENTS_TOPIC, "",
                FileUploadResultCommandBuilder.buildWith(FileUploadStatus.ERROR));

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> {
                    StorageNode storageNodeResult =
                            storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).get();
                    return StorageNodeStatus.ERROR == storageNodeResult.getStatus();
                });
        StorageNode result = storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).orElseThrow();
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_SIZE, result.getSize());
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_FILE_NAME, result.getNodeName());
    }

    @Test
    @DisplayName("Обработка файла с валидационными ошибками")
    void handle_validationErrorsFile() throws JsonProcessingException {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(
                UUID.fromString(FileUploadResultCommandBuilder.DEFAULT_FILE_UUID), StorageNodeStatus.IN_PROGRESS);

        sendMessage(kafkaConsumer, kafkaProducer, FILE_EVENTS_TOPIC, "",
                FileUploadResultCommandBuilder.buildValidationErrorMessage(List.of("FILE_TOO_LARGE", "FILE_NAME_TOO_LARGE", "random_thing")));

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid())
                        .map(StorageNode::getStatus)
                        .orElse(null) == StorageNodeStatus.ERROR);
        StorageNode result = storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).orElseThrow();
        assertEquals(3, result.getErrors().size());
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_SIZE, result.getSize());
        assertEquals(FileUploadResultCommandBuilder.DEFAULT_FILE_NAME, result.getNodeName());
    }

    @Test
    @DisplayName("Обработка файла с валидационными ошибками на стороне bazar-storage")
    void handle_bazarStorageValidationErrors() throws JsonProcessingException {
        StorageNode storageNode = testDataHelper.createStorageNodeWith(
                UUID.fromString(FileUploadResultCommandBuilder.DEFAULT_FILE_UUID), StorageNodeStatus.IN_PROGRESS);

        sendMessage(kafkaConsumer, kafkaProducer, FILE_EVENTS_TOPIC, "",
                FileUploadResultCommandBuilder.buildWith(BIG_SIZE, LONG_FILE_NAME, INVALID_CONTENT_TYPE));

        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .until(() -> storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid())
                        .map(StorageNode::getStatus)
                        .orElse(null) == StorageNodeStatus.ERROR);
        StorageNode result = storageNodeJpaRepository.findByFileUuid(storageNode.getFileUuid()).orElseThrow();
        assertEquals(3, result.getErrors().size());
        assertEquals(BIG_SIZE, result.getSize());
        assertEquals(LONG_FILE_NAME, result.getNodeName());
    }
}
