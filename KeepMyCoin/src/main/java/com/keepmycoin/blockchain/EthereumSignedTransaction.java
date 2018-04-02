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

import java.math.BigInteger;

import com.keepmycoin.utils.KMCNumberUtil;

public class EthereumSignedTransaction extends AbstractSignedTransaction {
	private int nonce;
	private BigInteger wei;
	private int gasLimit;
	private String data;
	
	public EthereumSignedTransaction(String from, String to, String transferAmtHex, String rawTx, String signedTx,
			String nonceHex, String gasPriceWeiHex, String gasLimitHex, String data) {
		super(from, to, KMCNumberUtil.fromHexToBigInteger(transferAmtHex), rawTx, signedTx);
		this.nonce = KMCNumberUtil.fromHexToBigInteger(nonceHex).intValue();
		this.wei = KMCNumberUtil.fromHexToBigInteger(gasPriceWeiHex);
		this.gasLimit = KMCNumberUtil.fromHexToBigInteger(gasLimitHex).intValue();
		this.data = data;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	
	public BigInteger getWei() {
		return wei;
	}
	
	public int getGwei() {
		return this.wei.divide(new BigInteger("1000000000", 10)).intValue();
	}

	public void setWei(BigInteger wei) {
		this.wei = wei;
	}

	public int getGasLimit() {
		return gasLimit;
	}

	public void setGasLimit(int gasLimit) {
		this.gasLimit = gasLimit;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "EthereumSignedTransaction [nonce=" + nonce + ", wei=" + wei + ", gasLimit=" + gasLimit + ", data="
				+ data + ", getNonce()=" + getNonce() + ", getWei()=" + getWei() + ", getGwei()=" + getGwei()
				+ ", getGasLimit()=" + getGasLimit() + ", getData()=" + getData() + ", getFrom()=" + getFrom()
				+ ", getTo()=" + getTo() + ", getTransferAmt()=" + getTransferAmt() + ", getRawTx()=" + getRawTx()
				+ ", getSignedTx()=" + getSignedTx() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
}
