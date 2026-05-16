package org.bazar.bazarstorage.adapter.inbound.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.app.api.node.DeleteMarkedNodesInbound;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NodesDeletingScheduler {
    private final DeleteMarkedNodesInbound deleteMarkedNodesInbound;

    @Scheduled(cron = "${settings.schedule.delete-marked-files.cron}")
    // Пока нет нескольких инстансов, не стал добавлять шедлок или другие подобные механизмы
    public void deleteMarkedNodes() {
        log.info("DeleteNodes scheduler is starting...");
        deleteMarkedNodesInbound.execute();
    }
}
