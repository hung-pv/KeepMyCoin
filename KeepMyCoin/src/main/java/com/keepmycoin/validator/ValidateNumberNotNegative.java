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

public class ValidateNumberNotNegative<T extends Number> extends ValidateNormal<T> {

	@Override
	public String describleWhenInvalid() {
		return "Must not be negative";
	}

	@Override
	public boolean isValid(Number num) {
		if (num == null) return false;
		if (num instanceof Double) {
			Double d = (Double)num;
			return !d.isNaN() && !d.isInfinite() && d.doubleValue() >= 0.D;
		} else if (num instanceof Long) {
			return num.longValue() >= 0;
		} else if (num instanceof Integer) {
			return num.intValue() >= 0;
		} else if (num instanceof Byte) {
			return num.byteValue() >= 0;
		} else if (num instanceof Float) {
			return num.floatValue() >= 0;
		} else {
			return false;
		}
	}

}
