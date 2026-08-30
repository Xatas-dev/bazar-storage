package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.ProcessFileUploadedResultInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadStatus;
import org.bazar.bazarstorage.app.api.validator.StorageNodeValidator;
import org.bazar.bazarstorage.app.impl.helper.FilesHelper;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final StorageNodeValidator storageNodeValidator;

    @Override
    @Transactional
    public void execute(FileUploadResultCommand command) {
        StorageNode storageNode = findStorageNode(command.fileUuid());
        setBasicInfo(storageNode, command);

        List<StorageNodeError> errors = determineErrors(command);

        if (!errors.isEmpty()) {
            storageNode.setErrors(errors);
            statusChanger.changeStatus(storageNode, StorageNodeStatus.ERROR);
        } else {
            statusChanger.changeStatus(storageNode, STATUS_MAPPING.get(command.status()));
        }
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private StorageNode findStorageNode(String fileUuid) {
        return storageNodeRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_FILE_UUID, fileUuid));
    }

    private void setBasicInfo(StorageNode storageNode, FileUploadResultCommand command) {
        storageNode.setSize(command.size());
        storageNode.setNodeName(command.fileName());
    }

    private List<StorageNodeError> determineErrors(FileUploadResultCommand command) {
        if (FileUploadStatus.VALIDATION_ERROR.equals(command.status())) {
            return generateNodeErrors(command);
        }
        return storageNodeValidator.validate(storageNodeMapper.toFileValidationRequest(command,
                FilesHelper.getExtensionFromContentType(command.contentType())));
    }

    private List<StorageNodeError> generateNodeErrors(FileUploadResultCommand command) {
        return Optional.ofNullable(command.errors())
                .map(errors -> errors.stream()
                        .map(storageNodeMapper::toStorageNodeError)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
