package io.github.css12345.sourceanalyse.display.annotation.validator;
import java.io.File;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.css12345.sourceanalyse.display.annotation.FileExistConstraint;

public class FileExistValidator implements ConstraintValidator<FileExistConstraint, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (new File(value).exists())
			return true;
		return false;
	}

}
