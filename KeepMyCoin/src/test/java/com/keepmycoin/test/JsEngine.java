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
package com.keepmycoin.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.keepmycoin.crypto.AES;
import com.keepmycoin.crypto.BIP39;

public class JsEngine {
	
	private static final String MNEMONIC = "maid night clock rice destroy laugh sick stuff shy venture exhaust angle scare minute vast source diagram stereo cost recycle dose diesel only trend";

	@Test
	public void testMnemonic() throws Exception {
		String mnemonic = BIP39.entropyToMnemonic(SignTx.PRVK_FROM);
		assertNotNull(mnemonic);
		assertEquals(MNEMONIC, mnemonic, MNEMONIC);
	}

	@Test
	public void testEntropy() throws Exception {
		String entropy = BIP39.mnemonicToEntropy(MNEMONIC);
		assertNotNull(entropy);
		assertEquals(SignTx.PRVK_FROM, entropy, SignTx.PRVK_FROM);
	}

	@Test
	public void testBothEntropyAndMnemonic() throws Exception {
		String mnemonic = BIP39.entropyToMnemonic(SignTx.PRVK_FROM);
		String entropy = BIP39.mnemonicToEntropy(mnemonic);
		assertEquals("Original must equals entropy", SignTx.PRVK_FROM, entropy);
		
		entropy = BIP39.mnemonicToEntropy(MNEMONIC);
		mnemonic = BIP39.entropyToMnemonic(entropy);
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
		
		assertArrayEquals(data, dataToVerify);
	}
}
