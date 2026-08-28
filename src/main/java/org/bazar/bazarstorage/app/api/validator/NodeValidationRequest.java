package org.bazar.bazarstorage.app.api.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodeValidationRequest {
    private String nodeName;
}
