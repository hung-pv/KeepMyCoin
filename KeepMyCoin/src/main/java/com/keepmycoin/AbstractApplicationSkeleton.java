package com.keepmycoin;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.bitsofproof.supernode.wallet.BIP39;
import com.keepmycoin.crypto.AES128;
import com.keepmycoin.crypto.AES256;
import com.keepmycoin.data.KeyStore;
import com.keepmycoin.utils.KMCArrayUtil;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCStringUtil;

public abstract class AbstractApplicationSkeleton implements IKeepMyCoin {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(AbstractApplicationSkeleton.class);

	protected KMCDevice dvc = new KMCDevice(Configuration.KMC_FOLDER);

	protected void preLaunch() throws Exception {
		log.trace("preLaunch");

		// check KMC device
		if (!this.dvc.isValid()) {
			findKMCDevice();
			if (!this.dvc.isValid()) {
				log.info("Unable to find a valid KMC Device");
				setupKMCDevice();
			}
		}
	}

	protected abstract void findKMCDevice() throws Exception;

	protected abstract void setupKMCDevice() throws Exception;

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
		File fDeviceId = this.dvc.getIdFile();
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
		generateNewKeystore_getInitPwd();
	}

	protected abstract void generateNewKeystore_getInitPwd() throws Exception;

	protected void generateNewKeystore_fromInitPwd(String pwd) throws Exception {
		log.trace("generateNewKeystore_fromInitPwd");
		// Gen key
		byte[] key = KMCArrayUtil.randomBytes(32);
		byte[] keyWithAES128 = AES128.encrypt(key, pwd);

		// Mnemonic
		byte[] keyWithBIP39Encode = BIP39.encode(keyWithAES128, pwd);
		String mnemonic = BIP39.getMnemonic(keyWithBIP39Encode).trim();

		// Verify BIP39
		byte[] keyToVerify = AES128.decrypt(BIP39.decode(mnemonic, pwd), pwd);
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

		generateNewKeystore_confirmSavedMnemonic(mnemonic, keyWithBIP39Encode, key, pwd);
	}

	protected abstract void generateNewKeystore_confirmSavedMnemonic(String mnemonic, byte[] keyWithBIP39Encode, byte[] key, String pwd) throws Exception;

	protected abstract void generateNewKeystore_confirmMnemonic(String mnemonic, byte[] keyWithBIP39Encode, byte[] key, String pwd) throws Exception;

	protected void generateNewKeystore_save(String mnemonic, byte[] keyWithBIP39Encode, byte[] key, String pwd) throws Exception {
		log.trace("generateNewKeystore_save");
		// Write file
		KeyStore ks = new KeyStore();
		ks.addAdditionalInformation();
		ks.setEncryptedKeyBuffer(keyWithBIP39Encode);
		KeystoreManager.save(ks);

		showMsg("Keystore created successfully");

		// Write MEMORIZE
		saveChecksum(mnemonic, key, pwd);
	}

	protected void saveChecksum(String mnemonic, byte[] key, String pwd) {
		log.trace("saveChecksum");
		try {
			byte[] content = AES256.encrypt(KMCStringUtil.getBytes(mnemonic, 256), key, pwd);
			byte[] buffer = new byte[content.length + 1];
			System.arraycopy(content, 0, buffer, 1, content.length);
			buffer[0] = (byte) mnemonic.split("\\s").length;
			FileUtils.writeByteArrayToFile(this.dvc.getIdFile(), buffer);
		} catch (Exception e) {
			log.error("Error while saving checksum", e);
		}
	}

	protected boolean isKeystoreExists() {
		log.trace("isKeystoreExists");
		if (!this.dvc.isValid())
			return false;
		if (KeystoreManager.isKeystoreFileExists())
			return true;
		return this.dvc.getFile(Configuration.KEYSTORE_NAME).exists();
	}

	@Override
	public void loadKeystore() throws Exception {
		log.trace("loadKeystore");
	}

	@Override
	public void saveAWallet() throws Exception {
		log.trace("saveAWallet");
	}

	protected abstract void showMsg(String format, Object... args);
}
