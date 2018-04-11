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

import java.util.Date;
import java.util.Calendar;

import com.keepmycoin.exception.UnlockMethodNotSupportedException;
import com.keepmycoin.js.JavaScript;
import com.keepmycoin.js.data.EtherSignedTx;
import com.keepmycoin.js.data.EtherTxInfo;
import com.keepmycoin.utils.KMCJsonUtil;

import jdk.nashorn.api.scripting.JSObject;

public class EthereumBlockChain implements IBlockChain {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EthereumBlockChain.class);

	@Override
	public ISignedTransaction signSimpleTransaction(ITransactionInput input, IUnlockMethod unlockMethod) throws Exception {
		log.trace("signSimpleTransaction");
		if (!(input instanceof SimpleEthereumTransactionInput)) return null;
		if (!(unlockMethod instanceof UnlockByPrivateKey)) throw new UnlockMethodNotSupportedException(unlockMethod);
		SimpleEthereumTransactionInput txInput = (SimpleEthereumTransactionInput)input;
		UnlockByPrivateKey privKeyUnlockMethod = (UnlockByPrivateKey)unlockMethod;
		
		EtherTxInfo txi = new EtherTxInfo(txInput.getFrom(), txInput.getTo(), txInput.getAmtTransfer(), txInput.getNonce(), txInput.getGWei(), txInput.getGasLimit(), privKeyUnlockMethod.getPrivateKey());

		String jsonObj = KMCJsonUtil.toJSon(txi);
		String jsonObjVar = String.format("jsonObjVar%s", txi.getGuid());
		JSObject jsObj = JavaScript.ENGINE_MEW.putVariableAndGetJsObj(jsonObjVar, jsonObj);
		JavaScript.ENGINE_MEW.freeVariable(jsonObjVar);
		JavaScript.ENGINE_MEW.invokeFunction("mew_signEtherTx", jsObj);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 30);
		Date expire = cal.getTime();
		while(true) {
			Object check = JavaScript.ENGINE_MEW.invokeFunction("mew_resultStorageHas", txi.getGuid());
			log.debug(check);
			if (Boolean.valueOf(String.valueOf(check))) {
				break;
			}
			Thread.sleep(50);
			log.debug("Waiting...");
			if (new Date().after(expire)) {
				return null;
			}
		}

		String tx = String.valueOf(JavaScript.ENGINE_MEW.invokeFunction("mew_resultStorageGet", txi.getGuid()));
		try {
			JavaScript.ENGINE_MEW.invokeFunction("mew_removeResult", txi.getGuid());// free the memory
		} catch (Exception e) {
		}
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
