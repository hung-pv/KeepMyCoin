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

public abstract class AbstractEthereumTransactionInput extends AbstractTransactionInput {
	private BigInteger gasPriceInWei;
	private int gasLimit;
	private String data;

	public AbstractEthereumTransactionInput(String from, String to, double amtTransfer, //
			int nonce, BigInteger gasPriceInWei, int gasLimit, String data) {
		super(from, to, amtTransfer, nonce);
		this.gasPriceInWei = gasPriceInWei;
		this.gasLimit = gasLimit;
		this.data = data;
	}

	public BigInteger getGasPriceInWei() {
		return gasPriceInWei;
	}

	public int getGWei() {
		return this.gasPriceInWei.divide(new BigInteger("1000000000", 10)).intValue();
	}

	public void setGasPriceInWei(BigInteger gasPriceInWei) {
		this.gasPriceInWei = gasPriceInWei;
	}

	public void setGasPriceInWei(int gasPriceInWei) {
		this.gasPriceInWei = new BigInteger(String.valueOf(gasPriceInWei), 10);
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
}
