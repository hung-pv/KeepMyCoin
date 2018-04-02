package com.keepmycoin.exception;

import com.keepmycoin.blockchain.IUnlockMethod;

public class UnlockMethodNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 4813774159028837174L;

	public UnlockMethodNotSupportedException(IUnlockMethod unlockMethod) {
		super(String.format("Unlock method %s had not been supported", unlockMethod.getClass().getSimpleName()));
	}
}
