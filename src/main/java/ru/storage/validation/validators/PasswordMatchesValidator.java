package ru.storage.validation.validators;

import org.springframework.stereotype.Component;
import ru.storage.dto.UserRegistrationDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.storage.validation.annotation.PasswordMatches;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDTO> {

    @Override
    public boolean isValid(UserRegistrationDTO user, ConstraintValidatorContext context) {
        boolean isValid = user.getPassword().equals(user.getConfirmPassword());

        if (!isValid) {
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("password")
                .addConstraintViolation();
        }

        return isValid;
    }
}