package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.ProcessFileUploadedResultInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadStatus;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_FILE_UUID;

@Component
@RequiredArgsConstructor
public class ProcessFileUploadedResultUseCase implements ProcessFileUploadedResultInbound {
    private static final Map<FileUploadStatus, StorageNodeStatus> STATUS_MAPPING = Map.of(
            FileUploadStatus.UPLOADED, StorageNodeStatus.UPLOADED,
            FileUploadStatus.ERROR, StorageNodeStatus.ERROR,
            FileUploadStatus.VALIDATION_ERROR, StorageNodeStatus.ERROR
    );

    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeStatusChanger statusChanger;
    private final StorageNodeMapper storageNodeMapper;

    @Override
    @Transactional
    public void execute(FileUploadResultCommand command) {
        String fileUuid = command.fileUuid();
        StorageNode storageNode = storageNodeRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_FILE_UUID, fileUuid));
        storageNode.setSize(command.size());
        if (FileUploadStatus.VALIDATION_ERROR.equals(command.status())) {
            storageNode.setErrors(generateNodeErrors(command));
        }
        statusChanger.changeStatus(storageNode, STATUS_MAPPING.get(command.status()));
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private List<StorageNodeError> generateNodeErrors(FileUploadResultCommand command) {
        if (command.errors() == null || command.errors().isEmpty()) {
            return Collections.emptyList();
        }

        List<StorageNodeError> result = new ArrayList<>();
        for (FileUploadResultCommand.Error error : command.errors()) {
            result.add(storageNodeMapper.toStorageNodeError(error));
        }
        return result;
    }
}
