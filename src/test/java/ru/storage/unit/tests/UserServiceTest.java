package ru.storage.unit.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.storage.dto.UserDTO;
import ru.storage.dto.UserRegistrationDTO;
import ru.storage.entity.User;
import ru.storage.exception.UsernameAlreadyExistException;
import ru.storage.repository.UserRepository;
import ru.storage.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User persistedUser;

    @BeforeEach
    void setUp() {
        this.userDTO = new UserRegistrationDTO(
                "testUsername",
                "password",
                "password"
        );

        this.persistedUser = User.builder()
                .id(1L)
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
    }

    @Test
    @DisplayName("Test save user functionality")
    void givenUserDtoToSave_whenSave_thenRepositoryIsCalled() {
        // given
        BDDMockito.given(userRepository.save(any(User.class))).willReturn(persistedUser);
        BDDMockito.given(passwordEncoder.encode(any(CharSequence.class))).willReturn("encodedPassword");
        // when
        boolean isSuccess = userService.save(userDTO);
        // then
        assertThat(isSuccess).isTrue();
    }

    @Test
    @DisplayName("Test save user with duplicate username functionality")
    void givenUserDtoWithDuplicatedUsername_whenSave_thenExceptionIsThrown() {
        // given
        BDDMockito.given(userRepository.save(any(User.class))).willThrow(DataIntegrityViolationException.class);
        // when-then
        assertThrows(UsernameAlreadyExistException.class, () -> userService.save(userDTO));
    }
}
