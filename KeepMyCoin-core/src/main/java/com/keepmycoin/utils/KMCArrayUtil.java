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
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(buffer);
		return KMCStringUtil.printHexBinary(digest.digest());
	}
}
