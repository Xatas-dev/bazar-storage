package org.bazar.bazarstorage.fw;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    public static final String PERSONA_USER_CACHE = "persona-user";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager(PERSONA_USER_CACHE);

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(3, TimeUnit.MINUTES)
                        .maximumSize(3_000)
                        .recordStats()
        );

        return cacheManager;
    }
}
