/*-
 * Based on sample code at
 * https://www.programcreek.com/java-api-examples/index.php?api=org.bouncycastle.crypto.paddings.PKCS7Padding
 */

package com.keepmycoin.crypto;

import com.keepmycoin.JavaScript;
import com.keepmycoin.utils.KMCJavaScriptUtil;
import com.keepmycoin.utils.KMCStringUtil;

public class AES {

	public static byte[] encrypt(byte[] data, String key) throws Exception {
		return encrypt(data, KMCStringUtil.getBytes(key, 32));
	}

	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		StringBuilder additionalScript = new StringBuilder();
		additionalScript.append("var aesKey = ");
		additionalScript.append(KMCJavaScriptUtil.buildJavaScriptArray(key));
		additionalScript.append(";\n");
		
		additionalScript.append("var buffer = ");
		additionalScript.append(KMCJavaScriptUtil.buildJavaScriptArray(data));
		additionalScript.append(";\n");
		
		additionalScript.append("var encrypted = encryptAES(aesKey, buffer);");
		
		Object result = KMCJavaScriptUtil.executeAndGetValue("encrypted", additionalScript.toString(), JavaScript.ENGINE_AES);
		byte[] buffer = KMCStringUtil.parseHexBinary(String.valueOf(result));
		return buffer;
	}

	public static byte[] decrypt(byte[] data, String key) throws Exception {
		return decrypt(data, KMCStringUtil.getBytes(key, 32));
	}

	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		StringBuilder additionalScript = new StringBuilder();
		additionalScript.append("var aesKey = ");
		additionalScript.append(KMCJavaScriptUtil.buildJavaScriptArray(key));
		additionalScript.append(";\n");
		
		additionalScript.append("var buffer = ");
		additionalScript.append(KMCJavaScriptUtil.buildJavaScriptArray(data));
		additionalScript.append(";\n");
		
		additionalScript.append("var decrypted = decryptAES(aesKey, buffer);");
		
		String decrypted = String.valueOf(KMCJavaScriptUtil.executeAndGetValue("decrypted", additionalScript.toString(), JavaScript.ENGINE_AES));
		String[] spl = decrypted.split("\\s*\\,\\s*");
		byte[] result = new byte[spl.length];
		for(int i = 0; i < spl.length; i++) {
			result[i] = (byte)Integer.parseInt(spl[i]);
		}
		return result;
	}

	private byte[] key;

	public AES(byte[] key) {
		this.key = key;
	}

	public byte[] encrypt(byte[] data) throws Exception {
		return AES.encrypt(data, this.key);
	}

	public byte[] encryptNullable(byte[] data) throws Exception {
		if (data == null)
			return null;
		return encrypt(data);
	}

	public byte[] decrypt(byte[] data) throws Exception {
		return AES.decrypt(data, this.key);
	}

	public byte[] decryptNullable(byte[] data) throws Exception {
		if (data == null)
			return null;
		return decrypt(data);
	}
}