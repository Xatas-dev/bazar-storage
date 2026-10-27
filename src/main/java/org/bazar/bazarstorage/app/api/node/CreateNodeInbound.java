package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.CreateNodeCommand;
import org.bazar.bazarstorage.app.api.node.output.CreatedNodeInfo;

public interface CreateNodeInbound {
    CreatedNodeInfo execute(CreateNodeCommand command);
}
