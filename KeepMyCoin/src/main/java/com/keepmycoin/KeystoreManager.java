package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.data.KeyStore;
import com.keepmycoin.utils.JsonUtil;

public class KeystoreManager {
	
	private static final File FILE_AES_KEYSTORE = new File(Configuration.KEYSTORE_NAME);

	public static File getKeystoreFile() {
		return FILE_AES_KEYSTORE;
	}

	public static boolean isKeystoreFileExists() {
		return FILE_AES_KEYSTORE.exists();
	}
	
	public static void save(KeyStore ks) throws Exception {
		FileUtils.write(FILE_AES_KEYSTORE, JsonUtil.toJSon(ks), StandardCharsets.UTF_8);
	}

	public static byte[] getEncryptedKey() throws Exception {
		String content = FileUtils.readFileToString(FILE_AES_KEYSTORE, StandardCharsets.UTF_8);
		if (StringUtils.isBlank(content)) {
			return null;
		}
		KeyStore ks = JsonUtil.parse(content, KeyStore.class);
		return ks.getEncryptedKeyBuffer();
	}
}
