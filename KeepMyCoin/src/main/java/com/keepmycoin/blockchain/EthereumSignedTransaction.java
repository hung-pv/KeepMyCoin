package com.keepmycoin.blockchain;

import java.math.BigInteger;

import com.keepmycoin.utils.KMCNumberUtil;

public class EthereumSignedTransaction extends AbstractSignedTransaction {
	private int nonce;
	private BigInteger wei;
	private int gasLimit;
	private String data;
	
	public EthereumSignedTransaction(String from, String to, String transferAmtHex, String rawTx, String signedTx,
			String nonceHex, String gasPriceWeiHex, String gasLimitHex, String data) {
		super(from, to, KMCNumberUtil.fromHexToBigInteger(transferAmtHex), rawTx, signedTx);
		this.nonce = KMCNumberUtil.fromHexToBigInteger(nonceHex).intValue();
		this.wei = KMCNumberUtil.fromHexToBigInteger(gasPriceWeiHex);
		this.gasLimit = KMCNumberUtil.fromHexToBigInteger(gasLimitHex).intValue();
		this.data = data;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	
	public BigInteger getWei() {
		return wei;
	}
	
	public int getGwei() {
		return this.wei.divide(new BigInteger("1000000000", 10)).intValue();
	}

	public void setWei(BigInteger wei) {
		this.wei = wei;
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

	@Override
	public String toString() {
		return "EthereumSignedTransaction [nonce=" + nonce + ", wei=" + wei + ", gasLimit=" + gasLimit + ", data="
				+ data + ", getNonce()=" + getNonce() + ", getWei()=" + getWei() + ", getGwei()=" + getGwei()
				+ ", getGasLimit()=" + getGasLimit() + ", getData()=" + getData() + ", getFrom()=" + getFrom()
				+ ", getTo()=" + getTo() + ", getTransferAmt()=" + getTransferAmt() + ", getRawTx()=" + getRawTx()
				+ ", getSignedTx()=" + getSignedTx() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
}
