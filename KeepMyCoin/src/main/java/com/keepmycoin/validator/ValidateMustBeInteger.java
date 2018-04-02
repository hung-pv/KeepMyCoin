package com.keepmycoin.validator;

public class ValidateMustBeInteger extends ValidateRegex {

	@Override
	public String describleWhenInvalid() {
		return "Must be integer";
	}

	@Override
	public String getPattern() {
		return "(\\-)?\\d+";
	}

}
