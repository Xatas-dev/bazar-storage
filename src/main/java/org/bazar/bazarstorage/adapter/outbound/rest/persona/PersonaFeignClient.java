package org.bazar.bazarstorage.adapter.outbound.rest.persona;

import org.bazar.bazarstorage.adapter.outbound.rest.persona.dto.GetUserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "bazar-persona", url = "${service.bazar-persona.url}")
public interface PersonaFeignClient {
    @GetMapping(value = "/users")
    List<GetUserResponseDto> getUsers(@RequestParam("ids") List<String> ids);
}
