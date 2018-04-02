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
package com.keepmycoin.validator.crypto.eth;

import com.keepmycoin.validator.ERC20BlockChainValidator;
import com.keepmycoin.validator.ValidateRegex;

public class ValidateEthAddress extends ValidateRegex {
	@Override
	public String describleWhenInvalid() {
		return "Must be a valid ethereum address";
	}

	@Override
	public String getPattern() {
		return null;
	}

	@Override
	public boolean isValid(String input) {
		return new ERC20BlockChainValidator().isValidAddress(input);
	}
}
