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

import com.keepmycoin.validator.ValidateMustBeDouble;

public class KMCNumberUtil {

	public static String convertBigIntegerToHex(BigInteger bi) {
		String result = bi.toString(16);
		if (result.length() % 2 == 1) {
			result = "0" + result;
		}
		return result;
	}

	public static BigInteger fromHexToBigInteger(String hex) {
		if (hex == null)
			return null;
		if (hex.toLowerCase().startsWith("0x"))
			hex = hex.substring(2);
		if (hex.toLowerCase().startsWith("x"))
			hex = hex.substring(1);
		return new BigInteger(hex, 16);
	}

	public static String shiftTheDot(String bigValue, int shiftSteps) {
		return shiftTheDot(bigValue, shiftSteps, false);
	}

	public static String shiftTheDot(String bigValue, int shiftSteps, boolean beauty) {
		if (bigValue != null && bigValue.contains(",")) {
			bigValue = bigValue.replaceAll("\\,", "");
		}
		if (!new ValidateMustBeDouble().isValid(bigValue)) {
			throw new NumberFormatException("Must be a raw text double");
		}
		if (shiftSteps == 0)
			return bigValue;
		if (!bigValue.contains(".")) {
			bigValue += ".0";
		}
		String[] spl = bigValue.split("\\.", 2);
		StringBuilder sb = new StringBuilder(bigValue);
		String targetPart = shiftSteps > 0 ? spl[1] : spl[0];
		int noOfCharsToAdd = Math.abs(shiftSteps) - targetPart.length();
		while (noOfCharsToAdd > 0) {
			if (shiftSteps > 0) {
				sb.append('0');
			} else {
				sb.insert(0, '0');
			}
			noOfCharsToAdd--;
		}

		int dotIndex = sb.indexOf(".");
		sb.deleteCharAt(dotIndex);

		int insertIndex = dotIndex + shiftSteps;
		sb.insert(insertIndex, '.');

		int indexLastChar = sb.length() - 1;

		if (sb.charAt(0) == '.') {
			sb.insert(0, '0');
		} else if (sb.charAt(indexLastChar) == '.') {
			sb.deleteCharAt(indexLastChar);
		}
		while (sb.charAt(0) == '0' && sb.charAt(1) != '.') {
			sb.deleteCharAt(0);
		}
		while (sb.charAt(sb.length() - 1) == '0' && sb.indexOf(".") >= 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		if (sb.charAt(sb.length() - 1) == '.') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return beauty ? KMCStringUtil.beautiNumber(sb.toString()) : sb.toString();
	}
}
