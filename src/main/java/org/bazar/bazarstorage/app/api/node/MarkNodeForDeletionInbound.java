package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.DeleteNodeCommand;

public interface MarkNodeForDeletionInbound {
    void execute(DeleteNodeCommand command);
}
