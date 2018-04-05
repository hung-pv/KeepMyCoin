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
package com.keepmycoin.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.keepmycoin.blockchain.EthereumBlockChain;
import com.keepmycoin.blockchain.EthereumSignedTransaction;
import com.keepmycoin.blockchain.ISignedTransaction;
import com.keepmycoin.blockchain.ITransactionInput;
import com.keepmycoin.blockchain.IUnlockMethod;
import com.keepmycoin.blockchain.SimpleEthereumTransactionInput;
import com.keepmycoin.blockchain.UnlockByPrivateKey;
import com.keepmycoin.js.JavaScript;
import com.keepmycoin.js.data.EtherSignedTx;
import com.keepmycoin.js.data.EtherTxInfo;
import com.keepmycoin.utils.KMCJsonUtil;

public class SignTx {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SignTx.class);
	
	private static final String ADDR_FROM = "0xd8C61D0719f54fDf2BF794135b6b0384AC89deFE";
	public static final String PRVK_FROM = "8612b0addc93c2fb31f6bcc79e493d846c051abc6e803cfab0c2d9f4187b26c7";
	private static final String ADDR_TO = "0xF5f3300d2021A81DB40330d7761281B736abd56d";
	private static final int NONCE = 2;
	private static final int ETH_VALUE = 1;
	private static final int GWEI = 60;
	private static final int GAS_LIMIT = 21000;
	
	private static final String SIGNED_TX = "0xf86c02850df847580082520894f5f3300d2021a81db40330d7761281b736abd56d880de0b6b3a7640000801ca090a040357148e41d5ca3043ed2bcc75b749253449d7daf0c3f1cb8b79626a405a01d5fe6c1dc473a2ef068f643e4b11f77587a7e1410899f9def890c4f219c703a";
	
	@Test
	public void testSignSimpleEthTx() throws Exception {
		EthereumSignedTransaction signedTx = (EthereumSignedTransaction)signSimpleEthTx();
		assertNotNull(signedTx);
		assertEquals(SIGNED_TX, signedTx.getSignedTx());
	}
	
	@Test
	public void testSignSimpleEthTxManually() throws Exception {
		EtherSignedTx signedTx = signSimpleEthTxManually();
		assertNotNull(signedTx);
		assertEquals(SIGNED_TX, signedTx.getSignedTx());
	}
	
	@Test
	public void testSignSimpleEthTxWayMatches() throws Exception {
		EthereumSignedTransaction signedTx1 = (EthereumSignedTransaction)signSimpleEthTx();
		EtherSignedTx signedTx2 = signSimpleEthTxManually();
		assertEquals(SIGNED_TX, signedTx1.getSignedTx());
		assertEquals(SIGNED_TX, signedTx2.getSignedTx());
		assertEquals("Signed tx from both ways need to be matched", signedTx1.getSignedTx(), signedTx2.getSignedTx());
	}
	
	private ISignedTransaction signSimpleEthTx() throws Exception {
		EthereumBlockChain ebc = new EthereumBlockChain();
		ITransactionInput input = new SimpleEthereumTransactionInput(ADDR_FROM, ADDR_TO, ETH_VALUE, NONCE, GWEI, GAS_LIMIT);
		IUnlockMethod unlock = new UnlockByPrivateKey(PRVK_FROM);
		return ebc.signSimpleTransaction(input, unlock);
	}
	
	private EtherSignedTx signSimpleEthTxManually() throws Exception {
		EtherTxInfo txi = new EtherTxInfo(ADDR_FROM, ADDR_TO, ETH_VALUE, NONCE, GWEI, GAS_LIMIT, PRVK_FROM);
		String jsonObj = KMCJsonUtil.toJSon(txi);
		JavaScript.ENGINE_MEW.execute("mew.signEtherTx('%s');", jsonObj);
		String tmp = "has" + txi.getGuid();
		do {
			Thread.sleep(50);
			log.info("Waiting...");
		} while(!Boolean.valueOf(JavaScript.ENGINE_MEW.executeAndGetValue("var %s = resultStorage.has('%s');", tmp, tmp, txi.getGuid())));

		String json = "json" + txi.getGuid();
		String tx = JavaScript.ENGINE_MEW.executeAndGetValue("var %s = resultStorage['%s'];", json, tmp, txi.getGuid());
		EtherSignedTx est = KMCJsonUtil.parse(tx, EtherSignedTx.class);
		return est;
	}
}
