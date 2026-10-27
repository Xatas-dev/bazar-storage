package org.bazar.bazarstorage.app.api.files.command;

public record InitiateUploadCommand(
        String fileName,
        Long size
) {}
