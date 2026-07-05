package org.bazar.bazarstorage.app.api.files;

import org.bazar.bazarstorage.app.api.node.commands.GetUploadUrlCommand;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;

import java.util.UUID;

public interface FilesService {
    InitiateUploadResult initiateUpload(GetUploadUrlCommand command);

    String initiateDownload(UUID fileUuid);

    void deleteFileByFileUuid(UUID fileUuid);
}
