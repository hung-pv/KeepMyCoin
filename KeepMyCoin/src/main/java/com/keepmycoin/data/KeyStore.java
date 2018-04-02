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