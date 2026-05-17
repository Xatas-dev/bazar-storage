package org.bazar.bazarstorage.adapter.outbound.rest.persona.dto;

import java.time.Instant;
import java.util.UUID;

public record GetUserResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String userName,
        String email,
        String firstName,
        String lastName,
        String userPic
) {
}
