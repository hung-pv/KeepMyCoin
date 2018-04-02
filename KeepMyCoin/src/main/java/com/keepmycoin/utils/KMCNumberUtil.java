package com.keepmycoin.utils;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class KMCNumberUtil {

	private static final Pattern P_VALID_EXPANDED_DOUBLE = Pattern.compile("^\\d+(\\.\\d+)?$");
	private static final Pattern P_VALID_EXPANDED_INT = Pattern.compile("^\\d+$");
	
	public static boolean isValidExpandedDouble(String number) {
		if (number == null) return false;
		return P_VALID_EXPANDED_DOUBLE.matcher(number).matches();
	}
	
	public static boolean isValidExpandedInt(String number) {
		if (number == null) return false;
		return P_VALID_EXPANDED_INT.matcher(number).matches();
	}
	
	public static String toBigValue(String number, int decimal) {
		if (!number.contains(".")) {
			number = number + ".0";
		}
		String[] spl = number.split("\\.");
		// 0: left - natural
		// 1: right - decimal
		String decimalPart = spl[1];
		while (decimalPart.length() < decimal) {
			decimalPart += "0";
		}
		return spl[0] + decimalPart;
	}
	
	public static String convertBigIntegerToHex(BigInteger bi) {
		String result = bi.toString(16);
		if (result.length() % 2 == 1) {
			result = "0" + result;
		}
		return result;
	}
	
	public static BigInteger fromHexToBigInteger(String hex) {
		if (hex == null) return null;
		if (hex.toLowerCase().startsWith("0x")) hex = hex.substring(2);
		return new BigInteger(hex, 16);
	}
	
	public static String fromBigValue(BigInteger bigInt, int decimal) {
		String bigValue = bigInt.toString(10);
		while (bigValue.length() < decimal + 1) {
			bigValue = "0" + bigValue;
		}
		String result = bigValue.substring(0, bigValue.length() - decimal) + "." + bigValue.substring(bigValue.length() - decimal);
		while(result.contains(".") && (result.endsWith("0") || result.endsWith("."))) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
