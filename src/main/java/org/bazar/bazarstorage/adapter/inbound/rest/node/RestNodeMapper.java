package org.bazar.bazarstorage.adapter.inbound.rest.node;

import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.mapstruct.Mapper;

@Mapper
public interface RestNodeMapper {
    GetUploadUrlCommand toCommand(V1GetUploadUrlRequest request);

    V1GetUploadUrlResponse toResponse(UploadUrlInfo uploadUrlInfo);
}
