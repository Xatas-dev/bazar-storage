package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.UploadUrlInfo;

public interface GetUploadUrlInbound {
    UploadUrlInfo execute(GetUploadUrlCommand command);
}
