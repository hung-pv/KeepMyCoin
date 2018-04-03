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
package com.keepmycoin.validator;

import java.util.regex.Pattern;

public class ERC20BlockChainValidator implements IBlockChainValidator {
	@Override
	public boolean isValidAddress(String address) {
		return address != null && Pattern.matches("(0[xX])?[aA-zZ0-9]{40}", address.trim());
	}

	@Override
	public boolean isValidPrivateKey(String privKey) {
		return privKey != null && Pattern.matches("[aA-zZ0-9]{64}", privKey.trim());
	}
}
