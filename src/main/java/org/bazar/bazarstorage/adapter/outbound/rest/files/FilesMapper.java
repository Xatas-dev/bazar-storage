package org.bazar.bazarstorage.adapter.outbound.rest.files;

import org.bazar.bazarstorage.adapter.outbound.rest.files.dto.V1InitiateUploadResponseDto;
import org.bazar.bazarstorage.app.impl.node.output.InitiateUploadResult;
import org.mapstruct.Mapper;

@Mapper
public interface FilesMapper {
    InitiateUploadResult toUploadUrlInfo(V1InitiateUploadResponseDto response);
}
