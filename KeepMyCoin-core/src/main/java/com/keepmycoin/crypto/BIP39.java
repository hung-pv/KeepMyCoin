package com.keepmycoin.crypto;

import com.keepmycoin.JavaScript;
import com.keepmycoin.utils.KMCStringUtil;

public class BIP39 {
	public static String entropyToMnemonic(byte[] entropyBuffer) throws Exception {
		return entropyToMnemonic(KMCStringUtil.printHexBinary(entropyBuffer));
	}
	
	public static String entropyToMnemonic(String entropy) throws Exception {
		StringBuilder script = new StringBuilder();
		script.append("var mnemonic = window.hd.bip39.entropyToMnemonic('");
		script.append(entropy);
		script.append("');");
		return JavaScript.ENGINE_MEW.executeAndGetValue(script, "mnemonic");
	}

	public static byte[] mnemonicToEntropyBuffer(String mnemonic) throws Exception {
		return KMCStringUtil.parseHexBinary(mnemonicToEntropy(mnemonic));
	}

	public static String mnemonicToEntropy(String mnemonic) throws Exception {
		StringBuilder script = new StringBuilder();
		script.append("var entropy = window.hd.bip39.mnemonicToEntropy('");
		script.append(mnemonic);
		script.append("');");
		return JavaScript.ENGINE_MEW.executeAndGetValue(script, "entropy");
	}
}
