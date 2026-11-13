package org.bazar.bazarstorage.app.api.node;

import org.bazar.bazarstorage.app.api.node.commands.GetDownloadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.DownloadUrlInfo;

public interface GetDownloadUrlInbound {
    DownloadUrlInfo execute(GetDownloadUrlCommand command);
}
