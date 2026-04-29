package org.bazar.bazarstorage.adapter.outbound.rest.persona;

import org.bazar.bazarstorage.adapter.outbound.rest.persona.dto.GetUserResponseDto;
import org.bazar.bazarstorage.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PersonaMapper {
    @Mapping(target = "userId", source = "id")
    User mapToUserDto(GetUserResponseDto response);
}
