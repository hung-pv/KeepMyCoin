package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.keepmycoin.crypto.AES;
import com.keepmycoin.crypto.BIP39;
import com.keepmycoin.data.AbstractKMCData;
import com.keepmycoin.data.Wallet;
import com.keepmycoin.exception.CryptoException;
import com.keepmycoin.utils.KMCArrayUtil;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCStringUtil;

public abstract class AbstractApplicationSkeleton implements IKeepMyCoin {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(AbstractApplicationSkeleton.class);

	protected KMCDevice dvc = new KMCDevice(Configuration.KMC_FOLDER);
	protected AES aes256Cipher = null;

	protected void preLaunch() throws Exception {
		log.trace("preLaunch");

		// check KMC device
		if (!dvc.isValid()) {
			findKMCDevice();
			if (!dvc.isValid()) {
				log.info("Unable to find a valid KMC Device");
				setupKMCDevice();
			}
		}
		
		setupSessionTimeOut();
	}

	protected abstract void findKMCDevice() throws Exception;

	protected abstract void setupKMCDevice() throws Exception;

	protected void setupSessionTimeOut() throws Exception {
		//TODO implement
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

	protected abstract void generateNewKeystore_confirmSavedMnemonic(String mnemonic, byte[] key, String pwd) throws Exception;

	protected abstract void generateNewKeystore_confirmMnemonic(String mnemonic, byte[] key,
			String pwd) throws Exception;

	protected void generateNewKeystore_save(String mnemonic, byte[] key, String pwd)
			throws Exception {
		log.trace("generateNewKeystore_save");
		// Write file
		KeystoreManager.save(AES.encrypt(key, pwd));

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
		restoreKeystore_getSeedWordsAndPassPharse();
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
				String mnemonicFromUsbID = new String(AES.decrypt(usbIdContent, passPharse),
						StandardCharsets.UTF_8);
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

		KeystoreManager.save(keyWithAES);
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

	protected boolean isKeystoreExists() {
		log.trace("isKeystoreExists");
		if (!dvc.isValid())
			return false;
		if (KeystoreManager.isKeystoreFileExists())
			return true;
		return dvc.getFile(Configuration.KEYSTORE_NAME).exists();
	}

	@Override
	public void loadKeystore() throws Exception {
		log.trace("loadKeystore");
		if (aes256Cipher != null) {
			return;
		}
		File fKeystore = KeystoreManager.getKeystoreFile();
		if (!fKeystore.exists()) {
			fKeystore = dvc.getFile(Configuration.KEYSTORE_NAME);
			if (!fKeystore.exists()) {
				showMsg("Keystore file named '%s' does not exists", KeystoreManager.getKeystoreFile().getName());
				System.exit(1);
			}
		}
		loadKeystore_getPasspharse();
	}

	protected abstract void loadKeystore_getPasspharse() throws Exception;

	protected void loadKeystore_processUsingPasspharse(String passpharse) throws Exception {
		log.trace("loadKeystore_processUsingPasspharse");
		byte[] keyWithAES = KeystoreManager.getEncryptedKey();
		byte[] key = null;
		try {
			key = AES.decrypt(keyWithAES, passpharse);
		} catch (CryptoException e) {
			showMsg("Incorrect passphrase");
			System.exit(1);
		}
		aes256Cipher = new AES(key);
	}

	@Override
	public void saveAWallet() throws Exception {
		log.trace("saveAWallet");
		saveAWallet_getInfo();
	}

	protected abstract void saveAWallet_getInfo() throws Exception;

	protected void saveAWallet_saveInfo(String address, String privateKey, WalletType walletType, String mnemonic,
			String publicNote, String privateNote) throws Exception {
		log.trace("saveAWallet_saveInfo");
		byte[] privateKeyWithAES256Encrypted = encryptUsingExistsKeystore(privateKey);
		byte[] mnemonicWithAES256Encrypted = encryptUsingExistsKeystore(mnemonic);
		byte[] notePrivateWithAES256Encrypted = encryptUsingExistsKeystore(privateNote);

		// Clear clip-board
		KMCClipboardUtil.clear();

		Wallet wallet = new Wallet(address, privateKeyWithAES256Encrypted, walletType.name(),
				mnemonicWithAES256Encrypted, publicNote, notePrivateWithAES256Encrypted);
		wallet.addAdditionalInformation();
		File file = dvc.getFile(String.format("%s.%s.%s", address, walletType.name(), Configuration.EXT_DEFAULT));
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
			return;
		}
		readAWallet_choose(wallets);
	}
	
	protected byte[] encryptUsingExistsKeystore(String data) throws Exception {
		log.trace("encryptUsingExistsKeystore");
		return aes256Cipher.encryptNullable(KMCStringUtil.getBytesNullable(data));
	}
	
	protected String decryptUsingExistsKeystore(byte[] buffer) throws Exception {
		log.trace("decryptUsingExistsKeystore");
		byte[] decrypted = aes256Cipher.decryptNullable(buffer);
		return decrypted == null ? null : new String(decrypted, StandardCharsets.UTF_8);
	}
	
	protected abstract void readAWallet_choose(List<Wallet> wallets) throws Exception;
	
	protected abstract void readAWallet_read(Wallet wallet) throws Exception;

	protected abstract void showMsg(String format, Object... args);

	protected void askContinueOrExit(String question) throws Exception {
		log.trace("askContinueOrExit");
	}
}
