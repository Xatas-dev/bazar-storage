package org.bazar.bazarstorage.app.impl.service;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.app.api.persona.PersonaService;
import org.bazar.bazarstorage.domain.storagenode.StorageNode;
import org.bazar.bazarstorage.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserLoader {
    private final PersonaService personaService;

    public Map<UUID, User> loadUsers(List<StorageNode> storageNodes) {
        List<UUID> userIds = storageNodes.stream()
                .map(StorageNode::getUserId)
                .distinct()
                .toList();
        List<User> usersByIds = personaService.getUsersByIds(userIds);

        return usersByIds.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
    }
}
