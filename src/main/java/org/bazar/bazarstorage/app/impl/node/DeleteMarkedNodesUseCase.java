package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.SettingProperties;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.DeleteMarkedNodesInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteMarkedNodesUseCase implements DeleteMarkedNodesInbound {
    private final StorageNodeRepository storageNodeRepository;
    private final SettingProperties settingProperties;
    private final TransactionTemplate transactionTemplate;
    private final FilesService filesService;

    @Override
    public void execute() {
        Long lastId = 0L;
        while (true) {
            List<StorageNode> nodesToDelete = storageNodeRepository.findDeletedNodesAfterId(
                    lastId,
                    settingProperties.getSchedule().getDeleteMarkedFiles().getBatchSize()
            );
            if (nodesToDelete.isEmpty()) {
                break;
            }

            List<Long> successfullyDeletedIds = new ArrayList<>();
            nodesToDelete.forEach(storageNode -> {
                if (deleteFromFile(storageNode)) {
                    successfullyDeletedIds.add(storageNode.getId());
                }
            });
            deleteFromDb(successfullyDeletedIds);

            lastId = nodesToDelete.getLast().getId();
        }
    }

    // =================================================================================================================
    // Implementation
    // =================================================================================================================

    private boolean deleteFromFile(StorageNode storageNode) {
        try {
            filesService.deleteFileByFileUuid(storageNode.getFileUuid());
            return true;
        } catch (Exception e) {
            log.error("Failed to cleanup file {}", storageNode.getFileUuid(), e);
            return false;
        }
    }

    private void deleteFromDb(List<Long> idsToDelete) {
        if (!idsToDelete.isEmpty()) {
            transactionTemplate.executeWithoutResult(status ->
                    storageNodeRepository.deleteAllByIds(idsToDelete));
        }
    }
}
