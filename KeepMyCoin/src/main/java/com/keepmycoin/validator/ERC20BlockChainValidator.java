package com.keepmycoin.validator;

import java.util.regex.Pattern;

public class ERC20BlockChainValidator implements IBlockChainValidator {
	@Override
	public boolean isValidAddress(String address) {
		return address != null && Pattern.matches("(0[xX])?[aA-zZ0-9]{40}", address.trim());
	}

	@Override
	public boolean isValidPrivateKey(String privKey) {
		return privKey != null && Pattern.matches("[aA-zZ0-9]{64}", privKey.trim());
	}
}
