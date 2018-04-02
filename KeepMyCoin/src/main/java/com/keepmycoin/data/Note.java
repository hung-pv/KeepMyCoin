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
