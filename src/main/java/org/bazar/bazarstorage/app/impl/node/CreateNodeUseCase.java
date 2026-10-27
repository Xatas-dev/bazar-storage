package org.bazar.bazarstorage.app.impl.node;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.auth.AuthenticationService;
import org.bazar.bazarstorage.app.api.auth.Authorize;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.node.CreateNodeInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.api.node.commands.CreateNodeCommand;
import org.bazar.bazarstorage.app.api.node.exception.NodeValidationException;
import org.bazar.bazarstorage.app.api.node.output.CreatedNodeInfo;
import org.bazar.bazarstorage.app.impl.validator.CreateFileNodeValidator;
import org.bazar.bazarstorage.app.impl.validator.CreateFolderNodeValidator;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.bazar.authorization.sdk.Permission.STORAGE_NODE_UPLOAD;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateNodeUseCase implements CreateNodeInbound {
    private final CreateFileNodeValidator createFileNodeValidator;
    private final CreateFolderNodeValidator createFolderNodeValidator;
    private final StorageNodeRepository storageNodeRepository;
    private final StorageNodeMapper storageNodeMapper;
    private final AuthenticationService authenticationService;

    @Override
    @Authorize(spaceIdParam = "#command.spaceId", permission = STORAGE_NODE_UPLOAD) // TODO: выделить в auth отдельный пермишен на создание узла или переименовать текущий
    @Transactional
    public CreatedNodeInfo execute(CreateNodeCommand command) {
        StorageNode parentNode = getParentNode(command.parentId());
        List<StorageNodeError> validationErrors = validateCommand(command, parentNode);
        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            throw new NodeValidationException(validationErrors);
        }

        UUID userId = authenticationService.getAuthenticatedUserId();
        StorageNode storageNode = storageNodeMapper.toDomain(command, parentNode, userId);
        storageNodeRepository.save(storageNode);

        return new CreatedNodeInfo(storageNode.getId().toString());
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private StorageNode getParentNode(Long parentId) {
        if (parentId == null) {
            return null;
        }

        return storageNodeRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID, parentId));
    }

    private List<StorageNodeError> validateCommand(CreateNodeCommand command, StorageNode parentNode) {
        return switch (command.type()) {
            case FILE -> createFileNodeValidator.validate(command, parentNode);
            case FOLDER -> createFolderNodeValidator.validate(command, parentNode);
        };
    }
}
