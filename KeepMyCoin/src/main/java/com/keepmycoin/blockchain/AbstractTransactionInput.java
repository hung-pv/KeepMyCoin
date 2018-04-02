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

public abstract class AbstractTransactionInput implements ITransactionInput {
	private String from;
	private String to;
	private double amtTransfer;
	private int nonce;

	public AbstractTransactionInput() {
	}

	public AbstractTransactionInput(String from, String to, double amtTransfer, int nonce) {
		this.from = from;
		this.to = to;
		this.amtTransfer = amtTransfer;
		this.nonce = nonce;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getAmtTransfer() {
		return amtTransfer;
	}

	public void setAmtTransfer(double amtTransfer) {
		this.amtTransfer = amtTransfer;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

}
