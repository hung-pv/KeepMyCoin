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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.keepmycoin.validator.ValidateRegex;

public class KMCStringUtil {

	public static byte[] getBytes(String text, int size) {
		return getBytes(text, size, StandardCharsets.UTF_8);
	}

	public static byte[] getBytesNullable(String text) {
		if (text == null)
			return null;
		return text.getBytes(StandardCharsets.UTF_8);
	}

	public static byte[] getBytes(String text, int size, Charset c) {
		byte[] barr = text.getBytes(c);
		byte[] key = new byte[size];
		if (barr.length > key.length) {
			System.arraycopy(barr, 0, key, 0, key.length);
		} else if (barr.length < key.length) {
			System.arraycopy(barr, 0, key, 0, barr.length);
		} else {
			key = barr;
		}
		return key;
	}

	public static String beautiNumber(String number) {
		String natural, decimal;
		if (number.startsWith(".")) {
			number = "0" + number;
		}
		number = number.replaceAll("\\,", "");
		if (number.contains(".")) {
			natural = number.substring(0, number.indexOf("."));
			decimal = number.substring(number.indexOf("."));
		} else {
			natural = number;
			decimal = "";
		}

		char[] reverse = org.apache.commons.lang3.StringUtils.reverse(natural).toCharArray();
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (char digit : reverse) {
			sb.append(digit);
			if (counter % 3 == 2) {
				sb.append(',');
			}
			counter++;
		}

		String result = org.apache.commons.lang3.StringUtils.reverse(sb.toString()) + decimal;
		if (result.startsWith(",")) {
			result = result.substring(1);
		}
		return result;
	}

	public static String toPathChars(String original) {
		return original.replaceAll("[^aA-zZ0-9_-]", "_");
	}

	public static String getDomainName(String url) throws URISyntaxException {
		ValidateRegex validURLwithProtocol = new ValidateRegex() {
			@Override
			public String describleWhenInvalid() {
				return null;
			}
			
			@Override
			public String getPattern() {
				return "\\w+\\:\\/\\/";
			}
		};
		if (!validURLwithProtocol.isValid(url)) {
			if (url.startsWith(":")) {
				url = url.substring(1);
			}
			if (url.startsWith("//")) {
				url = url.substring(2);
			}
			url = "protocol://" + url;
		}
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	public static String convertDateTimeToString(Date date) {
		return sdf.format(date);
	}
	
	public static String printHexBinary(byte[] arr) {
		if (arr == null) return null;
		return DatatypeConverter.printHexBinary(arr).toLowerCase();
	}
	
	public static byte[] parseHexBinary(String hex) {
		if (hex == null) return null;
		return DatatypeConverter.parseHexBinary(hex);
	}
}
