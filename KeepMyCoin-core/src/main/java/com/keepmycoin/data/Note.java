package com.keepmycoin.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Note extends AbstractKMCData {
	@JsonProperty("title")
	private String title;
	@JsonProperty("publicNote")
	private String publicNote;
	@JsonProperty("encryptedPrivateNote")
	private String encryptedPrivateNote;

	@JsonGetter("title")
	public String getTitle() {
		return this.title;
	}

	@JsonSetter("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonGetter("publicNote")
	public String getPublicNote() {
		return this.publicNote;
	}

	@JsonSetter("publicNote")
	public void setPublicNote(String publicNote) {
		this.publicNote = publicNote;
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
	public byte[] getEncryptedPrivateNoteBuffer() {
		return this.decodeToBuffer(this.encryptedPrivateNote);
	}

	@JsonIgnore
	public void setEncryptedPrivateNoteBuffer(byte[] encryptedPrivateNoteBuffer) {
		this.encryptedPrivateNote = this.encodeBuffer(encryptedPrivateNoteBuffer);
	}
}
