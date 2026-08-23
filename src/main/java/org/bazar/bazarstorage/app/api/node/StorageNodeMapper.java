package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.AuthorInfo;
import org.bazar.bazarstorage.app.api.node.output.AuthorStatus;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;
import org.bazar.bazarstorage.app.api.node.output.NodeErrorInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.app.api.properties.SettingProperties;
import org.bazar.bazarstorage.app.api.properties.SettingProperties.FileValidation.ErrorType;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;
import org.bazar.bazarstorage.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Mapper
public abstract class StorageNodeMapper {
    @Autowired
    protected SettingProperties settingProperties;

    @Mapping(target = "nodeName", source = "command.fileName")
    @Mapping(target = "status", expression = "java(org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus.IN_PROGRESS)")
    @Mapping(target = "type", expression = "java(org.bazar.bazarstorage.domain.storagenode.StorageNodeType.FILE)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "errors", ignore = true)
    public abstract StorageNode toDomain(GetUploadUrlCommand command, InitiateUploadResult uploadUrlInfo, UUID userId);

    @Mapping(target = "fileName", source = "storageNode.nodeName")
    @Mapping(target = "uploadedAt", source = "storageNode.createdAt")
    @Mapping(target = "author", expression = "java(toAuthorInfo(user, status))")
    @Mapping(target = "nodeId", source = "storageNode.id")
    public abstract NodeInfo toNodeInfo(StorageNode storageNode, User user, AuthorStatus status);

    public abstract AuthorInfo toAuthorInfo(User user, AuthorStatus status);

    public NodeInfoPage toNodeInfoPage(Page<NodeInfo> dtoPage) {
        return new NodeInfoPage(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages()
        );
    }

    @Mapping(target = "author", expression = "java(toAuthorInfo(user, authorStatus))")
    @Mapping(target = "errors", ignore = true)
    public abstract FileStatusInfo toFileStatusInfo(String status, User user, AuthorStatus authorStatus);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "errors", ignore = true)
    public abstract FileStatusInfo toFileStatusInfo(String status);

    @Mapping(target = "author", ignore = true)
    public abstract FileStatusInfo toFileStatusInfo(StorageNode storageNode);

    @Mapping(target = "description", source = "storageNodeError.code", qualifiedByName = "mapErrorDescription")
    public abstract NodeErrorInfo toFileStatusError(StorageNodeError storageNodeError);

    public abstract DownloadUrlInfo toDownloadUrlInfo(String downloadUrl);

    public abstract UploadUrlInfo toUploadUrlInfo(InitiateUploadResult initiateUploadResult, String nodeId);

    public abstract StorageNodeError toStorageNodeError(FileUploadResultCommand.Error error);

    @Named("mapErrorDescription")
    protected String mapErrorDescription(String code) {
        return settingProperties.fileValidation().errorMessages().get(ErrorType.fromString(code));
    }
}
