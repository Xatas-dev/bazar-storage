package org.bazar.bazarstorage.adapter.inbound.rest.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetDownloadUrlResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetFileStatusResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetNodesPaginationResponse;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlRequest;
import org.bazar.bazarstorage.adapter.inbound.rest.node.dto.V1GetUploadUrlResponse;
import org.bazar.bazarstorage.app.api.node.MarkNodeForDeletionInbound;
import org.bazar.bazarstorage.app.api.node.GetDownloadUrlInbound;
import org.bazar.bazarstorage.app.api.node.GetFileStatusInbound;
import org.bazar.bazarstorage.app.api.node.GetNodesBySpaceIdInbound;
import org.bazar.bazarstorage.app.api.node.GetUploadUrlInbound;
import org.bazar.bazarstorage.app.api.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.api.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/nodes")
@RequiredArgsConstructor
public class NodeController implements NodeControllerSwagger {
    private final RestNodeMapper restNodeMapper;
    private final GetUploadUrlInbound getUploadUrlInbound;
    private final GetFileStatusInbound getFileStatusInbound;
    private final GetNodesBySpaceIdInbound getNodesBySpaceIdInbound;
    private final GetDownloadUrlInbound getDownloadUrlInbound;
    private final MarkNodeForDeletionInbound markNodeForDeletionInbound;

    @PostMapping
    public V1GetUploadUrlResponse getUploadUrl(@RequestBody V1GetUploadUrlRequest request, @PathVariable String spaceId) {
        GetUploadUrlCommand command = restNodeMapper.toCommand(request, spaceId);
        UploadUrlInfo urlInfo = getUploadUrlInbound.execute(command);
        return restNodeMapper.toResponse(urlInfo);
    }

    @GetMapping("/{nodeId}/status")
    public V1GetFileStatusResponse getFileStatus(@PathVariable String spaceId, @PathVariable String nodeId) {
        FileStatusInfo status = getFileStatusInbound.execute(spaceId, nodeId);
        return restNodeMapper.toResponse(status);
    }

    @GetMapping
    public V1GetNodesPaginationResponse getNodes(@PathVariable String spaceId, @PageableDefault(size = 20) Pageable pageable) {
        GetNodesBySpaceIdCommand command = restNodeMapper.toCommand(spaceId, pageable);
        NodeInfoPage nodeInfoPage = getNodesBySpaceIdInbound.execute(command);
        return restNodeMapper.toResponse(nodeInfoPage);
    }

    @GetMapping("/{nodeId}/download")
    public V1GetDownloadUrlResponse getUrlForDownload(@PathVariable String spaceId, @PathVariable String nodeId) {
        DownloadUrlInfo urlInfo = getDownloadUrlInbound.execute(spaceId, nodeId);
        return restNodeMapper.toResponse(urlInfo);
    }

    @DeleteMapping("/{nodeId}")
    public void deleteNode(@PathVariable String spaceId, @PathVariable String nodeId) {
        markNodeForDeletionInbound.execute(spaceId, nodeId);
    }
}
