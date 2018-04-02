package com.keepmycoin.blockchain;

import java.math.BigInteger;

import com.keepmycoin.utils.KMCNumberUtil;

public abstract class AbstractSignedTransaction implements ISignedTransaction {
	private String from;
	private String to;
	private BigInteger transferAmt;
	private String rawTx;
	private String signedTx;
	
	public AbstractSignedTransaction(String from, String to, BigInteger transferAmt, String rawTx, String signedTx) {
		this.from = from;
		this.to = to;
		this.transferAmt = transferAmt;
		this.rawTx = rawTx;
		this.signedTx = signedTx;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigInteger getTransferAmt() {
		return transferAmt;
	}

	public String getTransferAmt(int noOfDownDigits) {
		return KMCNumberUtil.fromBigValue(this.transferAmt, noOfDownDigits);
	}

	public void setTransferAmt(BigInteger transferAmt) {
		this.transferAmt = transferAmt;
	}

	public String getRawTx() {
		return rawTx;
	}

	public void setRawTx(String rawTx) {
		this.rawTx = rawTx;
	}

	public String getSignedTx() {
		return signedTx;
	}

	public void setSignedTx(String signedTx) {
		this.signedTx = signedTx;
	}
}
