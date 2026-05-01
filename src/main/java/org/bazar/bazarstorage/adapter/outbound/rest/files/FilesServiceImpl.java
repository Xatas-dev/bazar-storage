package org.bazar.bazarstorage.adapter.outbound.rest.files;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.adapter.outbound.rest.files.dto.V1InitiateUploadResponseDto;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilesServiceImpl implements FilesService {
    private static final String DOMAIN = "STORAGE";

    private final FilesFeignClient feignClient;
    private final FilesMapper mapper;

    @Override
    public UploadUrlInfo initiateUpload(GetUploadUrlCommand command) {
        try {
            MediaType contentType = MediaTypeFactory.getMediaType(command.fileName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
            V1InitiateUploadResponseDto response = feignClient.initiateUpload(
                    command.fileName(),
                    command.size(),
                    contentType.toString(),
                    DOMAIN
            );
            return mapper.toUploadUrlInfo(response);
        } catch (FeignException e) {
            log.error("Error while calling files service to initiate upload: status {}, message {}", e.status(), e.getMessage());
            throw new BusinessException(ErrorCode.TECH_ERROR);
        }
    }

    @Override
    public String initiateDownload(String fileUuid) {
        try {
            return feignClient.initiateDownload(fileUuid).downloadUrl();
        } catch (FeignException e) {
            log.error("Error while calling files service to initiate download: status {}, message {}", e.status(), e.getMessage());
            throw new BusinessException(ErrorCode.TECH_ERROR);
        }
    }
}
