package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.commands.FileUploadResultCommand;

public interface ProcessFileUploadedResultInbound {
    void execute(FileUploadResultCommand command);
}
