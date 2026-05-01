package org.bazar.bazarstorage.adapter.outbound.rest.files;

import org.bazar.bazarstorage.adapter.outbound.rest.files.dto.V1InitiateDownloadResponseDto;
import org.bazar.bazarstorage.adapter.outbound.rest.files.dto.V1InitiateUploadResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bazar-files", url = "${service.bazar-files.url}")
public interface FilesFeignClient {
    @GetMapping("/v1/files/initiate-upload")
    V1InitiateUploadResponseDto initiateUpload(@RequestParam String fileName,
                                               @RequestParam Long size,
                                               @RequestParam String contentType,
                                               @RequestParam String domain);

    @GetMapping("/v1/files/initiate-download")
    V1InitiateDownloadResponseDto initiateDownload(@RequestParam String fileUuid);
}
