package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.output.FileStatusInfo;

public interface GetFileStatusInbound {
    FileStatusInfo execute(String spaceId, String nodeId);
}
