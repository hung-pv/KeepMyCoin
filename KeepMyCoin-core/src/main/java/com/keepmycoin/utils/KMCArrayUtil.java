package com.keepmycoin.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KMCArrayUtil {
	public static byte[] randomBytes(int size) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[size];
		SecureRandom.getInstanceStrong().nextBytes(bytes);
		return bytes;
	}
}
