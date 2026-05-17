package org.bazar.bazarstorage.it.schedule;

import org.bazar.bazarstorage.adapter.inbound.schedule.NodesDeletingScheduler;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeStatus;
import org.bazar.bazarstorage.it.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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
    void deleteMarkedNodes_success() {
        generateStubFiles(true);

        nodesDeletingScheduler.deleteMarkedNodes();

        List<StorageNode> resultAfterScheduler = storageNodeJpaRepository.findAll();
        assertEquals(2, resultAfterScheduler.size());
        List<StorageNode> deletedNodes = resultAfterScheduler.stream()
                .filter(node -> node.getStatus() == StorageNodeStatus.DELETED)
                .toList();
        assertEquals(0, deletedNodes.size());
    }

    @Test
    void deleteMarkedNodes_failedFromFiles() {
        generateStubFiles(false);

        nodesDeletingScheduler.deleteMarkedNodes();

        List<StorageNode> resultAfterScheduler = storageNodeJpaRepository.findAll();
        assertEquals(4, resultAfterScheduler.size());
        List<StorageNode> deletedNodes = resultAfterScheduler.stream()
                .filter(node -> node.getStatus() == StorageNodeStatus.DELETED)
                .toList();
        assertEquals(2, deletedNodes.size());
    }

    // =================================================================================================================
    // Implementation
    // =================================================================================================================

    private void generateStubFiles(boolean deleteSuccess) {
        UUID deletedUuid1 = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(deletedUuid1, StorageNodeStatus.DELETED);
        UUID deletedUuid2 = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(deletedUuid2, StorageNodeStatus.DELETED);
        UUID uploadedUuid = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(uploadedUuid, StorageNodeStatus.UPLOADED);
        UUID inProgressUuid = UUID.randomUUID();
        testDataHelper.createStorageNodeWith(inProgressUuid, StorageNodeStatus.IN_PROGRESS);

        Integer status = deleteSuccess ? 200 : 400;
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(deletedUuid1.toString(), status);
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(deletedUuid2.toString(), status);
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(uploadedUuid.toString(), status);
        wireMockTestHelper.stubBazarFilesDeleteFileByFileUuid(inProgressUuid.toString(), status);
    }
}
