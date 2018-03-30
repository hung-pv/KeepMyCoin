package com.keepmycoin;

import com.keepmycoin.annotation.Continue;
import com.keepmycoin.annotation.RequiredKeystore;

public interface IKeepMyCoin {
	void launch() throws Exception;

	void loadKeystore() throws Exception;

	@Continue
	void generateNewKeystore() throws Exception;

	@Continue
	void restoreKeystore() throws Exception;

	@RequiredKeystore
	@Continue
	void saveAWallet() throws Exception;

	@RequiredKeystore
	@Continue
	void readAWallet() throws Exception;

	@RequiredKeystore
	@Continue
	void saveAnAccount() throws Exception;

	@RequiredKeystore
	@Continue
	void readAnAccount() throws Exception;

	@RequiredKeystore
	@Continue
	void signTransaction() throws Exception;
}
