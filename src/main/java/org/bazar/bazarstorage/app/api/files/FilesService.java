package org.bazar.bazarstorage.app.api.files;

import org.bazar.bazarstorage.app.api.files.command.InitiateUploadCommand;
import org.bazar.bazarstorage.app.api.node.output.InitiateUploadResult;

import java.util.UUID;

public interface FilesService {
    InitiateUploadResult initiateUpload(InitiateUploadCommand command);

    String initiateDownload(UUID fileUuid);

    void deleteFileByFileUuid(UUID fileUuid);
}
