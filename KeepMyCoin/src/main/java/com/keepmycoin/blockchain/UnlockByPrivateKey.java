package com.keepmycoin.blockchain;

public class UnlockByPrivateKey implements IUnlockMethod {
	private String privateKey;

	public UnlockByPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
