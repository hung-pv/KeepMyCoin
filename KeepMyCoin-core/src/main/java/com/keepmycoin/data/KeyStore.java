package com.keepmycoin.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyStore extends AbstractKMCData {
	@JsonProperty("encryptedKey")
	private String encryptedKey;

	@JsonGetter("encryptedKey")
	public String getEncryptedKey() {
		return this.encryptedKey;
	}

	@JsonSetter("encryptedKey")
	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
	
	@JsonIgnore
	public byte[] getEncryptedKeyBuffer() {
		return Base64.getDecoder().decode(this.encryptedKey);
	}

	@JsonIgnore
	public void setEncryptedKeyBuffer(byte[] encryptedKeyBuffer) {
		this.encryptedKey = Base64.getEncoder().encodeToString(encryptedKeyBuffer);
	}
}
