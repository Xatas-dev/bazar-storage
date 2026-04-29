package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.ProcessFileUploadedResultInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.impl.helper.StorageNodeStatusChanger;
import org.bazar.bazarstorage.app.impl.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.impl.node.commands.FileUploadStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_FILE_UUID;

@Component
@RequiredArgsConstructor
public class ProcessFileUploadedResultUseCase implements ProcessFileUploadedResultInbound {
    private static final Map<FileUploadStatus, StorageNodeStatus> STATUS_MAPPING = Map.of(
            FileUploadStatus.UPLOADED, StorageNodeStatus.UPLOADED,
            FileUploadStatus.ERROR, StorageNodeStatus.ERROR
    );

    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeStatusChanger statusChanger;

    @Override
    @Transactional
    public void execute(FileUploadResultCommand command) {
        String fileUuid = command.fileUuid();
        StorageNode storageNode = storageNodeRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_FILE_UUID, fileUuid));
        storageNode.setSize(command.size());
        statusChanger.changeStatus(storageNode, STATUS_MAPPING.get(command.status()));
    }
}
