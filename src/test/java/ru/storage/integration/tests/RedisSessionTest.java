package ru.storage.integration.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;
import ru.storage.dto.UserLoginDTO;
import ru.storage.service.UserService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
class RedisSessionTest extends AbstractControllerBaseTest {
    private Jedis jedis;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        jedis = new Jedis("localhost", REDIS_CONTAINER.getMappedPort(6379));
        jedis.flushAll();
    }

    @Test
    @DisplayName("Given Redis Container When Checking Running Status Then Status Should Be Running")
    void givenRedisContainerConfiguredWithDynamicProperties_whenCheckingRunningStatus_thenStatusIsRunning() {
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Given Empty Redis When Fetching All Keys Then Result Should Be Empty")
    void givenEmptyRedis_whenFetchingAllKeys_thenResultShouldBeEmpty() {
        Set<String> result = jedis.keys("*");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given User Login When Session Saved Then Session Should Be Deleted After Expiry")
    void givenCorrectUserLoginData_whenUserLogin_thenSessionShouldBeSavedAndDeletedAfterExpiry() throws Exception {
        // Given
        UserLoginDTO userLoginDTO = new UserLoginDTO("newUser", "newPassword");
        userService.save(userLoginDTO);

        Set<String> result = jedis.keys("*");
        assertThat(result).isEmpty();

        // When
        mockMvc.perform(post("/auth/process_login")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", userLoginDTO.getUsername())
                        .param("password", userLoginDTO.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        // Then
        result = jedis.keys("*");
        assertThat(result).isNotEmpty();

        Thread.sleep(6000); // Wait till the redis session is deleted
        assertThat(jedis.keys("*")).isEmpty();
    }
}
