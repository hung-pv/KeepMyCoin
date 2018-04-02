/*******************************************************************************
 * Copyright 2018 HungPV
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.keepmycoin.blockchain;

import com.keepmycoin.exception.UnlockMethodNotSupportedException;
import com.keepmycoin.js.JavaScript;
import com.keepmycoin.js.data.EtherSignedTx;
import com.keepmycoin.js.data.EtherTxInfo;
import com.keepmycoin.utils.KMCJsonUtil;

public class EthereumBlockChain implements IBlockChain {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EthereumBlockChain.class);

	@Override
	public ISignedTransaction signSimpleTransaction(ITransactionInput input, IUnlockMethod unlockMethod) throws Exception {
		if (!(input instanceof SimpleEthereumTransactionInput)) return null;
		if (!(unlockMethod instanceof UnlockByPrivateKey)) throw new UnlockMethodNotSupportedException(unlockMethod);
		SimpleEthereumTransactionInput txInput = (SimpleEthereumTransactionInput)input;
		UnlockByPrivateKey privKeyUnlockMethod = (UnlockByPrivateKey)unlockMethod;
		
		EtherTxInfo txi = new EtherTxInfo(txInput.getFrom(), txInput.getTo(), txInput.getAmtTransfer(), txInput.getNonce(), txInput.getGWei(), txInput.getGasLimit(), privKeyUnlockMethod.getPrivateKey());
		JavaScript.ENGINE_MEW.execute("mew.signEtherTx('" + KMCJsonUtil.toJSon(txi) + "');");

		String tmp = "has" + txi.getGuid();
		do {
			Thread.sleep(50);
		} while(!Boolean.valueOf(JavaScript.ENGINE_MEW.executeAndGetValue("var " + tmp + " = resultStorage.has('" + txi.getGuid() + "');", tmp)));

		String json = "json" + txi.getGuid();
		String tx = JavaScript.ENGINE_MEW.executeAndGetValue("var " + json + " = resultStorage['" + txi.getGuid() + "'];", json);
		log.debug(tx);
		EtherSignedTx est = KMCJsonUtil.parse(tx, EtherSignedTx.class);
		return est == null || est.isIsError() ? null : new EthereumSignedTransaction(txInput.getFrom(), est.getTo(), est.getValue(), est.getRawTx(), est.getSignedTx(), est.getNonce(), est.getGasPrice(), est.getGasLimit(), est.getData());
	}

	@Override
	public ISignedTransaction signContractTransaction(ITransactionInput input, IUnlockMethod unlockMethod) {
		// TODO Implement later
		return null;
	}
}
