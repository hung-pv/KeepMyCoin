package com.keepmycoin.validator;

public interface IBlockChainValidator {
	boolean isValidAddress(String address);
	boolean isValidPrivateKey(String privKey);
}
