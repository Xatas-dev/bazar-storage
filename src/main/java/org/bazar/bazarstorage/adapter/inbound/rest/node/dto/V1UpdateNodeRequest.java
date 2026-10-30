package org.bazar.bazarstorage.adapter.inbound.rest.node.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;

@Getter
public class V1UpdateNodeRequest {
    private Long parentId;

    @JsonIgnore
    private boolean parentIdProvided;

    @JsonSetter("parentId")
    public void setParentId(Long parentId) {
        this.parentId = parentId;
        this.parentIdProvided = true;
    }
}
