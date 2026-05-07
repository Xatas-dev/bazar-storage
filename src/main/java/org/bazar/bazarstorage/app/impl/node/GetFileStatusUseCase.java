package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.persona.PersonaService;
import org.bazar.bazarstorage.app.impl.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.impl.node.output.FileStatusInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.user.User;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_FILE_UUID;

@Component
@RequiredArgsConstructor
public class GetFileStatusUseCase implements GetFileStatusInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeMapper storageNodeMapper;
    private final PersonaService personaService;

    @Override
    public FileStatusInfo execute(String fileUuid) {
        StorageNode storageNode = storageNodeRepository.findByFileUuid(fileUuid)
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_FILE_UUID, fileUuid));
        User user = personaService.getUserById(storageNode.getUserId()).orElse(null);
        return storageNodeMapper.toFileStatusInfo(storageNode.getStatus().name(), user, AuthorStatus.from(user));
    }
}
