package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_FILE_UUID;

@Component
@RequiredArgsConstructor
public class GetFileStatusUseCase implements GetFileStatusInbound {
    private final StorageNodeRepository storageNodeRepository;

    @Override
    public String execute(String fileUuid) {
        StorageNode storageNode = storageNodeRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_FILE_UUID, fileUuid));
        return storageNode.getStatus().name();
    }
}
