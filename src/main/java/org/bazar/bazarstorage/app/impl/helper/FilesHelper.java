package org.bazar.bazarstorage.app.impl.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilesHelper {
    public String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex != -1 ? fileName.substring(lastDotIndex + 1) : "";
    }
}
