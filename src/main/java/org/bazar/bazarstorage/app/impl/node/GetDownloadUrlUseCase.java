package org.bazar.bazarstorage.app.impl.node;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.files.FilesService;
import org.bazar.bazarstorage.app.api.node.GetDownloadUrlInbound;
import org.bazar.bazarstorage.app.api.node.StorageNodeMapper;
import org.bazar.bazarstorage.app.impl.node.output.DownloadUrlInfo;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDownloadUrlUseCase implements GetDownloadUrlInbound {
    private final FilesService filesService;
    private final StorageNodeMapper storageNodeMapper;

    @Override
    public DownloadUrlInfo execute(String fileUuid) {
        String downloadUrl = filesService.initiateDownload(fileUuid);
        return storageNodeMapper.toDownloadUrlInfo(downloadUrl);
    }
}
