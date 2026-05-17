package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;

public interface GetUploadUrlInbound {
    UploadUrlInfo execute(GetUploadUrlCommand command);
}
