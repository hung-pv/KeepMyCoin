package com.keepmycoin.utils;

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
}
