package org.bazar.bazarstorage.app.impl.helper;

import lombok.RequiredArgsConstructor;
import org.bazar.authorization.sdk.AuthorizationRequest;
import org.bazar.authorization.sdk.Permission;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestBuilder {
    private static final String CREATED_BY_ATTRIBUTE = "created_by";

    public static AuthorizationRequest buildNodeDownloadRequest(Long spaceId, String bearerToken, StorageNode storageNode) {
        Map<String, String> resourceAttributes = Map.of(CREATED_BY_ATTRIBUTE, storageNode.getUserId().toString());

        return AuthorizationRequest.builder()
                .spaceId(spaceId)
                .permission(Permission.STORAGE_NODE_DOWNLOAD)
                .principalAttributes(Collections.emptyMap())
                .resourceAttributes(resourceAttributes)
                .bearerToken(bearerToken)
                .build();
    }
}
