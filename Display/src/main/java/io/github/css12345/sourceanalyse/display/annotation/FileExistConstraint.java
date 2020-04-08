package io.github.css12345.sourceanalyse.display.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.github.css12345.sourceanalyse.display.annotation.validator.FileExistValidator;

@Constraint(validatedBy = FileExistValidator.class)
@Retention(RUNTIME)
@Target(FIELD)
public @interface FileExistConstraint {
	String message() default "file did not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
