package org.bazar.bazarstorage.adapter.outbound.rest.files;

import org.bazar.bazarstorage.adapter.outbound.rest.files.dto.V1InitiateUploadResponseDto;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.mapstruct.Mapper;

@Mapper
public interface FilesMapper {
    UploadUrlInfo toUploadUrlInfo(V1InitiateUploadResponseDto response);
}
