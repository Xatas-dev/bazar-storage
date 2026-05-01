package org.bazar.bazarstorage.app.api.persona;

import org.bazar.bazarstorage.domain.user.User;

import java.util.List;
import java.util.UUID;

public interface PersonaService {
    List<User> getUsersByIds(List<UUID> userIds);
}
