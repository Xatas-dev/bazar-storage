package org.bazar.bazarstorage.fw;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("org.bazar.bazarstorage.adapter")
public class FeignClientConfig {
}
