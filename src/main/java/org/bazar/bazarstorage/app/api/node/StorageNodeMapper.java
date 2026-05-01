package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.AuthorInfo;
import org.bazar.bazarstorage.app.impl.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.impl.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.impl.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfo;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Mapper
public interface StorageNodeMapper {
    @Mapping(target = "nodeName", source = "command.fileName")
    @Mapping(target = "status", expression = "java(org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus.IN_PROGRESS)")
    @Mapping(target = "type", expression = "java(org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FILE)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    StorageNode toDomain(GetUploadUrlCommand command, UploadUrlInfo uploadUrlInfo, UUID userId);

    @Mapping(target = "fileName", source = "storageNode.nodeName")
    @Mapping(target = "uploadedAt", source = "storageNode.createdAt")
    @Mapping(target = "author", expression = "java(toAuthorInfo(user, status))")
    NodeInfo toNodeInfo(StorageNode storageNode, User user, AuthorStatus status);

    AuthorInfo toAuthorInfo(User user, AuthorStatus status);

    default NodeInfoPage toNodeInfoPage(Page<NodeInfo> dtoPage) {
        return new NodeInfoPage(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages()
        );
    }

    FileStatusInfo toFileStatusInfo(String status);

    DownloadUrlInfo toDownloadUrlInfo(String downloadUrl);
}
