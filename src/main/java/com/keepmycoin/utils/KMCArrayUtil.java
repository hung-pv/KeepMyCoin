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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KMCArrayUtil {
	public static byte[] randomBytes(int size) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[size];
		SecureRandom.getInstanceStrong().nextBytes(bytes);
		return bytes;
	}
	
	public static int[] unsignedBytes(byte[] arr) {
		int[] result = new int[arr.length];
		for(int i = 0; i < arr.length; i++) {
			result[i] = arr[i] & 0xFF;
		}
		return result;
	}
	
	public static byte[] checksum(byte[] buffer) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(buffer);
		return digest.digest();
	}
	
	public static String checksumValue(byte[] buffer) throws Exception {
		return KMCStringUtil.printHexBinary(checksum(buffer));
	}
}
