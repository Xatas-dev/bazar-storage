package org.bazar.bazarstorage.adapter.inbound.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.node.ProcessFileUploadedResultInbound;
import org.bazar.bazarstorage.app.impl.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.impl.node.commands.FileUploadStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileEventsConsumer {
    private final static String STORAGE_DOMAIN = "STORAGE";

    private final ObjectMapper mapper;
    private final ProcessFileUploadedResultInbound processFileUploadedResultInbound;

    @KafkaListener(topics = "${kafka.file-events.topic}")
    public void accept(String message) {
        log.info("Received message: {} in [file-events] topic", message);
        FileUploadResultCommand command = mapper.readValue(message, FileUploadResultCommand.class);

        if (!STORAGE_DOMAIN.equals(command.domain())) {
            log.warn("Received message with unsupported domain: {}", command.domain());
            return;
        }

        if (command.status() == FileUploadStatus.UNKNOWN) {
            log.error("Received unsupported file upload status");
            return;
        }

        processFileUploadedResultInbound.execute(command);
    }
}
