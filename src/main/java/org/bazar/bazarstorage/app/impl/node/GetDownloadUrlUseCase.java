package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetDownloadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.api.node.StorageNodeRepository;
import org.bazar.bazarstorage.app.impl.node.output.DownloadUrlInfo;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.springframework.stereotype.Component;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.STORAGE_NODE_NOT_FOUND_BY_ID;

@Component
@RequiredArgsConstructor
public class GetDownloadUrlUseCase implements GetDownloadUrlInbound {
    private final FilesService filesService;
    private final StorageNodeMapper storageNodeMapper;
    private final StorageNodeRepository storageNodeRepository;

    @Override
    public DownloadUrlInfo execute(String nodeId) {
        StorageNode storageNode = storageNodeRepository.findById(Long.parseLong(nodeId))
                .orElseThrow(() -> new BusinessException(STORAGE_NODE_NOT_FOUND_BY_ID, nodeId));
        String downloadUrl = filesService.initiateDownload(storageNode.getFileUuid());
        return storageNodeMapper.toDownloadUrlInfo(downloadUrl);
    }
}
