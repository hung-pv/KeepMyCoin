package com.keepmycoin.blockchain;

import java.math.BigInteger;

public class SimpleEthereumTransactionInput extends AbstractEthereumTransactionInput {

	public SimpleEthereumTransactionInput(String from, String to, double amtEthereumTransfer, int nonce,
			BigInteger gasPriceInWei, int gasLimit) {
		super(from, to, amtEthereumTransfer, nonce, gasPriceInWei, gasLimit, "");
	}

	public SimpleEthereumTransactionInput(String from, String to, double amtEthereumTransfer, int nonce,
			int gwei, int gasLimit) {
		super(from, to, amtEthereumTransfer, nonce, new BigInteger(gwei + "000000000", 10), gasLimit, "");
	}
}
