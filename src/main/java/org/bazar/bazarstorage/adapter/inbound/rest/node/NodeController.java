package org.bazar.bazarstorage.adapter.inbound.rest.node;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.Permission;
import org.bazar.bazarstorage.adapter.inbound.rest.aop.Authorize;
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
import org.bazar.bazarstorage.app.impl.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.app.impl.node.output.FileStatusInfo;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfoPage;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;
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
    @Authorize(permission = Permission.NODES_UPLOAD)
    public V1GetUploadUrlResponse getUploadUrl(@RequestBody V1GetUploadUrlRequest request, @PathVariable String spaceId) {
        GetUploadUrlCommand command = restNodeMapper.toCommand(request, spaceId);
        UploadUrlInfo urlInfo = getUploadUrlInbound.execute(command);
        return restNodeMapper.toResponse(urlInfo);
    }

    // TODO: продумать, нужен ли в методе spaceId, если nodeId уникальный. Решение или изменения в коде делать в рамках задачи: https://grinbog015.atlassian.net/browse/BZR-112
    @GetMapping("/{nodeId}/status")
    public V1GetFileStatusResponse getFileStatus(@PathVariable String spaceId, @PathVariable String nodeId) {
        FileStatusInfo status = getFileStatusInbound.execute(nodeId);
        return restNodeMapper.toResponse(status);
    }

    @GetMapping
    public V1GetNodesPaginationResponse getNodes(@PathVariable String spaceId, @PageableDefault(size = 20) Pageable pageable) {
        GetNodesBySpaceIdCommand command = restNodeMapper.toCommand(spaceId, pageable);
        NodeInfoPage nodeInfoPage = getNodesBySpaceIdInbound.execute(command);
        return restNodeMapper.toResponse(nodeInfoPage);
    }

    @GetMapping("/{nodeId}/download")
    @Authorize(permission = Permission.NODES_DOWNLOAD)
    public V1GetDownloadUrlResponse getUrlForDownload(@PathVariable String spaceId, @PathVariable String nodeId) {
        DownloadUrlInfo urlInfo = getDownloadUrlInbound.execute(nodeId);
        return restNodeMapper.toResponse(urlInfo);
    }

    // TODO: то же самое что и в getFileStatus. Решение или изменения в коде делать в рамках задачи: https://grinbog015.atlassian.net/browse/BZR-112
    @DeleteMapping("/{nodeId}")
    @Authorize(permission = Permission.NODES_DELETE)
    public void deleteNode(@PathVariable String spaceId, @PathVariable String nodeId) {
        markNodeForDeletionInbound.execute(nodeId);
    }
}
