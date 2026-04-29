package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.commands.GetNodesBySpaceIdCommand;
import org.bazar.bazarstorage.app.impl.node.output.NodeInfoPage;

public interface GetNodesBySpaceIdInbound {
    NodeInfoPage execute(GetNodesBySpaceIdCommand command);
}
