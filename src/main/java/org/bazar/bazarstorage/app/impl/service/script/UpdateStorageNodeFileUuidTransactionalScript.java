package org.bazar.bazarstorage.app.impl.service.script;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateStorageNodeFileUuidTransactionalScript {
    private final StorageNodeRepository storageNodeRepository;

    @Transactional
    public void execute(Long nodeId, UUID fileUuid) {
        int updatedRows = storageNodeRepository.updateFileUuid(nodeId, fileUuid);

        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID, nodeId);
        }
    }
}
