package builder;

import lombok.experimental.UtilityClass;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;

import java.util.UUID;

@UtilityClass
public class StorageNodeBuilder {
    public static final String DEFAULT_NODE_NAME = "name.pdf";
    public static final StorageNodeStatus DEFAULT_STATUS = StorageNodeStatus.ACTIVE;
    public static final StorageNodeUploadStatus DEFAULT_UPLOAD_STATUS = StorageNodeUploadStatus.UPLOADED;
    public static final Long DEFAULT_SIZE = 1000L;
    public static final Long DEFAULT_SPACE_ID = 1L;

    public StorageNode buildDefault() {
        StorageNode storageNode = new StorageNode();
        storageNode.setFileUuid(UUID.randomUUID());
        storageNode.setNodeName(DEFAULT_NODE_NAME);
        storageNode.setStatus(DEFAULT_STATUS);
        storageNode.setSize(DEFAULT_SIZE);
        storageNode.setSpaceId(DEFAULT_SPACE_ID);
        storageNode.setUserId(JwtBuilder.TEST_USER_ID);
        storageNode.setUploadStatus(DEFAULT_UPLOAD_STATUS);
        return storageNode;
    }

    public StorageNode buildWith(StorageNodeStatus status) {
        StorageNode storageNode = buildDefault();
        storageNode.setStatus(status);
        return storageNode;
    }

    public StorageNode buildWith(StorageNodeUploadStatus status) {
        StorageNode storageNode = buildDefault();
        storageNode.setUploadStatus(status);
        return storageNode;
    }

    public StorageNode buildWith(UUID fileUuid) {
        StorageNode storageNode = buildDefault();
        storageNode.setFileUuid(fileUuid);
        return storageNode;
    }

    public StorageNode buildWith(UUID fileUuid, StorageNodeStatus status) {
        StorageNode storageNode = buildDefault();
        storageNode.setFileUuid(fileUuid);
        storageNode.setStatus(status);
        return storageNode;
    }

    public StorageNode buildWith(UUID fileUuid, StorageNodeUploadStatus status) {
        StorageNode storageNode = buildDefault();
        storageNode.setFileUuid(fileUuid);
        storageNode.setUploadStatus(status);
        return storageNode;
    }
}
