package org.bazar.bazarstorage.adapter.inbound.rest.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController implements NodeControllerSwagger {
    private final RestNodeMapper restNodeMapper;
    private final GetUploadUrlInbound getUploadUrlInbound;
    private final GetFileStatusInbound getFileStatusInbound;

    @GetMapping("/upload-url")
    public V1GetUploadUrlResponse getUploadUrl(@ModelAttribute V1GetUploadUrlRequest request) {
        GetUploadUrlCommand command = restNodeMapper.toCommand(request);
        UploadUrlInfo urlInfo = getUploadUrlInbound.execute(command);
        return restNodeMapper.toResponse(urlInfo);
    }

    @GetMapping("/status")
    public V1GetFileStatusResponse getFileStatus(@RequestParam String fileUuid) {
        String status = getFileStatusInbound.execute(fileUuid);
        return restNodeMapper.toResponse(status);
    }
}
