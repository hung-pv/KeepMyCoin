package com.keepmycoin;

import com.keepmycoin.annotation.Continue;
import com.keepmycoin.annotation.RequiredKeystore;

public interface IKeepMyCoin {
	void launch() throws Exception;
	
	void loadKeystore() throws Exception;

	@Continue
	void generateNewKeystore() throws Exception;

	@RequiredKeystore
	@Continue
	void saveAWallet() throws Exception;
}
