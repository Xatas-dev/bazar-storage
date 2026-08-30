package builder;

import lombok.experimental.UtilityClass;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadResultCommand;
import org.bazar.bazarstorage.app.api.node.commands.FileUploadStatus;

import java.util.List;

@UtilityClass
public class FileUploadResultCommandBuilder {
    public String DEFAULT_DOMAIN = "STORAGE";
    public Long DEFAULT_SIZE = 150L;
    public String DEFAULT_FILE_UUID = "813e043f-b7ce-45d6-8b7d-a2873d70a515";
    public String DEFAULT_FILE_NAME = "fileName";
    public String DEFAULT_CONTENT_TYPE = "text/plain";
    public FileUploadStatus DEFAULT_STATUS = FileUploadStatus.UPLOADED;
    public String DEFAULT_ERROR_DESCRIPTION = "description";

    public FileUploadResultCommand buildDefault() {
        return new FileUploadResultCommand(DEFAULT_DOMAIN, DEFAULT_SIZE, DEFAULT_FILE_UUID, DEFAULT_STATUS,
                DEFAULT_FILE_NAME, DEFAULT_CONTENT_TYPE, List.of());
    }

    public FileUploadResultCommand buildWith(FileUploadStatus status) {
        return new FileUploadResultCommand(DEFAULT_DOMAIN, DEFAULT_SIZE, DEFAULT_FILE_UUID, status,
                DEFAULT_FILE_NAME, DEFAULT_CONTENT_TYPE, List.of());
    }

    public FileUploadResultCommand buildWith(Long size, String fileName, String contentType) {
        return new FileUploadResultCommand(DEFAULT_DOMAIN, size, DEFAULT_FILE_UUID, DEFAULT_STATUS, fileName, contentType, List.of());
    }

    public FileUploadResultCommand buildValidationErrorMessage(List<String> errors) {
        List<FileUploadResultCommand.Error> errorList = errors.stream()
                .map(error -> new FileUploadResultCommand.Error(error, DEFAULT_ERROR_DESCRIPTION))
                .toList();
        return new FileUploadResultCommand(DEFAULT_DOMAIN, DEFAULT_SIZE, DEFAULT_FILE_UUID, FileUploadStatus.VALIDATION_ERROR,
                DEFAULT_FILE_NAME, DEFAULT_CONTENT_TYPE, errorList);
    }
}
