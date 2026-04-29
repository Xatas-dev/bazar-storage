package org.bazar.bazarstorage.adapter.outbound.rest.persona;

import lombok.RequiredArgsConstructor;
import org.bazar.bazarstorage.adapter.outbound.rest.persona.dto.GetUserResponseDto;
import org.bazar.bazarstorage.app.api.persona.PersonaService;
import org.bazar.bazarstorage.domain.user.User;
import org.bazar.bazarstorage.fw.CaffeineCacheConfig;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaFeignClient personaFeignClient;
    private final PersonaMapper mapper;
    private final CacheManager cacheManager;

    @Override
    public List<User> getUsersByIds(List<UUID> userIds) {
        return getUsersUsingCache(userIds);
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private List<User> getUsersUsingCache(List<UUID> userIds) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(CaffeineCacheConfig.PERSONA_USER_CACHE));

        Map<UUID, User> result = new HashMap<>();
        List<UUID> missingUserIds = new ArrayList<>();
        collectCachedAndMissingUsers(cache, userIds, result, missingUserIds);

        List<GetUserResponseDto> missingUsers = getUsersFromFeignClient(missingUserIds);
        processMissingUsers(missingUsers, result, cache);

        return userIds.stream()
                .map(result::get)
                .toList();
    }

    private void collectCachedAndMissingUsers(Cache cache, List<UUID> userIds, Map<UUID, User> result, List<UUID> missingUserIds) {
        userIds.forEach(userId -> {
            User userDto = cache.get(userId, User.class);
            if (userDto != null) {
                result.put(userId, userDto);
            } else {
                missingUserIds.add(userId);
            }
        });
    }

    private void processMissingUsers(List<GetUserResponseDto> missingUsers, Map<UUID, User> result, Cache cache) {
        missingUsers.stream()
                .map(mapper::mapToUserDto)
                .forEach(user -> {
                    result.put(user.getUserId(), user);
                    cache.put(user.getUserId(), user);
                });
    }

    private List<GetUserResponseDto> getUsersFromFeignClient(List<UUID> userIds) {
        if (!userIds.isEmpty()) {
            return personaFeignClient.getUsers(userIds.stream().map(UUID::toString).toList());
        }
        return List.of();
    }
}
