package org.bazar.bazarstorage.it;

import org.bazar.authorization.sdk.BazarAuthorizationClient;
import org.bazar.bazarstorage.adapter.outbound.persistence.storagenode.StorageNodeJpaRepository;
import org.bazar.bazarstorage.fw.BazarStorageApplication;
import org.bazar.bazarstorage.it.testutil.TestDataHelper;
import org.bazar.bazarstorage.it.testutil.WireMockTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        classes = {
                BazarStorageApplication.class
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Import({TestSecurityConfig.class, WireMockConfig.class})
public abstract class AbstractIntegrationTest {
    @Autowired
    protected WireMockTestHelper wireMockTestHelper;
    @Autowired
    protected TestDataHelper testDataHelper;
    @Autowired
    protected StorageNodeJpaRepository storageNodeJpaRepository;
    @MockitoBean
    protected BazarAuthorizationClient bazarAuthorizationClient;

    @BeforeEach
    void cleanUp() {
        testDataHelper.clearTables();
        wireMockTestHelper.stopWireMockServers();
    }

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
