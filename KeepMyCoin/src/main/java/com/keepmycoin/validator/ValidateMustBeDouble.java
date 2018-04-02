package com.keepmycoin.validator;

public class ValidateMustBeDouble extends ValidateRegex {

	@Override
	public String describleWhenInvalid() {
		return "Must be double";
	}

	@Override
	public String getPattern() {
		return "(\\-)?\\d+(\\.\\d+)?";
	}

}
