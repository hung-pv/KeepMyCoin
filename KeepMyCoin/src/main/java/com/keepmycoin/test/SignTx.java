package com.keepmycoin.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.keepmycoin.JavaScript;
import com.keepmycoin.blockchain.EthereumBlockChain;
import com.keepmycoin.blockchain.EthereumSignedTransaction;
import com.keepmycoin.blockchain.ISignedTransaction;
import com.keepmycoin.blockchain.ITransactionInput;
import com.keepmycoin.blockchain.IUnlockMethod;
import com.keepmycoin.blockchain.SimpleEthereumTransactionInput;
import com.keepmycoin.blockchain.UnlockByPrivateKey;
import com.keepmycoin.jsdata.EtherSignedTx;
import com.keepmycoin.jsdata.EtherTxInfo;
import com.keepmycoin.utils.KMCJsonUtil;

public class SignTx {
	private static final String ADDR_FROM = "0xd8C61D0719f54fDf2BF794135b6b0384AC89deFE";
	public static final String PRVK_FROM = "8612b0addc93c2fb31f6bcc79e493d846c051abc6e803cfab0c2d9f4187b26c7";
	private static final String ADDR_TO = "0xF5f3300d2021A81DB40330d7761281B736abd56d";
	private static final int NONCE = 2;
	private static final int ETH_VALUE = 1;
	private static final int GWEI = 60;
	private static final int GAS_LIMIT = 21000;
	
	@Test
	public void testSignSimpleEthTx() throws Exception {
		EthereumSignedTransaction signedTx = (EthereumSignedTransaction)signSimpleEthTx();
		assertNotNull(signedTx);
	}
	
	@Test
	public void testSignSimpleEthTxManually() throws Exception {
		EtherSignedTx signedTx = signSimpleEthTxManually();
		assertNotNull(signedTx);
	}
	
	@Test
	public void testSignSimpleEthTxWayMatches() throws Exception {
		EthereumSignedTransaction signedTx1 = (EthereumSignedTransaction)signSimpleEthTx();
		EtherSignedTx signedTx2 = signSimpleEthTxManually();
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
		JavaScript.ENGINE_MEW.execute("mew.signEtherTx('" + KMCJsonUtil.toJSon(txi) + "');");
		String tmp = "has" + txi.getGuid();
		do {
			Thread.sleep(50);
		} while(!Boolean.valueOf(JavaScript.ENGINE_MEW.executeAndGetValue("var " + tmp + " = resultStorage.has('" + txi.getGuid() + "');", tmp)));

		String json = "json" + txi.getGuid();
		String tx = JavaScript.ENGINE_MEW.executeAndGetValue("var " + json + " = resultStorage['" + txi.getGuid() + "'];", json);
		EtherSignedTx est = KMCJsonUtil.parse(tx, EtherSignedTx.class);
		return est;
	}
}
