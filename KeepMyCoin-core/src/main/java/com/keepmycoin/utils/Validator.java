package com.keepmycoin.utils;

public class Validator {
	public static boolean isValidSeedWords(String text) {
		if (text == null)
			return false;
		return text.trim().split("\\s").length % 2 == 0;
	}
}
