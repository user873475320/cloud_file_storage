package ru.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    @Size(min = 3, max = 100, message = "Username length must be between 3 and 100 characters")
    @NotBlank(message = "Username can not be empty")
    private String login;

    @Size(min = 6, max = 100, message = "Password length must be between 6 and 100 characters")
    @NotBlank(message = "Password can not be empty")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;
}