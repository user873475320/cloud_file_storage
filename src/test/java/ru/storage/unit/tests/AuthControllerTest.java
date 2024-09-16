package ru.storage.unit.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.storage.controller.AuthController;
import ru.storage.dto.UserRegistrationDTO;
import ru.storage.repository.UserRepository;
import ru.storage.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Test success response to login page")
    void givenRequestToLoginPage_thenShowLoginPage_thenSuccessResponse() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test success response to registration page")
    void givenRequestToRegistrationPage_thenShowRegistrationPage_thenSuccessResponse() throws Exception {
        mockMvc.perform(get("/auth/registration"))
                .andDo(print())
                .andExpect(model().attributeExists("userRegistrationDTO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test registration with valid form data")
    void givenValidForm_whenProcessRegistration_thenRedirectToLogin() throws Exception {
        // Mock userService save method
        BDDMockito.given(userService.save(any(UserRegistrationDTO.class))).willReturn(true);

        // Simulate form submission with valid data
        mockMvc.perform(post("/auth/process_registration")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "validuser")
                        .param("password", "validpassword")
                        .param("confirmPassword", "validpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?success"));

        // Verify save method was called once
        verify(userService, times(1)).save(any(UserRegistrationDTO.class));
    }

    @Test
    @DisplayName("Test registration with invalid form data")
    void givenInvalidForm_whenProcessRegistration_thenReturnRegistrationPage() throws Exception {
        // Simulate form submission with invalid data
        mockMvc.perform(post("/auth/process_registration")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")  // Invalid username
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())  // It should return status OK (show the registration page)
                .andExpect(view().name("auth/registration_page")) // Expect the view to be registration page
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "username", "password"));

        // Verify save method was never called
        verify(userService, times(0)).save(any(UserRegistrationDTO.class));
    }
}
