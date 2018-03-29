package com.keepmycoin.crypto;

import com.keepmycoin.JavaScript;
import com.keepmycoin.utils.KMCJavaScriptUtil;
import com.keepmycoin.utils.KMCStringUtil;

public class BIP39 {
	public static String entropyToMnemonic(byte[] entropyBuffer) throws Exception {
		return entropyToMnemonic(KMCStringUtil.printHexBinary(entropyBuffer));
	}
	
	public static String entropyToMnemonic(String entropy) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("var mnemonic = window.hd.bip39.entropyToMnemonic('");
		sb.append(entropy);
		sb.append("');");
		return String.valueOf(KMCJavaScriptUtil.executeAndGetValue("mnemonic", sb.toString(), JavaScript.ENGINE_MEW));
	}

	public static byte[] mnemonicToEntropyBuffer(String mnemonic) throws Exception {
		return KMCStringUtil.parseHexBinary(mnemonicToEntropy(mnemonic));
	}

	public static String mnemonicToEntropy(String mnemonic) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("var entropy = window.hd.bip39.mnemonicToEntropy('");
		sb.append(mnemonic);
		sb.append("');");
		return String.valueOf(KMCJavaScriptUtil.executeAndGetValue("entropy", sb.toString(), JavaScript.ENGINE_MEW));
	}
}
