package org.bazar.bazarstorage.adapter.outbound.rest.persona;

import org.bazar.bazarstorage.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CollectedUsers(
        Map<UUID, User> found,
        List<UUID> missing
) {}