package org.bazar.bazarstorage.adapter.inbound.rest.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.GetNodesBySpaceIdInbound;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.impl.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController implements NodeControllerSwagger {
    private final RestNodeMapper restNodeMapper;
    private final GetUploadUrlInbound getUploadUrlInbound;
    private final GetFileStatusInbound getFileStatusInbound;
    private final GetNodesBySpaceIdInbound getNodesBySpaceIdInbound;

    @GetMapping("/nodes/upload-url")
    public V1GetUploadUrlResponse getUploadUrl(@ModelAttribute V1GetUploadUrlRequest request) {
        GetUploadUrlCommand command = restNodeMapper.toCommand(request);
        UploadUrlInfo urlInfo = getUploadUrlInbound.execute(command);
        return restNodeMapper.toResponse(urlInfo);
    }

    @GetMapping("/nodes/status")
    public V1GetFileStatusResponse getFileStatus(@RequestParam String fileUuid) {
        String status = getFileStatusInbound.execute(fileUuid);
        return restNodeMapper.toResponse(status);
    }

    @GetMapping("/spaces/{spaceId}/nodes")
    public V1GetNodesPaginationResponse getNodes(@PathVariable String spaceId, @PageableDefault(size = 20) Pageable pageable) {
        GetNodesBySpaceIdCommand command = restNodeMapper.toCommand(spaceId, pageable);
        NodeInfoPage nodeInfoPage = getNodesBySpaceIdInbound.execute(command);
        return restNodeMapper.toResponse(nodeInfoPage);
    }
}
