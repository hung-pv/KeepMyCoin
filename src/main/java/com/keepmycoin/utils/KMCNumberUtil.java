/*******************************************************************************
 * Copyright 2018 HungPV
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.keepmycoin.utils;

import java.math.BigInteger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.validator.ValidateMustBeDouble;

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
		if (number.endsWith(".")) {
			number = number + "0";
		}
		String[] spl = number.split("\\.");
		// 0: left - natural
		// 1: right - decimal
		String decimalPart = spl[1];
		while (decimalPart.length() < decimal) {
			decimalPart += "0";
		}
		String result = spl[0] + decimalPart;
		while (result.startsWith("0")) {
			result = result.substring(1);
		}
		return result;
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
		if (hex.toLowerCase().startsWith("x")) hex = hex.substring(1);
		return new BigInteger(hex, 16);
	}
	
	public static String fromBigValue(BigInteger bigInt, int decimal) {
		return fromBigValue(bigInt.toString(10), decimal);
	}
	
	public static String fromBigValue(String bigValue10, int decimal) {
		if (bigValue10.startsWith(".")) {
			bigValue10 = "0" + bigValue10;
		}
		if (bigValue10.endsWith(".")) {
			bigValue10 = bigValue10 + "0";
		}
		if (!new ValidateMustBeDouble().isValid(bigValue10)) {
			throw new IllegalArgumentException("Bad input number");
		}
		if (decimal < 1) {
			return bigValue10;
		}
		if (bigValue10.contains(".")) {
			String[] spl = bigValue10.split("\\.");
			decimal += spl[1].length();
			bigValue10 = bigValue10.replaceAll("\\.", "");
		}
		while(bigValue10.endsWith("0")) {
			bigValue10 = bigValue10.substring(0, bigValue10.length() - 1);
			decimal--;
		}
		if (decimal == 0) return bigValue10;
		while (decimal > bigValue10.length()) {
			bigValue10 = "0" + bigValue10;
		}
		StringBuilder sb = new StringBuilder()//
				.append(ArrayUtils.add(StringUtils.reverse(bigValue10).toCharArray(), decimal, '.'));
		String result = StringUtils.reverse(sb.toString());
		return result.startsWith(".") ? ("0" + result) : result;
	}
}
