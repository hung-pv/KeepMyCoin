package com.keepmycoin.validator.crypto.eth;

import com.keepmycoin.validator.ERC20BlockChainValidator;
import com.keepmycoin.validator.ValidateRegex;

public class ValidatorEthPrivateKey extends ValidateRegex {
	@Override
	public String describleWhenInvalid() {
		return "Must be a valid ethereum private key";
	}

	@Override
	public String getPattern() {
		return null;
	}

	@Override
	public boolean isValid(String input) {
		return new ERC20BlockChainValidator().isValidPrivateKey(input);
	}
}
