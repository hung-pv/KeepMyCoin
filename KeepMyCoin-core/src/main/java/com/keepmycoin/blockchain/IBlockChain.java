package com.keepmycoin.blockchain;

public interface IBlockChain {
	ISignedTransaction signSimpleTransaction(ITransactionInput input, IUnlockMethod unlockMethod) throws Exception;
	ISignedTransaction signContractTransaction(ITransactionInput input, IUnlockMethod unlockMethod) throws Exception;
}
