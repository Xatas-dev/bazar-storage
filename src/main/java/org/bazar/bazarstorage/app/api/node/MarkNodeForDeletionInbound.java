package org.bazar.bazarstorage.app.api.node;

public interface MarkNodeForDeletionInbound {
    void execute(String spaceId, String nodeId);
}
