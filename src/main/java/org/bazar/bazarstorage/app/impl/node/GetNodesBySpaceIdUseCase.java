package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.node.GetNodesBySpaceIdInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.impl.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.impl.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfo;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.service.UserLoader;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetNodesBySpaceIdUseCase implements GetNodesBySpaceIdInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final UserLoader userLoader;
    private final StorageNodeMapper storageNodeMapper;

    @Override
    public NodeInfoPage execute(GetNodesBySpaceIdCommand command) {
        Page<StorageNode> storageNodes =
                storageNodeRepository.findBySpaceIdAndStatus(command.spaceId(), StorageNodeStatus.UPLOADED, command.pageable());
        Map<UUID, User> usersMap = userLoader.loadUsers(storageNodes.getContent());
        Page<NodeInfo> dtoPage = storageNodes.map(storageNode -> {
                    User user = usersMap.get(storageNode.getUserId());
                    AuthorStatus authorStatus = AuthorStatus.from(user);

                    return storageNodeMapper.toNodeInfo(storageNode, user, authorStatus);
                }
        );

        return storageNodeMapper.toNodeInfoPage(dtoPage);
    }
}
