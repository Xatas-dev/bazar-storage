package org.bazar.bazarstorage.it.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bazar.bazarstorage.it.AbstractIntegrationTest;
import org.bazar.bazarstorage.it.testutil.RestTestUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractControllerIntegrationTest extends AbstractIntegrationTest {
    protected static final String POST_UPLOAD_URL_API_URL = "/api/v1/spaces/%s/nodes";
    protected static final String GET_STATUS_API_URL = "/api/v1/spaces/%s/nodes/%s/status";
    protected static final String GET_NODES_API_URL = "/api/v1/spaces/%s/nodes";
    protected static final String GET_DOWNLOAD_URL_API_URL = "/api/v1/spaces/%s/nodes/%s/download";
    protected static final String DELETE_NODE_API_URL = "/api/v1/spaces/%s/nodes/%s";

    protected static final TypeReference<String> TYPE_REF_V1_POST_UPLOAD_URL_RESPONSE_AUTH_ERROR = new TypeReference<>() {};

    @Autowired
    protected RestTestUtil restTestUtil;
}
