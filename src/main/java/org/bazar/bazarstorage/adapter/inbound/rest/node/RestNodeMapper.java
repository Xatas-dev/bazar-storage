package org.bazar.bazarstorage.adapter.inbound.rest.node;

import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1CreateNodeResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.api.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.api.node.commands.CreateNodeCommand;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.api.node.output.CreatedNodeInfo;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

@Mapper
public interface RestNodeMapper {
    CreateNodeCommand toCommand(V1CreateNodeRequest request, String spaceId);

    GetUploadUrlCommand toCommand(String spaceId, String nodeId);

    GetNodesBySpaceIdCommand toCommand(String spaceId, Pageable pageable);

    V1CreateNodeResponse toResponse(CreatedNodeInfo createdNodeInfo);

    V1GetUploadUrlResponse toResponse(UploadUrlInfo createdNodeInfo);

    V1GetFileStatusResponse toResponse(FileStatusInfo status);

    V1GetNodesPaginationResponse toResponse(NodeInfoPage nodeInfoPage);

    V1GetDownloadUrlResponse toResponse(DownloadUrlInfo urlInfo);
}
