package ru.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.storage.integration.tests.AbstractControllerBaseTest;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class CloudFileStorageApplicationTests extends AbstractControllerBaseTest {

    @Test
    void contextLoads() {
    }

}
