package org.bazar.bazarstorage.adapter.inbound.rest.node;

import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.api.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Pageable;

@Mapper
public interface RestNodeMapper {
    GetUploadUrlCommand toCommand(V1GetUploadUrlRequest request, String spaceId);

    GetNodesBySpaceIdCommand toCommand(String spaceId, Pageable pageable);

    V1GetUploadUrlResponse toResponse(UploadUrlInfo uploadUrlInfo);

    V1GetFileStatusResponse toResponse(FileStatusInfo status);

    V1GetNodesPaginationResponse toResponse(NodeInfoPage nodeInfoPage);

    V1GetDownloadUrlResponse toResponse(DownloadUrlInfo urlInfo);
}
