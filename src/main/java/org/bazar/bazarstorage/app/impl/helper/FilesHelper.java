package org.bazar.bazarstorage.app.impl.helper;

import lombok.experimental.UtilityClass;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

@UtilityClass
public class FilesHelper {
    public String getExtensionByFileName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex != -1 ? fileName.substring(lastDotIndex + 1) : "";
    }

    public String getExtensionFromContentType(String contentType) {
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType mimeType = allTypes.forName(contentType);
            return mimeType.getExtension().substring(1);
        } catch (MimeTypeException e) {
            throw new IllegalArgumentException(String.format("Invalid contentType: %s", contentType));
        }
    }
}
