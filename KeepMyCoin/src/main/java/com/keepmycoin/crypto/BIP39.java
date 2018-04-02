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
package com.keepmycoin.crypto;

import com.keepmycoin.js.JavaScript;
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
