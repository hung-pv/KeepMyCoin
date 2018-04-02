package com.keepmycoin.validator;

import java.util.regex.Pattern;

public abstract class ValidateRegex implements IValidator<String> {
	public abstract String getPattern();

	@Override
	public boolean isValid(String input) {
		return Pattern.matches(getPattern(), input);
	}
}
