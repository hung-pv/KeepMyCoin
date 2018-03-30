package com.keepmycoin.jsdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EtherSignedTx {
	@JsonProperty("nonce")
	private String nonce;
	@JsonProperty("gasPrice")
	private String gasPrice;
	@JsonProperty("gasLimit")
	private String gasLimit;
	@JsonProperty("to")
	private String to;
	@JsonProperty("value")
	private String value;
	@JsonProperty("data")
	private String data;
	@JsonProperty("rawTx")
	private String rawTx;
	@JsonProperty("signedTx")
	private String signedTx;
	@JsonProperty("isError")
	private boolean isError;
	@JsonProperty("e")
	private String e;

	@JsonGetter("nonce")
	public String getNonce() {
		return this.nonce;
	}

	@JsonSetter("nonce")
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	@JsonGetter("gasPrice")
	public String getGasPrice() {
		return this.gasPrice;
	}

	@JsonSetter("gasPrice")
	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}

	@JsonGetter("gasLimit")
	public String getGasLimit() {
		return this.gasLimit;
	}

	@JsonSetter("gasLimit")
	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
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
	public String getValue() {
		return this.value;
	}

	@JsonSetter("value")
	public void setValue(String value) {
		this.value = value;
	}

	@JsonGetter("data")
	public String getData() {
		return this.data;
	}

	@JsonSetter("data")
	public void setData(String data) {
		this.data = data;
	}

	@JsonGetter("rawTx")
	public String getRawTx() {
		return this.rawTx;
	}

	@JsonSetter("rawTx")
	public void setRawTx(String rawTx) {
		this.rawTx = rawTx;
	}

	@JsonGetter("signedTx")
	public String getSignedTx() {
		return this.signedTx;
	}

	@JsonSetter("signedTx")
	public void setSignedTx(String signedTx) {
		this.signedTx = signedTx;
	}

	@JsonGetter("isError")
	public boolean isIsError() {
		return this.isError;
	}

	@JsonSetter("isError")
	public void setIsError(boolean isError) {
		this.isError = isError;
	}

	@JsonGetter("e")
	public String getE() {
		return this.e;
	}

	@JsonSetter("e")
	public void setE(String e) {
		this.e = e;
	}
}
