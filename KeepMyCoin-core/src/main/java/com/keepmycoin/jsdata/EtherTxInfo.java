package com.keepmycoin.jsdata;

import java.math.BigInteger;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.keepmycoin.utils.KMCNumberUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EtherTxInfo {
	@JsonProperty("guid")
	private String guid;
	@JsonProperty("from")
	private String from;
	@JsonProperty("to")
	private String to;
	@JsonProperty("value")
	private double value;
	@JsonProperty("nonce")
	private int nonce;
	@JsonProperty("gwei")
	private int gwei;
	@JsonProperty("gasLimit")
	private int gasLimit;
	@JsonProperty("privKey")
	private String privKey;
	@JsonProperty("nonceHex")
	private String nonceHex;
	@JsonProperty("gasPriceHex")
	private String gasPriceHex;
	
	public EtherTxInfo() {
	}

	public EtherTxInfo(String from, String to, double value, int nonce, int gwei, int gasLimit, String privKey) {
		this.guid = UUID.randomUUID().toString().replaceAll("[^aA-zZ0-9]", "");
		this.from = from;
		this.to = to;
		this.value = value;
		this.nonce = nonce;
		this.gwei = gwei;
		this.gasLimit = gasLimit;
		this.privKey = privKey;
		this.gasPriceHex = "0x" + KMCNumberUtil.convertBigIntegerToHex(new BigInteger(gwei + "000000000" /* Gwei to Wei (multiply by a billion) */, 10));
		this.nonceHex = "0x" + KMCNumberUtil.convertBigIntegerToHex(new BigInteger(String.valueOf(this.nonce), 10));
	}

	@JsonGetter("guid")
	public String getGuid() {
		return this.guid;
	}

	@JsonSetter("guid")
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@JsonGetter("from")
	public String getFrom() {
		return this.from;
	}

	@JsonSetter("from")
	public void setFrom(String from) {
		this.from = from;
	}

	@JsonGetter("to")
	public String getTo() {
		return this.to;
	}

	@JsonSetter("to")
	public void setTo(String to) {
		this.to = to;
	}

	@JsonGetter("value")
	public double getValue() {
		return this.value;
	}

	@JsonSetter("value")
	public void setValue(double value) {
		this.value = value;
	}

	@JsonGetter("nonce")
	public int getNonce() {
		return this.nonce;
	}

	@JsonSetter("nonce")
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	@JsonGetter("gwei")
	public int getGwei() {
		return this.gwei;
	}

	@JsonSetter("gwei")
	public void setGwei(int gwei) {
		this.gwei = gwei;
	}

	@JsonGetter("gasLimit")
	public int getGasLimit() {
		return this.gasLimit;
	}

	@JsonSetter("gasLimit")
	public void setGasLimit(int gasLimit) {
		this.gasLimit = gasLimit;
	}

	@JsonGetter("privKey")
	public String getPrivKey() {
		return this.privKey;
	}

	@JsonSetter("privKey")
	public void setPrivKey(String privKey) {
		this.privKey = privKey;
	}

	@JsonGetter("nonceHex")
	public String getNonceHex() {
		return this.nonceHex;
	}

	@JsonSetter("nonceHex")
	public void setNonceHex(String nonceHex) {
		this.nonceHex = nonceHex;
	}

	@JsonGetter("gasPriceHex")
	public String getGasPriceHex() {
		return this.gasPriceHex;
	}

	@JsonSetter("gasPriceHex")
	public void setGasPriceHex(String gasPriceHex) {
		this.gasPriceHex = gasPriceHex;
	}
}
