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
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.TimeoutManager.ITimedOutListener;
import com.keepmycoin.crypto.AES;
import com.keepmycoin.crypto.BIP39;
import com.keepmycoin.data.AbstractKMCData;
import com.keepmycoin.data.Account;
import com.keepmycoin.data.KeyStore;
import com.keepmycoin.data.Wallet;
import com.keepmycoin.data.Wallet.WalletType;
import com.keepmycoin.utils.KMCArrayUtil;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCJsonUtil;
import com.keepmycoin.utils.KMCStringUtil;

public abstract class AbstractApplicationSkeleton implements IKeepMyCoin {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(AbstractApplicationSkeleton.class);

	protected KMCDevice dvc = null;
	protected AES aes = null;

	protected void preLaunch() throws Exception {
		log.trace("preLaunch");
		
		log.debug("Check device is exists");
		
		if (Configuration.KMC_DEVICE == null) {
			log.debug("KMC device location is null, auto detect from current directory");
			String userDir = System.getProperty("user.dir");
			if (StringUtils.isBlank(userDir)) {
				showMsg("Unable to detect current user directory");
				System.exit(1);
			}
			Configuration.KMC_DEVICE = new File(userDir);
			if (Configuration.KMC_FOLDER == null) {
				Configuration.KMC_FOLDER = Paths.get(userDir, Configuration.KMC_FOLDER_DEFAULT).toFile();
			}
		}
		this.dvc = new KMCDevice(Configuration.KMC_DEVICE);
		
		if (!this.dvc.exists()) {
			showMsg("Device does not exists");
			System.exit(1);
		}
		
		if (!this.dvc.isValid()) {
			if (!this.dvc.getIdFile().exists()) {
				// create is file
				FileUtils.writeByteArrayToFile(this.dvc.getIdFile(), new byte[0]);
			} else {
				throw new RuntimeException("Un-handled case");
			}
		}
		
		if (Configuration.KMC_FOLDER == null || !Configuration.KMC_FOLDER.exists()) {
			showMsg("KMC folder does not exists");
			System.exit(1);
		}
		
		setupSessionTimeOut();
	}

	protected void setupSessionTimeOut() throws Exception {
		log.trace("setupSessionTimeOut");
		TimeoutManager.start(new ITimedOutListener() {
			@Override
			public void doNotify() {
				onSessionTimedOut();
			}
		});
	}

	protected void onSessionTimedOut() {
		log.trace("onSessionTimedOut");
		showMsg("Session timed out after %d seconds idle", Configuration.TIME_OUT_SEC);
	}

	@Override
	public void launch() throws Exception {
		log.trace("launch");
		preLaunch();
		// launch code
		launchUserInterface();
		launchMenu();
	}

	protected abstract void launchUserInterface() throws Exception;

	protected abstract void launchMenu() throws Exception;

	@Override
	public void generateNewKeystore() throws Exception {
		log.trace("generateNewKeystore");
		if (isKeystoreExists()) {
			showMsg("Illegal call, keystore already exists");
			System.exit(0);
		}
		File fDeviceId = dvc.getIdFile();
		if (!Configuration.DEBUG && fDeviceId.exists() && FileUtils.readFileToByteArray(fDeviceId).length > 0) {
			showMsg("WARNING! Your USB '%s' were used by another keystore before, restoring keystore may results losting encrypted data FOREVER\n"
					+ //
					"In order to generate new keystore you need to perform following actions:\n" + //
					" 1. Delete '%s' file located in USB\n" + //
					" 2. Create a new '%s' file in your USB (leave it empty)", //
					fDeviceId.getName(), fDeviceId.getName(), fDeviceId.getName());
			System.exit(0);
			return;
		}
		generateNewKeystore_getInitPassPharse();
	}

	protected abstract void generateNewKeystore_getInitPassPharse() throws Exception;

	protected void generateNewKeystore_fromInitPassPharse(String pwd) throws Exception {
		log.trace("generateNewKeystore_fromInitPassPharse");
		// Gen key
		byte[] key = KMCArrayUtil.randomBytes(32);
		byte[] keyWithAES = AES.encrypt(key, pwd);

		// Mnemonic
		String mnemonic = BIP39.entropyToMnemonic(keyWithAES);
		String entropyToVerify = BIP39.mnemonicToEntropy(mnemonic);
		byte[] entropyToVerifyBuffer = KMCStringUtil.parseHexBinary(entropyToVerify);

		// Verify BIP39
		byte[] keyToVerify = AES.decrypt(entropyToVerifyBuffer, pwd);
		for (int i = 0; i < key.length; i++) {
			if (key[i] != keyToVerify[i]) {
				throw new RuntimeException("Mismatch BIP39, contact author");
			}
		}

		showMsg("Below are %d seeds word\n" + //
				"LOSING THESE WORDS, YOU CAN NOT RESTORE YOUR PRIVATE KEY\n" + //
				"you HAVE TO write it down and keep it safe.\n" + //
				"'%s'\n" + //
				"(copied to clipboard)\n" + //
				"LOSING THESE WORDS, YOU CAN NOT RESTORE YOUR PRIVATE KEY", mnemonic.split("\\s").length, mnemonic);
		KMCClipboardUtil.setText(mnemonic, "Mnemonic");

		generateNewKeystore_confirmSavedMnemonic(mnemonic, key, pwd);
	}

	protected abstract void generateNewKeystore_confirmSavedMnemonic(String mnemonic, byte[] key, String pwd)
			throws Exception;

	protected abstract void generateNewKeystore_confirmMnemonic(String mnemonic, byte[] key, String pwd)
			throws Exception;

	protected void generateNewKeystore_save(String mnemonic, byte[] key, String pwd) throws Exception {
		log.trace("generateNewKeystore_save");
		// Write file
		KeystoreManager.save(AES.encrypt(key, pwd), KMCArrayUtil.checksumValue(key));

		showMsg("Keystore created successfully");

		// Write MEMORIZE
		saveChecksum(mnemonic, key, pwd);
	}

	@Override
	public void restoreKeystore() throws Exception {
		log.trace("restoreKeystore");
		if (isKeystoreExists()) {
			showMsg("Illegal call, keystore already exists");
			System.exit(0);
		}
		restoreKeystore_selectImportWay();
	}
	
	protected abstract void restoreKeystore_selectImportWay() throws Exception;
	
	protected void restoreKeystore_fromLocalPath(File input) throws Exception {
		log.trace("restoreKeystore_fromLocalPath");
		if (input.isFile()) {
			loadKeystore_getPasspharse(input);
			return;
		} else if (input.isDirectory()) {
			File[] files = input.listFiles();
			for (File file : files) {
				if (!file.isFile()) continue;
				try {
					KeyStore ks = KMCJsonUtil.parseIgnoreError(FileUtils.readFileToString(file, StandardCharsets.UTF_8), KeyStore.class);
					if (ks != null && ks.isValid()) {
						loadKeystore_getPasspharse(file);
						return;
					}
				} catch (Exception e) {
					log.warn("Error file parsing " + file.getAbsolutePath(), e);
					continue;
				}
			}
		}
	}

	protected abstract void restoreKeystore_getSeedWordsAndPassPharse() throws Exception;

	protected void restoreKeystore_processUsingInput(String mnemonic, String passPharse) throws Exception {
		log.trace("restoreKeystore_processUsingInput");

		byte[] keyWithAES, key, usbIdContentBuffer, usbIdContent;
		int cacheSeedWordsLength = 0;

		try {
			usbIdContentBuffer = FileUtils.readFileToByteArray(dvc.getIdFile());
			if (usbIdContentBuffer.length > 1) {
				cacheSeedWordsLength = usbIdContentBuffer[0];
				usbIdContent = new byte[usbIdContentBuffer.length - 1];
				System.arraycopy(usbIdContentBuffer, 1, usbIdContent, 0, usbIdContent.length);
			} else {
				usbIdContent = new byte[0];
			}

			keyWithAES = BIP39.mnemonicToEntropyBuffer(mnemonic);
			key = AES.decrypt(keyWithAES, passPharse);

			if (cacheSeedWordsLength > 0) {
				String mnemonicFromUsbID = new String(AES.decrypt(usbIdContent, passPharse), StandardCharsets.UTF_8);
				if (!mnemonic.trim().equals(mnemonicFromUsbID.trim())) {
					showMsg("Incorrect! You have to check your seed words and your passphrase then try again!\n" + //
							"Hint: seed contains %d words", cacheSeedWordsLength);
					restoreKeystore_getSeedWordsAndPassPharse();
					return;
				}
			}
		} catch (Exception e) {
			showMsg("Look like something wrong, you have to check your seed words and your passphrase then try again!\n"
					+ //
					"Hint: seed contains %d words", cacheSeedWordsLength);
			restoreKeystore_getSeedWordsAndPassPharse();
			return;
		}

		KeystoreManager.save(keyWithAES, KMCArrayUtil.checksumValue(key));
		showMsg("Keystore restored successfully");

		// Write MEMORIZE
		saveChecksum(mnemonic, key, passPharse);
	}

	protected void saveChecksum(String mnemonic, byte[] key, String pwd) {
		log.trace("saveChecksum");
		try {
			byte[] content = AES.encrypt(KMCStringUtil.getBytes(mnemonic, 256), pwd);
			byte[] buffer = new byte[content.length + 1];
			System.arraycopy(content, 0, buffer, 1, content.length);
			buffer[0] = (byte) mnemonic.split("\\s").length;
			FileUtils.writeByteArrayToFile(dvc.getIdFile(), buffer);
		} catch (Exception e) {
			log.error("Error while saving checksum", e);
		}
	}
	
	protected File getAnyKeyStoreFile() {
		log.trace("getAnyKeyStoreFile");
		if (!dvc.isValid())
			return null;
		File keystore;
		
		keystore = KeystoreManager.getKeystoreFile();
		if (KMCFileUtil.isFileExists(keystore))
			return keystore;
		
		keystore = dvc.getFile(Configuration.KEYSTORE_NAME);
		if (KMCFileUtil.isFileExists(keystore))
			return keystore;
		
		return null;
	}

	protected boolean isKeystoreExists() {
		log.trace("isKeystoreExists");
		return getAnyKeyStoreFile() != null;
	}

	@Override
	public void loadKeystore() throws Exception {
		log.trace("loadKeystore");
		if (aes != null) {
			return;
		}
		File fKeystore = getAnyKeyStoreFile();
		if (fKeystore == null) {
			showMsg("Keystore file named '%s' does not exists", KeystoreManager.getKeystoreFile().getName());
			System.exit(1);
		}
		loadKeystore_getPasspharse(fKeystore);
	}

	protected abstract void loadKeystore_getPasspharse(File fKeystore) throws Exception;

	protected void loadKeystore_processUsingPasspharse(File fKeystore, String passpharse) throws Exception {
		log.trace("loadKeystore_processUsingPasspharse");
		KeyStore ks = KeystoreManager.readKeyStore(fKeystore);
		byte[] keyWithAES = ks.getEncryptedKeyBuffer();
		String checksumFromFile = StringUtils.trimToNull(ks.getChecksum());
		byte[] key = AES.decrypt(keyWithAES, passpharse);
		if (checksumFromFile != null) {
			String checksumVerify = KMCArrayUtil.checksumValue(key);
			if (!checksumVerify.equals(checksumFromFile)) {
				showMsg("Incorrect passphrase");
				loadKeystore_getPasspharse(fKeystore);
				return;
			}
		}
		aes = new AES(key);
		KeystoreManager.cacheFilePath(ks, fKeystore);
	}

	protected void saveAWallet_saveInfo(String address, String privateKey, WalletType walletType, String mnemonic,
			String publicNote, String privateNote) throws Exception {
		log.trace("saveAWallet_saveInfo");
		byte[] privateKeyWithAESEncrypted = encryptUsingExistsKeystore(privateKey);
		byte[] mnemonicWithAESEncrypted = encryptUsingExistsKeystore(mnemonic);
		byte[] notePrivateWithAESEncrypted = encryptUsingExistsKeystore(privateNote);

		// Clear clip-board
		KMCClipboardUtil.clear();

		Wallet wallet = new Wallet(address, privateKeyWithAESEncrypted, walletType.name(), mnemonicWithAESEncrypted,
				publicNote, notePrivateWithAESEncrypted);
		wallet.addAdditionalInformation();
		File file = dvc.getFile(String.format("%s.%s.%s", address, walletType.name(), Configuration.EXT));
		KMCFileUtil.writeFile(file, wallet);

		showMsg("Saved %s", address);

		askContinueOrExit(null);
	}

	@Override
	public void readAWallet() throws Exception {
		log.trace("readAWallet");
		List<Wallet> wallets = AbstractKMCData.filter(this.dvc.getAllKMCFiles(), Wallet.class);
		if (wallets.isEmpty()) {
			showMsg("There is no wallet file! You will need to perform saving a wallet first");
			if (this instanceof KeepMyCoinConsole) {
				((KeepMyCoinConsole) this).pressEnterToContinue();
			}
			return;
		}
		readAWallet_choose(wallets);
	}

	protected abstract void readAWallet_choose(List<Wallet> wallets) throws Exception;

	protected abstract void readAWallet_read(Wallet wallet) throws Exception;

	protected void saveAnAccount_saveInfo(String name, String website, String publicNote, String password,
			String priv2FA, String privateNote) throws Exception {
		log.trace("saveAnAccount_saveInfo");
		byte[] paswordWithAESEncrypted = encryptUsingExistsKeystore(password);
		byte[] priv2faWithAESEncrypted = encryptUsingExistsKeystore(priv2FA);
		byte[] privateNoteWithAESEncrypted = encryptUsingExistsKeystore(privateNote);

		// Clear clip-board
		KMCClipboardUtil.clear();

		Account account = new Account(name, website, publicNote, paswordWithAESEncrypted, priv2faWithAESEncrypted,
				privateNoteWithAESEncrypted);
		account.addAdditionalInformation();

		StringBuilder fileName = new StringBuilder();
		fileName.append(KMCStringUtil.toPathChars(name));
		if (website != null) {
			fileName.append('.');
			try {
				fileName.append(KMCStringUtil.toPathChars(KMCStringUtil.getDomainName(website)));
			} catch (Exception e) {
				fileName.append("website");
			}
		}

		File file = dvc.getFile(String.format("%s.%s", fileName.toString(), Configuration.EXT));
		KMCFileUtil.writeFile(file, account);

		showMsg("Saved account %s%s", name, website != null ? (" of " + website) : "");

		askContinueOrExit(null);
	}

	@Override
	public void readAnAccount() throws Exception {
		log.trace("readAnAccount");
		List<Account> accounts = AbstractKMCData.filter(this.dvc.getAllKMCFiles(), Account.class);
		if (accounts.isEmpty()) {
			showMsg("There is no account file! You will need to perform saving an account first");
			return;
		}
		readAnAccount_choose(accounts);
	}

	protected abstract void readAnAccount_choose(List<Account> accounts) throws Exception;

	protected abstract void readAnAccount_read(Account account) throws Exception;

	protected byte[] encryptUsingExistsKeystore(String data) throws Exception {
		log.trace("encryptUsingExistsKeystore");
		return aes.encryptNullable(KMCStringUtil.getBytesNullable(data));
	}

	protected String decryptUsingExistsKeystore(byte[] buffer) throws Exception {
		log.trace("decryptUsingExistsKeystore");
		byte[] decrypted = aes.decryptNullable(buffer);
		return decrypted == null ? null : new String(decrypted, StandardCharsets.UTF_8);
	}

	protected abstract void showMsg(String format, Object... args);

	protected void askContinueOrExit(String question) throws Exception {
		log.trace("askContinueOrExit");
	}

	protected void exit() throws Exception {
		KMCClipboardUtil.clear();
		System.exit(0);
	}
}
