package org.bazar.bazarstorage.it.schedule;

import org.bazar.bazarstorage.adapter.inbound.schedule.NodesDeletingScheduler;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeType;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeUploadStatus;
import org.bazar.bazarstorage.it.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodesDeletingSchedulerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private NodesDeletingScheduler nodesDeletingScheduler;

    @BeforeEach
    void setUp() {
        wireMockTestHelper.startMockBazarFilesServer();
    }

    @Test
    @DisplayName("Успешное удаление помеченных узлов")
    void deleteMarkedNodes_success() {
        generateStubFiles(true);

        nodesDeletingScheduler.deleteMarkedNodes();

        List<StorageNode> resultAfterScheduler = storageNodeJpaRepository.findAll();
        assertEquals(2, resultAfterScheduler.size());
        List<StorageNode> nodesMarkedDeleted = resultAfterScheduler.stream()
                .filter(node -> node.getStatus() == StorageNodeStatus.DELETED)
                .toList();
        assertEquals(0, nodesMarkedDeleted.size());
    }

    @Test
    @DisplayName("Неуспешное удаление помеченных узлов - ошибка от bazar-files")
    void deleteMarkedNodes_failedFromFiles() {
        generateStubFiles(false);

        nodesDeletingScheduler.deleteMarkedNodes();

        List<StorageNode> resultAfterScheduler = storageNodeJpaRepository.findAll();
        assertEquals(3, resultAfterScheduler.size());
        List<StorageNode> nodesMarkedDeleted = resultAfterScheduler.stream()
                .filter(node -> node.getStatus() == StorageNodeStatus.DELETED)
                .toList();
        assertEquals(1, nodesMarkedDeleted.size());
    }

    // =================================================================================================================
    // Implementation
    // =================================================================================================================

    private void generateStubFiles(boolean deleteSuccess) {
        UUID deletedUuid1 = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(deletedUuid1, StorageNodeStatus.DELETED, StorageNodeType.FILE);
        testDataHelper.createStorageNodeWith(null, StorageNodeStatus.DELETED, StorageNodeType.FOLDER);
        UUID uploadedUuid = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(uploadedUuid, StorageNodeUploadStatus.UPLOADED);
        UUID inProgressUuid = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(inProgressUuid, StorageNodeUploadStatus.IN_PROGRESS);

        Integer status = deleteSuccess ? 200 : 400;
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(deletedUuid1.toString(), status);
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(uploadedUuid.toString(), status);
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(inProgressUuid.toString(), status);
    }
}
