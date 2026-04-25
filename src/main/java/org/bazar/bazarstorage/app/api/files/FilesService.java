package org.bazar.bazarstorage.app.api.files;

import org.bazar.bazarstorage.app.impl.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.impl.node.output.UploadUrlInfo;

public interface FilesService {
    UploadUrlInfo initiateUpload(GetUploadUrlCommand command);
}
