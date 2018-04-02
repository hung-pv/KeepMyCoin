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

public class ValidateMnemonic extends ValidateNormal<String> {

	@Override
	public String describleWhenInvalid() {
		return "Total number of words must be divisible by 12";
	}

	@Override
	public boolean isValid(String input) {
		if (input == null)
			return false;
		String[] spl = input.trim().split("\\s");
		return spl.length % 12 == 0;
	}

}
