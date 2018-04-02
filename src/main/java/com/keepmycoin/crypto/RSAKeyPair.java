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

import java.security.Key;
import java.util.Base64;

public class RSAKeyPair {
	private Key publicKey;
	private Key privateKey;

	public RSAKeyPair(Key publicKey, Key privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public Key getPublicKey() {
		return this.publicKey;
	}

	public byte[] getPublicKeyRaw() {
		return this.publicKey.getEncoded();
	}

	public String getPublicKeyAsString() {
		return Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
	}

	public Key getPrivateKey() {
		return this.privateKey;
	}

	public byte[] getPrivateKeyRaw() {
		return this.privateKey.getEncoded();
	}

	public String getPrivateKeyAsString() {
		return Base64.getEncoder().encodeToString(this.privateKey.getEncoded());
	}
}
