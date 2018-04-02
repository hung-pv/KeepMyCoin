package com.keepmycoin.validator;

public class ValidateMnemonic extends ValidateNormal<String> {

	@Override
	public String describleWhenInvalid() {
		return "Total number of words must be divisible by 12";
	}

	@Override
	public boolean isValid(String input) {
		if (input == null)
			return false;
		String[] spl = input.trim().split("\\s");
		return spl.length % 12 == 0;
	}

}
