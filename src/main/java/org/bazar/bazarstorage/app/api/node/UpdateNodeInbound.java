package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.UpdateNodeCommand;

public interface UpdateNodeInbound {
    void execute(UpdateNodeCommand command);
}
