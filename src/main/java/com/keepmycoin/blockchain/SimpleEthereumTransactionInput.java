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

public class SimpleEthereumTransactionInput extends AbstractEthereumTransactionInput {

	public SimpleEthereumTransactionInput(String from, String to, double amtEthereumTransfer, int nonce,
			BigInteger gasPriceInWei, int gasLimit) {
		super(from, to, amtEthereumTransfer, nonce, gasPriceInWei, gasLimit, "");
	}

	public SimpleEthereumTransactionInput(String from, String to, double amtEthereumTransfer, int nonce,
			int gwei, int gasLimit) {
		this(from, to, amtEthereumTransfer, nonce, new BigInteger(gwei + "000000000", 10), gasLimit);
	}
}
