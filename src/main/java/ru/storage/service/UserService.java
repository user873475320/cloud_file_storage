package ru.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.storage.config.security.CustomUserDetails;
import ru.storage.dto.UserDTO;
import ru.storage.entity.User;
import ru.storage.exception.RepositoryException;
import ru.storage.exception.UsernameAlreadyExistException;
import ru.storage.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    @Transactional
    public boolean save(UserDTO userDTO) {
        if (userDTO == null) {
            log.error("Failed to save user: null provided");
            return false;
        }

        try {
            User savedUser = userRepository.save(User.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build());
            log.debug("The next user was saved: {}", savedUser);
        } catch (DataIntegrityViolationException e) {
            log.warn("User with the next username already exist: {}", userDTO.getUsername());
            throw new UsernameAlreadyExistException(e);
        } catch (Exception e) {
            log.error("Unexpected exception in UserRepository in 'save' method");
            throw new RepositoryException("Exception in UserRepository 'save' method", e);
        }
        return true;
    }
}
