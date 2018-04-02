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
package com.keepmycoin.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyStore extends AbstractKMCData {
	@JsonProperty("encryptedKey")
	private String encryptedKey;
	@JsonProperty("checksum")
	private String checksum;

	@JsonGetter("encryptedKey")
	public String getEncryptedKey() {
		return this.encryptedKey;
	}

	@JsonSetter("encryptedKey")
	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}

	@JsonGetter("checksum")
	public String getChecksum() {
		return this.checksum;
	}

	@JsonSetter("checksum")
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	@JsonIgnore
	public byte[] getEncryptedKeyBuffer() {
		return this.decodeToBuffer(this.encryptedKey);
	}

	@JsonIgnore
	public void setEncryptedKeyBuffer(byte[] encryptedKeyBuffer) {
		this.encryptedKey = this.encodeBuffer(encryptedKeyBuffer);
	}
}
