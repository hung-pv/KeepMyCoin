package com.keepmycoin.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wallet extends AbstractKMCData {

	@JsonIgnoreType
	public enum WalletType {
		ERC20("Ethereum (and ERC20 tokens)"), BIT("Bitcoin based"), Other("Other");

		private String displayText;

		private WalletType(String displayText) {
			this.displayText = displayText;
		}

		public String getDisplayText() {
			return displayText;
		}
	}

	@JsonProperty("address")
	private String address;
	@JsonProperty("encryptedPrivateKey")
	private String encryptedPrivateKey;
	@JsonProperty("walletType")
	private String walletType;
	@JsonProperty("encryptedMnemonic")
	private String encryptedMnemonic;
	@JsonProperty("publicNote")
	private String publicNote;
	@JsonProperty("encryptedPrivateNote")
	private String encryptedPrivateNote;

	public Wallet(String address, //
			byte[] encryptedPrivateKeyBuffer, String walletType, //
			byte[] encryptedMnemonicBuffer, //
			String publicNote, byte[] encryptedPrivateNoteBuffer) {
		this.address = address;
		setEncryptedPrivateKeyBuffer(encryptedPrivateKeyBuffer);
		this.walletType = walletType;
		setEncryptedMnemonicBuffer(encryptedMnemonicBuffer);
		this.publicNote = publicNote;
		setEncryptedPrivateNoteBuffer(encryptedPrivateNoteBuffer);
	}

	public Wallet() {
		super();
		this.address = null;
		this.encryptedPrivateKey = null;
		this.walletType = null;
		this.encryptedMnemonic = null;
		this.publicNote = null;
		this.encryptedPrivateNote = null;
	}

	@JsonGetter("address")
	public String getAddress() {
		return this.address;
	}

	@JsonSetter("address")
	public void setAddress(String address) {
		this.address = address;
	}

	@JsonGetter("encryptedPrivateKey")
	public String getEncryptedPrivateKey() {
		return this.encryptedPrivateKey;
	}

	@JsonSetter("encryptedPrivateKey")
	public void setEncryptedPrivateKey(String encryptedPrivateKey) {
		this.encryptedPrivateKey = encryptedPrivateKey;
	}

	@JsonGetter("walletType")
	public String getWalletType() {
		return this.walletType;
	}

	@JsonIgnore
	public WalletType getWalletTypeEnum() {
		try {
			return WalletType.valueOf(this.walletType);
		} catch (Exception e) {
			return null;
		}
	}

	@JsonIgnore
	public boolean is(WalletType type) {
		WalletType wt = getWalletTypeEnum();
		return wt != null && wt == type;
	}

	@JsonSetter("walletType")
	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

	@JsonGetter("encryptedMnemonic")
	public String getEncryptedMnemonic() {
		return this.encryptedMnemonic;
	}

	@JsonSetter("encryptedMnemonic")
	public void setEncryptedMnemonic(String encryptedMnemonic) {
		this.encryptedMnemonic = encryptedMnemonic;
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
	public byte[] getEncryptedPrivateKeyBuffer() {
		return this.decodeToBuffer(this.encryptedPrivateKey);
	}

	@JsonIgnore
	public void setEncryptedPrivateKeyBuffer(byte[] encryptedPrivateKeyBuffer) {
		this.encryptedPrivateKey = this.encodeBuffer(encryptedPrivateKeyBuffer);
	}

	@JsonIgnore
	public byte[] getEncryptedMnemonicBuffer() {
		return this.decodeToBuffer(this.encryptedMnemonic);
	}

	@JsonIgnore
	public void setEncryptedMnemonicBuffer(byte[] encryptedMnemonicBuffer) {
		this.encryptedMnemonic = this.encodeBuffer(encryptedMnemonicBuffer);
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
