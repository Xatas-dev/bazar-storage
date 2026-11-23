package org.bazar.bazarstorage.adapter.outbound.rest.persona.dto;

import java.time.Instant;
import java.util.UUID;

public record GetUserResponseDto(
        UUID id,
        String username,
        String displayName,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
