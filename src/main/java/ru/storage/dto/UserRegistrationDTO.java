package ru.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.storage.validation.annotation.PasswordMatches;
import ru.storage.validation.annotation.UniqueUsername;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserRegistrationDTO implements UserDTO {
    @Size(min = 3, max = 100, message = "Username length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    @UniqueUsername
    private String username;

    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String confirmPassword;
}
