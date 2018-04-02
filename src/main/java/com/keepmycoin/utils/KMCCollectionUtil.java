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
package com.keepmycoin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KMCCollectionUtil {
	public static byte[] merge(Collection<byte[]> collection, int size) {
		byte[] arr = new byte[size];
		int pointer = 0;
		for (byte[] ele : collection) {
			for (byte b : ele) {
				arr[pointer++] = b;
			}
		}
		return arr;
	}

	public static List<byte[]> split(byte[] arr, int size) {
		List<byte[]> result = new ArrayList<byte[]>();
		int remain = arr.length;
		int pointer = 0;
		while (remain > 0) {
			int spl = Math.min(remain, size);
			int endIndex = pointer + spl;
			byte[] barr = Arrays.copyOfRange(arr, pointer, endIndex);
			result.add(barr);
			pointer = endIndex;
			remain = arr.length - pointer;
		}
		return result;
	}
}
