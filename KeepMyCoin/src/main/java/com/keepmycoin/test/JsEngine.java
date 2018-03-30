package com.keepmycoin.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.keepmycoin.JavaScript;
import com.keepmycoin.crypto.AES;

public class JsEngine {
	
	private static final String MNEMONIC = "maid night clock rice destroy laugh sick stuff shy venture exhaust angle scare minute vast source diagram stereo cost recycle dose diesel only trend";

	@Test
	public void testMnemonic() throws Exception {
		String mnemonic = getMnemonic(SignTx.PRVK_FROM);
		assertNotNull(mnemonic);
		assertEquals(MNEMONIC, mnemonic, MNEMONIC);
	}

	@Test
	public void testEntropy() throws Exception {
		String entropy = getEntropy(MNEMONIC);
		assertNotNull(entropy);
		assertEquals(SignTx.PRVK_FROM, entropy, SignTx.PRVK_FROM);
	}

	@Test
	public void testBothEntropyAndMnemonic() throws Exception {
		String mnemonic = getMnemonic(SignTx.PRVK_FROM);
		String entropy = getEntropy(mnemonic);
		assertEquals("Original must equals entropy", SignTx.PRVK_FROM, entropy);
		
		entropy = getEntropy(MNEMONIC);
		mnemonic = getMnemonic(entropy);
		assertEquals("Original must equals mnemonic", MNEMONIC, mnemonic);
	}

	@Test
	public void testAES() throws Exception {
		byte[] key = new byte[32];
		byte[] data = new byte[32];
		for (int i = 0; i < key.length; i++) {
			key[i] = (byte) (i + 1);
			data[i] = (byte) (i + 1);
		}
		byte[] dataWithAES = AES.encrypt(data, key);
		byte[] dataToVerify = AES.decrypt(dataWithAES, key);
		
		assertArrayEquals(dataWithAES, dataToVerify);
	}

	private String getEntropy(String mnemonic) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("var entropy = window.hd.bip39.mnemonicToEntropy('");
		sb.append(mnemonic);
		sb.append("');");
		return JavaScript.ENGINE_MEW.executeAndGetValue(sb, "entropy");
	}

	private String getMnemonic(String entropy) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("var mnemonic = window.hd.bip39.entropyToMnemonic('");
		sb.append(entropy);
		sb.append("');");
		return JavaScript.ENGINE_MEW.executeAndGetValue(sb, "mnemonic");
	}
}
