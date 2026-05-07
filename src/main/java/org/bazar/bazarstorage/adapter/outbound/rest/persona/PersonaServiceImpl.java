package org.bazar.bazarstorage.adapter.outbound.rest.persona;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bazar.bazarstorage.adapter.outbound.rest.persona.dto.GetUserResponseDto;
import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.ErrorCode;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

    @Override
    public Optional<User> getUserById(UUID userId) {
        return getUsersUsingCache(List.of(userId)).stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private List<User> getUsersUsingCache(List<UUID> userIds) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(CaffeineCacheConfig.PERSONA_USER_CACHE));
        CollectedUsers collected = collectCachedAndMissingUsers(cache, userIds);

        Map<UUID, User> result = new HashMap<>(collected.found());
        List<UUID> missingUserIds = collected.missing();

        List<GetUserResponseDto> missingUsers = getUsersFromFeignClient(missingUserIds);
        processMissingUsers(missingUsers, result, cache);

        return userIds.stream()
                .map(result::get)
                .toList();
    }

    private CollectedUsers collectCachedAndMissingUsers(Cache cache, List<UUID> userIds) {
        Map<UUID, User> found = new HashMap<>();
        List<UUID> missing = new ArrayList<>();

        for (UUID userId : userIds) {
            User user = cache.get(userId, User.class);
            if (user != null) {
                found.put(userId, user);
            } else {
                missing.add(userId);
            }
        }

        return new CollectedUsers(
                Map.copyOf(found),
                List.copyOf(missing)
        );
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
            try {
                return personaFeignClient.getUsers(userIds.stream().map(UUID::toString).toList());
            } catch (FeignException e) {
                log.error("Error while calling persona service to get users: status {}, message {}", e.status(), e.getMessage());
                throw new BusinessException(ErrorCode.TECH_ERROR);
            }
        }
        return List.of();
    }

    private record CollectedUsers(
            Map<UUID, User> found,
            List<UUID> missing
    ) {}
}
