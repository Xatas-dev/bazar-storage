package org.bazar.bazarstorage.fw;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan("org.bazar.bazarstorage.domain")
@EnableJpaRepositories("org.bazar.bazarstorage.adapter.outbound.persistence")
public class PersistenceConfig {
}
