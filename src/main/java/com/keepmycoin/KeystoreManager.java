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
package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.data.KeyStore;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCJsonUtil;

public class KeystoreManager {

	private static final File FILE_AES_KEYSTORE = new File(Configuration.KEYSTORE_NAME);

	public static File getKeystoreFile() {
		return FILE_AES_KEYSTORE;
	}

	public static boolean isKeystoreFileExists() {
		return FILE_AES_KEYSTORE.exists();
	}

	public static void save(byte[] keyWithAES, String checkSum) throws Exception {
		KeyStore ks = new KeyStore();
		ks.addAdditionalInformation();
		ks.setEncryptedKeyBuffer(keyWithAES);
		ks.setChecksum(checkSum);
		KMCFileUtil.writeFile(FILE_AES_KEYSTORE, ks);
	}

	public static KeyStore readKeyStore() throws Exception {
		String content = FileUtils.readFileToString(FILE_AES_KEYSTORE, StandardCharsets.UTF_8);
		if (StringUtils.isBlank(content)) {
			return null;
		}
		return KMCJsonUtil.parse(content, KeyStore.class);
	}
}
