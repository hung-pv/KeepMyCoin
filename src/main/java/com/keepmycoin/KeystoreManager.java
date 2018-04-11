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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.data.KeyStore;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCJsonUtil;

public class KeystoreManager {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KeystoreManager.class);

	private static final File FILE_AES_KEYSTORE = new File(Configuration.KEYSTORE_NAME);
	private static final String CACHE_KEYSTORE_FILE_PATH = "KeyStore.cache";

	public static File getKeystoreFile() {
		return FILE_AES_KEYSTORE;
	}

	public static boolean isKeystoreFileExists() {
		return FILE_AES_KEYSTORE.exists();
	}

	public static void save(byte[] keyWithAES, String checkSum) throws Exception {
		KeyStore ks = new KeyStore();
		ks.addAdditionalInformation();
		ks.setGuid(UUID.randomUUID().toString());
		ks.setEncryptedKeyBuffer(keyWithAES);
		ks.setChecksum(checkSum);
		KMCFileUtil.writeFile(FILE_AES_KEYSTORE, ks);
	}

	public static KeyStore readKeyStore(File file) throws Exception {
		file = file != null ? file : FILE_AES_KEYSTORE;
		String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		if (StringUtils.isBlank(content)) {
			return null;
		}
		return KMCJsonUtil.parse(content, KeyStore.class);
	}
	
	public static void cacheFilePath(KeyStore ks, File file) {
		try {
			File fCache = new File(CACHE_KEYSTORE_FILE_PATH);
			final Map<String, Set<String>> map = loadCachedKeyStoreFilePaths();
			
			Set<String> paths = map.get(StringUtils.trimToNull(ks.getGuid()));
			if (paths == null) {
				paths = new HashSet<>();
				map.put(StringUtils.trimToNull(ks.getGuid()), paths);
			}
			paths.add(file.getAbsolutePath());
			
			StringBuilder sb = new StringBuilder();
			for (Entry<String, Set<String>> e1 : map.entrySet()) {
				for (String e2 : e1.getValue()) {
					sb.append(e1.getKey());
					sb.append('=');
					sb.append(e2);
					sb.append('\n');
				}
			}
			FileUtils.write(fCache, sb, StandardCharsets.UTF_8);
		}catch (Exception e) {
			log.error("Error while caching keystore file path", e);
		}
	}
	
	public static List<File> findExistsKeyStoreFromCachedFilePaths() {
		List<File> result = new ArrayList<>();
		try {
			final Map<String, Set<String>> map = loadCachedKeyStoreFilePaths();
			for (Entry<String, Set<String>> e1 : map.entrySet()) {
				String guid = e1.getKey();
				for (String filePath : e1.getValue()) {
					try {
						File f = new File(filePath);
						if (!f.exists()) continue;
						KeyStore ks = KMCJsonUtil.parseIgnoreError(FileUtils.readFileToString(f, StandardCharsets.UTF_8), KeyStore.class);
						if (ks != null && ks.isValid() && ks.getGuid().equalsIgnoreCase(guid)) {
							result.add(f);
						}
					} catch (Exception e) {
						log.error("Can try load keystore from " + filePath, e);
					}
				}
			}
		} catch (IOException e) {
			log.error("Can not find keystore", e);
		}
		return result;
	}
	
	private static Map<String, Set<String>> loadCachedKeyStoreFilePaths() throws IOException {
		File fCache = new File(CACHE_KEYSTORE_FILE_PATH);
		final Map<String, Set<String>> map = new HashMap<>();
		if (fCache.exists()) {
			// load old data
			List<String> lines = FileUtils.readLines(fCache, StandardCharsets.UTF_8);
			for (String line : lines) {
				String[] spl = line.split("=", 2);
				String guid = StringUtils.trimToNull(spl[0]);
				String path = StringUtils.trimToNull(spl[1]);
				if (guid == null || path == null) {
					continue;
				}
				Set<String> paths = map.get(guid);
				if (paths == null) {
					paths = new HashSet<>();
					map.put(guid, paths);
				}
				paths.add(path);
			}
		}
		return map;
	}
}
