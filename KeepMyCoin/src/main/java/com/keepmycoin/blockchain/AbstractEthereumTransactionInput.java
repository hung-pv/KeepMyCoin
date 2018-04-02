package com.keepmycoin.blockchain;

import java.math.BigInteger;

public abstract class AbstractEthereumTransactionInput extends AbstractTransactionInput {
	private BigInteger gasPriceInWei;
	private int gasLimit;
	private String data;

	public AbstractEthereumTransactionInput(String from, String to, double amtTransfer, //
			int nonce, BigInteger gasPriceInWei, int gasLimit, String data) {
		super(from, to, amtTransfer, nonce);
		this.gasPriceInWei = gasPriceInWei;
		this.gasLimit = gasLimit;
		this.data = data;
	}

	public BigInteger getGasPriceInWei() {
		return gasPriceInWei;
	}

	public int getGWei() {
		return this.gasPriceInWei.divide(new BigInteger("1000000000", 10)).intValue();
	}

	public void setGasPriceInWei(BigInteger gasPriceInWei) {
		this.gasPriceInWei = gasPriceInWei;
	}

	public void setGasPriceInWei(int gasPriceInWei) {
		this.gasPriceInWei = new BigInteger(String.valueOf(gasPriceInWei), 10);
	}

	public int getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(int gasLimit) {
		this.gasLimit = gasLimit;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
