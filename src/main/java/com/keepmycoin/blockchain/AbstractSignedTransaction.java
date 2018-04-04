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

public abstract class AbstractSignedTransaction implements ISignedTransaction {
	private String from;
	private String to;
	private BigInteger transferAmt;
	private String rawTx;
	private String signedTx;
	
	public AbstractSignedTransaction(String from, String to, BigInteger transferAmt, String rawTx, String signedTx) {
		this.from = from;
		this.to = to;
		this.transferAmt = transferAmt;
		this.rawTx = rawTx;
		this.signedTx = signedTx;
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

	public BigInteger getTransferAmt() {
		return transferAmt;
	}

	public String getTransferAmt(int shiftTheDots) {
		return KMCNumberUtil.shiftTheDot(this.transferAmt.toString(10), shiftTheDots);
	}

	public void setTransferAmt(BigInteger transferAmt) {
		this.transferAmt = transferAmt;
	}

	public String getRawTx() {
		return rawTx;
	}

	public void setRawTx(String rawTx) {
		this.rawTx = rawTx;
	}

	public String getSignedTx() {
		return signedTx;
	}

	public void setSignedTx(String signedTx) {
		this.signedTx = signedTx;
	}
}
