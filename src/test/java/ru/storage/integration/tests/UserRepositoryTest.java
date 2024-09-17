package ru.storage.integration.tests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.storage.entity.User;
import ru.storage.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
@DirtiesContext
class UserRepositoryTest extends AbstractControllerBaseTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .username("qwertyk")
                .password("testPassword")
                .build();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save user functionality")
    void givenUserObject_whenSave_thenUserIsCreated() {
        // given: this.user
        // when
        User savedUser = userRepository.save(this.user);
        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test exist user functionality")
    void givenSavedUserAndHisUsername_whenExistsByUsername_thenReturnTrue() {
        // given
        userRepository.save(this.user);
        // when
        boolean isExist = userRepository.existsByUsername(user.getUsername());
        // then
        assertThat(isExist).isTrue();
    }

    @Test
    @DisplayName("Test get user by id functionality")
    void givenSavedUser_whenFindById_thenUserIsReturned() {
        // given
        User savedUser = userRepository.save(this.user);
        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        // then
        assertThat(foundUser).isPresent();
    }

    @Test
    @DisplayName("Test user not found functionality")
    void givenUserIsNotSaved_whenFindById_thenOptionalIsEmpty() {
        // given-when
        Optional<User> foundUser = userRepository.findById(1L);
        // then
        assertThat(foundUser).isNotPresent();
    }
}
