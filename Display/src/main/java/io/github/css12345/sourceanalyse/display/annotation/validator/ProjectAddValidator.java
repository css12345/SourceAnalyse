package io.github.css12345.sourceanalyse.display.annotation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.css12345.sourceanalyse.display.annotation.ProjectAddConstraint;
import io.github.css12345.sourceanalyse.display.utils.ProjectIDUtils;

public class ProjectAddValidator implements ConstraintValidator<ProjectAddConstraint, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return !ProjectIDUtils.checkPathAdded(value);
	}

}
