package ru.storage.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.storage.validation.validators.UniqueUsernameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "Login is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}