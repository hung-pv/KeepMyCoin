package com.keepmycoin.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends AbstractKMCData {
	@JsonProperty("name")
	private String name;
	@JsonProperty("website")
	private String website;
	@JsonProperty("publicNote")
	private String publicNote;
	@JsonProperty("encryptedPassword")
	private String encryptedPassword;
	@JsonProperty("encrypted2FA")
	private String encrypted2FA;
	@JsonProperty("encryptedPrivateNote")
	private String encryptedPrivateNote;
	
	public Account() {
	}

	public Account(String name, String website, String publicNote, //
			byte[] encryptedPasswordBuffer, byte[] encrypted2faBuffer, byte[] encryptedPrivateNoteBuffer) {
		this.name = name;
		this.website = website;
		this.publicNote = publicNote;
		this.setEncryptedPasswordBuffer(encryptedPasswordBuffer);
		this.setEncrypted2FABuffer(encrypted2faBuffer);
		this.setEncryptedPrivateNoteBuffer(encryptedPrivateNoteBuffer);
	}

	@JsonGetter("name")
	public String getName() {
		return this.name;
	}

	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonGetter("website")
	public String getWebsite() {
		return this.website;
	}

	@JsonSetter("website")
	public void setWebsite(String website) {
		this.website = website;
	}

	@JsonGetter("publicNote")
	public String getPublicNote() {
		return this.publicNote;
	}

	@JsonSetter("publicNote")
	public void setPublicNote(String publicNote) {
		this.publicNote = publicNote;
	}

	@JsonGetter("encryptedPassword")
	public String getEncryptedPassword() {
		return this.encryptedPassword;
	}

	@JsonSetter("encryptedPassword")
	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	@JsonGetter("encrypted2FA")
	public String getEncrypted2FA() {
		return this.encrypted2FA;
	}

	@JsonSetter("encrypted2FA")
	public void setEncrypted2FA(String encrypted2FA) {
		this.encrypted2FA = encrypted2FA;
	}

	@JsonGetter("encryptedPrivateNote")
	public String getEncryptedPrivateNote() {
		return this.encryptedPrivateNote;
	}

	@JsonSetter("encryptedPrivateNote")
	public void setEncryptedPrivateNote(String encryptedPrivateNote) {
		this.encryptedPrivateNote = encryptedPrivateNote;
	}

	@JsonIgnore
	public byte[] getEncryptedPasswordBuffer() {
		return this.decodeToBuffer(this.encryptedPassword);
	}

	@JsonIgnore
	public void setEncryptedPasswordBuffer(byte[] encryptedPasswordBuffer) {
		this.encryptedPassword = this.encodeBuffer(encryptedPasswordBuffer);
	}

	@JsonIgnore
	public byte[] getEncrypted2FABuffer() {
		return this.decodeToBuffer(this.encrypted2FA);
	}

	@JsonIgnore
	public void setEncrypted2FABuffer(byte[] encrypted2FABuffer) {
		this.encrypted2FA = this.encodeBuffer(encrypted2FABuffer);
	}

	@JsonIgnore
	public byte[] getEncryptedPrivateNoteBuffer() {
		return this.decodeToBuffer(this.encryptedPrivateNote);
	}

	@JsonIgnore
	public void setEncryptedPrivateNoteBuffer(byte[] encryptedPrivateNoteBuffer) {
		this.encryptedPrivateNote = this.encodeBuffer(encryptedPrivateNoteBuffer);
	}
}
