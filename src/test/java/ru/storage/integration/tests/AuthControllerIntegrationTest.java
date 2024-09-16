package ru.storage.integration.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.storage.entity.User;
import ru.storage.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class AuthControllerIntegrationTest extends AbstractControllerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test registration with valid form data")
    void givenValidForm_whenProcessRegistration_thenRedirectToLogin() throws Exception {
        mockMvc.perform(post("/auth/process_registration")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "validuser")
                        .param("password", "validpassword")
                        .param("confirmPassword", "validpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?success"));

        Optional<User> user = userRepository.findAll().stream().findAny();
        assertThat(user).isPresent();
        assertThat(user.get().getUsername()).isEqualTo("validuser");
    }

    @Test
    @DisplayName("Test registration with invalid form data")
    void givenInvalidForm_whenProcessRegistration_thenReturnRegistrationPageWithErrors() throws Exception {
        mockMvc.perform(post("/auth/process_registration")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration_page"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "username", "password"));
    }

    @Test
    @DisplayName("Test registration with duplicate username")
    void givenUserDataWithDuplicateUsername_whenProcessRegistration_thenReturnRegistrationPageWithError() throws Exception {
        userRepository.save(User.builder()
                        .username("duplicateUsername")
                        .password("someAnotherPassword")
                .build());

        mockMvc.perform(post("/auth/process_registration")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "duplicateUsername")
                        .param("password", "correctPassword")
                        .param("confirmPassword", "correctPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration_page"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "username"))
                .andExpect(model().attributeErrorCount("userRegistrationDTO", 1));
    }
}
