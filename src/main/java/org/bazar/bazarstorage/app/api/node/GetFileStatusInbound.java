package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.output.FileStatusInfo;

public interface GetFileStatusInbound {
    FileStatusInfo execute(String nodeId);
}
