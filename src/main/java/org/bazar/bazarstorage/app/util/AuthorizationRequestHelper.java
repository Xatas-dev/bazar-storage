package org.bazar.bazarstorage.app.util;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.authorization.sdk.Permission;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.service.AuthenticationService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestHelper {
    private final AuthenticationService authenticationService;
    private final AuthorizationRequestUtil util;
    private final StorageNodeRepository repository;

    public AuthorizationRequest buildNodeReadRequest(String spaceId) {
        return util.buildSimpleAuthorizationRequest(Long.parseLong(spaceId), Permission.STORAGE_NODE_READ);
    }

    public AuthorizationRequest buildNodeUploadRequest(String spaceId) {
        return util.buildSimpleAuthorizationRequest(Long.parseLong(spaceId), Permission.STORAGE_NODE_UPLOAD);
    }

    public AuthorizationRequest buildNodeDownloadRequest(String spaceId, String nodeId) {
        Map<String, String> principalAttributes = Collections.emptyMap();

        String createdBy = repository.findById(Long.parseLong(nodeId))
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, nodeId))
                .getUserId().toString();
        Map<String, String> resourceAttributes = Map.of("created_by", createdBy);

        return util.buildAuthorizationRequestWithAttributes(Long.parseLong(spaceId), Permission.STORAGE_NODE_DOWNLOAD, principalAttributes, resourceAttributes);
    }

    public AuthorizationRequest buildNodeDeleteRequest(String spaceId, String nodeId) {
        Map<String, String> principalAttributes = Collections.emptyMap();

        String createdBy = repository.findById(Long.parseLong(nodeId))
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, nodeId))
                .getUserId().toString();
        Map<String, String> resourceAttributes = Map.of("created_by", createdBy);

        return util.buildAuthorizationRequestWithAttributes(Long.parseLong(spaceId), Permission.STORAGE_NODE_DELETE, principalAttributes, resourceAttributes);
    }
}
