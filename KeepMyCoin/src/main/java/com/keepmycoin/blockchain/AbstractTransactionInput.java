package com.keepmycoin.blockchain;

public abstract class AbstractTransactionInput implements ITransactionInput {
	private String from;
	private String to;
	private double amtTransfer;
	private int nonce;

	public AbstractTransactionInput() {
	}

	public AbstractTransactionInput(String from, String to, double amtTransfer, int nonce) {
		this.from = from;
		this.to = to;
		this.amtTransfer = amtTransfer;
		this.nonce = nonce;
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

	public double getAmtTransfer() {
		return amtTransfer;
	}

	public void setAmtTransfer(double amtTransfer) {
		this.amtTransfer = amtTransfer;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

}
