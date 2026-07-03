package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.AuthorInfo;
import org.bazar.bazarstorage.app.api.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;
import org.bazar.bazarstorage.app.api.node.output.NodeInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
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
    StorageNode toDomain(GetUploadUrlCommand command, InitiateUploadResult uploadUrlInfo, UUID userId);

    @Mapping(target = "fileName", source = "storageNode.nodeName")
    @Mapping(target = "uploadedAt", source = "storageNode.createdAt")
    @Mapping(target = "author", expression = "java(toAuthorInfo(user, status))")
    @Mapping(target = "nodeId", source = "storageNode.id")
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

    @Mapping(target = "author", expression = "java(toAuthorInfo(user, authorStatus))")
    FileStatusInfo toFileStatusInfo(String status, User user, AuthorStatus authorStatus);

    @Mapping(target = "author", ignore = true)
    FileStatusInfo toFileStatusInfo(String status);

    DownloadUrlInfo toDownloadUrlInfo(String downloadUrl);

    UploadUrlInfo toUploadUrlInfo(InitiateUploadResult initiateUploadResult, String nodeId);
}
