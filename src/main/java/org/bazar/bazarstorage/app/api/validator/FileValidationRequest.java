package org.bazar.bazarstorage.app.api.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileValidationRequest extends NodeValidationRequest {
    private Long size;
    private String extension;
}
