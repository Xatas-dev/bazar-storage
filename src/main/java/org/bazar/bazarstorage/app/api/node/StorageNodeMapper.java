package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
