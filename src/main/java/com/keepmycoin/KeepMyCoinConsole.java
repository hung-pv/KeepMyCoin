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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.keepmycoin.blockchain.EthereumBlockChain;
import com.keepmycoin.blockchain.EthereumSignedTransaction;
import com.keepmycoin.blockchain.IBlockChain;
import com.keepmycoin.blockchain.ISignedTransaction;
import com.keepmycoin.blockchain.ITransactionInput;
import com.keepmycoin.blockchain.IUnlockMethod;
import com.keepmycoin.blockchain.SimpleEthereumTransactionInput;
import com.keepmycoin.blockchain.UnlockByPrivateKey;
import com.keepmycoin.console.MenuManager;
import com.keepmycoin.data.AbstractKMCData;
import com.keepmycoin.data.Account;
import com.keepmycoin.data.KeyStore;
import com.keepmycoin.data.Wallet;
import com.keepmycoin.data.Wallet.WalletType;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCInputUtil;
import com.keepmycoin.utils.KMCJsonUtil;
import com.keepmycoin.utils.KMCStringUtil;
import com.keepmycoin.validator.IValidator;
import com.keepmycoin.validator.ValidateMnemonic;
import com.keepmycoin.validator.ValidateMustBeDouble;
import com.keepmycoin.validator.ValidateMustBeInteger;
import com.keepmycoin.validator.ValidateNormal;
import com.keepmycoin.validator.ValidateNumberNotNegative;
import com.keepmycoin.validator.crypto.eth.ValidateEthAddress;
import com.keepmycoin.validator.crypto.eth.ValidatorEthPrivateKey;

public class KeepMyCoinConsole extends AbstractApplicationSkeleton {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KeepMyCoinConsole.class);

	public KeepMyCoinConsole() {
		Configuration.MODE_CONSOLE = true;
	}

	@Override
	protected void preLaunch() throws Exception {
		log.trace("preLaunch");
		log.debug("Console mode");
		super.preLaunch();
	}

	@Override
	protected void launchUserInterface() throws Exception {
		log.trace("launchUserInterface");
		log.debug("Console mode doesn't have user interface");
	}

	@Override
	protected void launchMenu() throws Exception {
		log.trace("launchMenu");
		TimeoutManager.renew();
		MenuManager mm = new MenuManager(this);
		
		boolean showKeyStoreMenu = aes == null && !isKeystoreExists();
		if (showKeyStoreMenu) {
			List<File> files = KeystoreManager.findExistsKeyStoreFromCachedFilePaths();
			if (files.size() == 1) {
				showMsg("Existing keystore detected at '%s'", files.get(0).getAbsolutePath());
				if (KMCInputUtil.confirm("Load from this file?")) {
					showKeyStoreMenu = false;
					loadKeystore_getPasspharse(files.get(0));
				}
			} else if (files.size() > 1) {
				MenuManager mm2 = new MenuManager(this);
				mm2.add("Skip", null);
				for (File file : files) {
					mm2.add(file.getAbsolutePath(), "loadKeystore_getPasspharse", file);
				}
				int selection = getMenuSelection(mm2, "Select a keystore file:");
				if (selection > 1) {
					showKeyStoreMenu = false;
					mm2.processSelectedOption(selection);
				}
			}
		}

		if (showKeyStoreMenu) {
			mm.add("Restore keystore", "restoreKeystore");
			mm.add("Generate keystore", "generateNewKeystore");
		} else {
			mm.add("Sign a transaction", "signTransaction");
			mm.add("Save a wallet", "saveAWallet");
			mm.add("Read a wallet", "readAWallet");
			mm.add("Save an account", "saveAnAccount");
			mm.add("Read an account", "readAnAccount");
		}
		mm.add("Exit", "exit");

		int selection = getMenuSelection(mm, "\n==========\n\nHello! Today is a beautiful day, what do want to do?");
		mm.processSelectedOption(selection);
	}

	@Override
	protected void generateNewKeystore_getInitPassPharse() throws Exception {
		log.trace("generateNewKeystore_getInitPassPharse");
		String pwd = KMCInputUtil.getPassword_required("Passphrase (up to 16 chars): ", Configuration.DEBUG ? 1 : 8);
		KMCInputUtil.requireConfirmation(pwd, true);
		generateNewKeystore_fromInitPassPharse(pwd);
	}

	@Override
	protected void generateNewKeystore_confirmSavedMnemonic(String mnemonic, byte[] key, String pwd) throws Exception {
		log.trace("generateNewKeystore_confirmSavedMnemonic");
		while (!KMCInputUtil.confirm("Did you saved the mnemonic to somewhere?")) {
			showMsg("Please carefully save it!");
		}
		if (!Configuration.DEBUG) {
			KMCClipboardUtil.clear();
		}
		generateNewKeystore_confirmMnemonic(mnemonic, key, pwd);
	}

	@Override
	protected void generateNewKeystore_confirmMnemonic(String mnemonic, byte[] key, String pwd) throws Exception {
		log.trace("generateNewKeystore_confirmMnemonic");
		showMsg("For sure you already saved these seed words, you have to typing these words again:");
		KMCInputUtil.requireConfirmation(mnemonic);
		showMsg("Good job! Keep these seed words safe");
		generateNewKeystore_save(mnemonic, key, pwd);
	}

	@Override
	protected void restoreKeystore_selectImportWay() throws Exception {
		log.trace("restoreKeystore_selectImportWay");
		MenuManager mm = new MenuManager(this);
		mm.add("Import from file path", "restoreKeystore_getLocalPath");
		mm.add("Restore from seed and passpharse", "restoreKeystore_getSeedWordsAndPassPharse");
		mm.processSelectedOption(getMenuSelection(mm, "Action:"));
	}

	protected void restoreKeystore_getLocalPath() throws Exception {
		log.trace("restoreKeystore_getLocalPath");
		showMsg("Path:");
		File path = KMCInputUtil.getInput("keystore file path", true, new KMCInputUtil.IConvert<File>() {
			@Override
			public File convert(String input) {
				return new File(input);
			}
		}, new ValidateNormal<File>() {
			@Override
			public String describleWhenInvalid() {
				return "Must be a valid keystore file or a directory contains keystore file, have read permission";
			}
			@Override
			public boolean isValid(File input) {
				try {
					if (input == null) {
						return false;
					} else {
						if (!input.canRead()) {
							return false;
						}
						if (!input.exists()) {
							return false;
						} else { // exists
							if (input.isFile()) {
								String content = FileUtils.readFileToString(input, StandardCharsets.UTF_8);
								if (StringUtils.isBlank(content))
									return false;
								return KMCJsonUtil.parseIgnoreError(content, KeyStore.class) != null;
							} else if (input.isDirectory()) {
								File[] files = input.listFiles();
								if (files == null) return false;
								for (File file : files) {
									if (!file.isFile()) continue;
									String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
									if (StringUtils.isBlank(content))
										continue;
									KeyStore ks = KMCJsonUtil.parseIgnoreError(content, KeyStore.class);
									if (ks != null && ks.isValid()) return true;
								}
								return false;
							} else {
								return false;
							}
						}
					}
				} catch (IOException e) {
					return false;
				}
			}
		});
		if (path == null) {
			return;
		}
		restoreKeystore_fromLocalPath(path);
	}

	@Override
	protected void restoreKeystore_getSeedWordsAndPassPharse() throws Exception {
		log.trace("restoreKeystore_getSeedWordsAndPassPharse");
		showMsg("Enter seed words:");
		String mnemonic;
		boolean first = true;
		do {
			if (first) {
				first = false;
			} else {
				showMsg("Incorrect, input again or type 'cancel':");
			}
			mnemonic = KMCInputUtil.getRawInput(null);
			if (mnemonic == null) {
				continue;
			}
			if ("cancel".equalsIgnoreCase(mnemonic)) {
				return;
			}
		} while (mnemonic.trim().split("\\s").length % 2 != 0);

		String pwd = KMCInputUtil.getPassword("Passphrase: ");
		if (pwd == null) {
			showMsg("Cancelled");
			return;
		}

		restoreKeystore_processUsingInput(mnemonic, pwd);
	}

	@Override
	protected void loadKeystore_getPasspharse(File fKeystore) throws Exception {
		log.trace("loadKeystore_getPasspharse");
		String pwd = KMCInputUtil.getPassword_required("Passphrase: ", 1);
		showMsg("Please wait...");
		loadKeystore_processUsingPasspharse(fKeystore, pwd);
	}

	@Override
	public void saveAWallet() throws Exception {
		log.trace("saveAWallet");
		MenuManager mm = new MenuManager(this);
		for (WalletType wt : WalletType.values()) {
			mm.add(wt.getDisplayText(), null);
		}
		int selection = getMenuSelection(mm, "Select wallet type:");
		WalletType wt = WalletType.values()[selection - 1];

		showMsg("Enter your private key (will be encrypted):");
		pressEnterToSkip();
		String privateKey;
		if (wt == WalletType.ERC20) {
			privateKey = KMCInputUtil.getInput("Private key", false, null, new ValidatorEthPrivateKey());
		} else {
			privateKey = StringUtils.trimToNull(KMCInputUtil.getPassword(null));
		}

		showMsg("Enter your mnemonic (will be encrypted):");
		pressEnterToSkip();
		String mnemonic = StringUtils.trimToNull(KMCInputUtil.getInput("Mnemonic", true, null, new ValidateMnemonic()));

		if (privateKey == null && mnemonic == null) {
			showMsg("You must provide at least one information, Private Key or Mnemonic seed words");
			if (KMCInputUtil.confirm("Do you understand?")) {
				showMsg("OK, now let's do it again");
				saveAWallet();
				return;
			}
			KMCClipboardUtil.clear();
			exit();
		}

		String address;
		showMsg("Address (required, will not be encrypted):");

		while (true) {
			if (wt == WalletType.ERC20) {
				address = KMCInputUtil.getInput("Address", false, null, new ValidateEthAddress());
			} else {
				address = KMCInputUtil.getInput(null, 68);
			}
			if (KMCInputUtil.confirm("Please carefully confirm this address again!")) {
				break;
			}
			showMsg("Address:");
		}

		showMsg("PRIVATE note - this content can NOT be changed later (optional, will be encrypted):");
		pressEnterToSkip();
		String privateNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		showMsg("PUBLIC note - this content can NOT be changed later (optional, will not be encrypted so should not contains private information):");
		pressEnterToSkip();
		String publicNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		saveAWallet_saveInfo(address, privateKey, wt, mnemonic, publicNote, privateNote);
	}

	@Override
	protected void readAWallet_choose(List<Wallet> wallets) throws Exception {
		log.trace("readAWallet_choose");
		MenuManager mm = new MenuManager(this);
		for (Wallet w : wallets) {
			mm.add(String.format(" %s (%s)", w.getAddress(), w.getWalletType()), "readAWallet_read", w);
		}
		int selection = getMenuSelection(mm, "Select a wallet");
		mm.processSelectedOption(selection);
	}

	@Override
	protected void readAWallet_read(Wallet wallet) throws Exception {
		log.trace("readAWallet_read");
		if (wallet == null)
			return;
		showMsg("* Address: %s", wallet.getAddress());
		if (wallet.getPublicNote() != null) {
			showMsg("* Note:\n%s", wallet.getPublicNote());
		}
		pressEnterToContinue();
		MenuManager mm = new MenuManager(this);
		mm.add("Go back to main menu", null);
		if (wallet.is(WalletType.ERC20)) {
			mm.add("Sign a simple transaction", "signSimpleEthereumTransactionForWallet", wallet);
		}
		mm.add("Copy private key to clipboard", "readAWallet_action", wallet, "copy", "private key");
		mm.add("Copy mnemonic to clipboard", "readAWallet_action", wallet, "copy", "mnemonic");
		mm.add("Show private key", "readAWallet_action", wallet, "show", "private key");
		mm.add("Show mnemonic", "readAWallet_action", wallet, "show", "mnemonic");
		mm.add("Show private note", "readAWallet_action", wallet, "show", "private note");
		mm.add("Show ALL private key, mnemonic and also private note", "readAWallet_action", wallet, "show", "all");

		int selection = getMenuSelection(mm, "* What do want to do?");
		mm.processSelectedOption(selection);
	}

	@SuppressWarnings("unused")
	private void readAWallet_action(Wallet wallet, String action, String target) throws Exception {
		log.trace("readAWallet_action");
		if (target.equals("all")) {
			String privateKey = decryptUsingExistsKeystore(wallet.getEncryptedPrivateKeyBuffer());
			String mnemonic = decryptUsingExistsKeystore(wallet.getEncryptedMnemonicBuffer());
			String privateNote = decryptUsingExistsKeystore(wallet.getEncryptedPrivateNoteBuffer());
			if (action.equals("show")) {
				if (privateKey != null) {
					showMsg("Private key: %s", privateKey);
				} else {
					showMsg("(Private Key does not exists)");
				}
				if (mnemonic != null) {
					showMsg("Mnemonic: %s", mnemonic);
				} else {
					showMsg("(Mnemonic does not exists)");
				}
				if (privateNote != null) {
					showMsg("Private note:\n%s", privateNote);
				} else {
					showMsg("(Private Note does not exists)");
				}
			}
		} else {
			String content = null;
			if (target.equals("private key")) {
				content = decryptUsingExistsKeystore(wallet.getEncryptedPrivateKeyBuffer());
			} else if (target.equals("mnemonic")) {
				content = decryptUsingExistsKeystore(wallet.getEncryptedMnemonicBuffer());
			} else if (target.equals("private note")) {
				content = decryptUsingExistsKeystore(wallet.getEncryptedPrivateNoteBuffer());
			}
			if (action.equals("copy")) {
				KMCClipboardUtil.setText(content, null);
			} else if (action.equals("show")) {
				showMsg("Here it is:\n%s", content);
			}
		}
		pressEnterToContinue();
	}

	@Override
	public void saveAnAccount() throws Exception {
		log.trace("saveAnAccount");
		showMsg("Website:");
		pressEnterToSkip();
		String website = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		showMsg("Account name:");
		String name = KMCInputUtil.getInput("Account Name", false, null);

		showMsg("Public note:");
		pressEnterToSkip();
		String publicNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		KMCClipboardUtil.clear();
		showMsg("NOTICE: Please DO NOT copy and paste password, just type it manually !!!");
		showMsg("Password (will be encrypted):");
		pressEnterToSkip();
		String password = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));
		if (password != null)
			KMCInputUtil.requireConfirmation(password);

		KMCClipboardUtil.clear();
		showMsg("NOTICE: Please DO NOT copy and paste 2fa private key, just type it manually !!!");
		showMsg("2FA private key (will be encrypted):");
		pressEnterToSkip();
		String priv2FA = KMCInputUtil.getInput2faPrivateKey();
		if (priv2FA != null)
			KMCInputUtil.requireConfirmation(priv2FA);

		showMsg("Private note (will be encrypted):");
		pressEnterToSkip();
		String privateNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		KMCClipboardUtil.clear();
		saveAnAccount_saveInfo(name, website, publicNote, password, priv2FA, privateNote);
	}

	@Override
	protected void readAnAccount_choose(List<Account> accounts) throws Exception {
		log.trace("readAnAccount_choose");
		MenuManager mm = new MenuManager(this);
		for (Account a : accounts) {
			StringBuilder option = new StringBuilder();
			option.append(a.getName());
			if (a.getWebsite() != null) {
				option.append(" (");
				option.append(a.getWebsite());
				option.append(")");
			}
			if (a.getPublicNote() != null) {
				option.append('\n');
				option.append(a.getPublicNote());
			}
			mm.add(option.toString(), "readAnAccount_read", a);
		}
		int selection = getMenuSelection(mm, "Select an account");
		mm.processSelectedOption(selection);
	}

	@Override
	protected void readAnAccount_read(Account account) throws Exception {
		log.trace("readAnAccount_read");
		if (account == null)
			return;
		showMsg("* Name: %s", account.getName());
		if (account.getWebsite() != null) {
			showMsg("* Website: %s", account.getWebsite());
		}
		if (account.getPublicNote() != null) {
			showMsg("* Note:\n%s", account.getPublicNote());
		}
		pressEnterToContinue();
		MenuManager mm = new MenuManager(this);
		mm.add("Go back to main menu", null);
		mm.add("Copy password to clipboard", "readAnAccount_action", account, "copy", "password");
		mm.add("Copy 2fa to clipboard", "readAnAccount_action", account, "copy", "2fa");
		mm.add("Show password", "readAnAccount_action", account, "show", "password");
		mm.add("Show 2fa", "readAnAccount_action", account, "show", "2fa");
		mm.add("Show private note", "readAnAccount_action", account, "show", "private note");
		mm.add("Show ALL password, 2fa and also private note", "readAnAccount_action", account, "show", "all");

		int selection = getMenuSelection(mm, "* What do want to do?");
		mm.processSelectedOption(selection);
	}

	@SuppressWarnings("unused")
	private void readAnAccount_action(Account account, String action, String target) throws Exception {
		log.trace("readAnAccount_action");
		if (target.equals("all")) {
			String password = decryptUsingExistsKeystore(account.getEncryptedPasswordBuffer());
			String _2fa = decryptUsingExistsKeystore(account.getEncrypted2FABuffer());
			String privateNote = decryptUsingExistsKeystore(account.getEncryptedPrivateNoteBuffer());
			if (action.equals("show")) {
				if (password != null) {
					showMsg("Password: %s", password);
				} else {
					showMsg("(Password does not exists)");
				}
				if (_2fa != null) {
					showMsg("private 2FA: %s", _2fa);
				} else {
					showMsg("(2FA does not exists)");
				}
				if (privateNote != null) {
					showMsg("Private note:\n%s", privateNote);
				} else {
					showMsg("(Private Note does not exists)");
				}
			}
		} else {
			String content = null;
			if (target.equals("password")) {
				content = decryptUsingExistsKeystore(account.getEncryptedPasswordBuffer());
			} else if (target.equals("2fa")) {
				content = decryptUsingExistsKeystore(account.getEncrypted2FABuffer());
			} else if (target.equals("private note")) {
				content = decryptUsingExistsKeystore(account.getEncryptedPrivateNoteBuffer());
			}
			if (action.equals("copy")) {
				KMCClipboardUtil.setText(content, null);
			} else if (action.equals("show")) {
				showMsg("Here it is:\n%s", content);
			}
		}
		pressEnterToContinue();
	}

	@Override
	public void signTransaction() throws Exception {
		log.trace("signTransaction");
		MenuManager mm = new MenuManager(this);
		mm.add("Return to main menu", null);
		mm.add("Sign an Ethereum tx", "signSimpleEthereumTransaction");
		mm.add("Sign an Contract ETH tx (on developing)", null);
		int selection = getMenuSelection(mm, "Choose a type of transaction");
		mm.processSelectedOption(selection);
	}

	@SuppressWarnings("unused")
	private void signSimpleEthereumTransaction() throws Exception {
		log.trace("signSimpleEthereumTransaction");
		MenuManager mm = new MenuManager(this);
		mm.add("Manually input", "signSimpleEthereumTransactionForWallet");
		AbstractKMCData.filter(this.dvc.getAllKMCFiles(), Wallet.class).stream()//
				.filter(w -> w.is(WalletType.ERC20)).forEach(w -> {
					mm.add(w.getAddress(), "signSimpleEthereumTransactionForWallet", w);
				});
		int selection = getMenuSelection(mm, "Select a wallet:");
		mm.processSelectedOption(selection);
	}

	@SuppressWarnings("unused")
	private void signSimpleEthereumTransactionForWallet(Wallet wallet) throws Exception {
		log.trace("signSimpleEthereumTransactionForWallet(Wallet)");

		String from = null, privKey = null;
		if (wallet != null) {
			if (!wallet.is(WalletType.ERC20)) {
				throw new RuntimeException("Not an ERC20 wallet");
			}
			from = StringUtils.trimToNull(wallet.getAddress());
			privKey = decryptUsingExistsKeystore(wallet.getEncryptedPrivateKeyBuffer());
		}
		IValidator<String> validatorEthAddress = new ValidateEthAddress();
		IValidator<String> validatorEthPrivKey = new ValidatorEthPrivateKey();

		if (from == null) {
			showMsg("From address:");
			from = KMCInputUtil.getInput("From address", false, null, validatorEthAddress);
		}
		if (privKey == null) {
			showMsg("Private key:");
			privKey = KMCInputUtil.getInput("Private key", false, null, validatorEthPrivKey);
		}

		showMsg("To address:");
		String to = KMCInputUtil.getInput("To address", false, null, validatorEthAddress);
		KMCInputUtil.requireConfirmation(to);

		if (from.equals(to)) {
			showMsg("Sender and receiver could not be the same");
			showMsg("(press Enter to try again)");
			KMCInputUtil.getRawInput(null);
			return;
		}

		showMsg("Nonce:");
		int nonce = KMCInputUtil.getInput("Nonce", false, input -> Integer.valueOf(input), //
				new ValidateMustBeInteger(), new ValidateNumberNotNegative<Integer>()).intValue();

		showMsg("Amount of ETH to transfer:");
		double amtEthTransfer = KMCInputUtil.getInput("ETH amount", false, input -> Double.valueOf(input), //
				new ValidateMustBeDouble(), new ValidateNumberNotNegative<Double>());

		int gasLimit = 21000;
		showMsg("Gas limit:");
		showMsg("(default %s, press Enter to skip)", gasLimit);
		Integer tmp = KMCInputUtil.getInput("Gas limit", true, input -> Integer.valueOf(input), //
				new ValidateMustBeInteger(), new ValidateNumberNotNegative<Integer>());
		if (tmp != null) {
			gasLimit = tmp.intValue();
		}

		int gwei = 41;
		showMsg("Gas price in Gwei:");
		showMsg("(default %s Gwei, press Enter to skip)", gwei);
		tmp = KMCInputUtil.getInput("Gas price", true, input -> Integer.valueOf(input), //
				new ValidateMustBeInteger(), new ValidateNumberNotNegative<Integer>());
		if (tmp != null) {
			gwei = tmp.intValue();
		}

		IBlockChain bc = new EthereumBlockChain();
		ITransactionInput input = new SimpleEthereumTransactionInput(from, to, amtEthTransfer, nonce, gwei, gasLimit);
		IUnlockMethod unlock = new UnlockByPrivateKey(privKey);

		MenuManager mm = new MenuManager(this);
		mm.add("Sign this transaction", "showSignedTransaction", bc, input, unlock);
		mm.add("Re-make", "signSimpleEthereumTransactionForWallet", wallet);
		mm.add("Cancel", null);
		int selection = getMenuSelection(mm, "Sign it?");
		mm.processSelectedOption(selection);
	}

	@SuppressWarnings("unused")
	private void showSignedTransaction(IBlockChain bc, ITransactionInput input, IUnlockMethod unlock) throws Exception {
		log.trace("showSignedTransaction");
		ISignedTransaction stx = bc.signSimpleTransaction(input, unlock);
		if (stx instanceof EthereumSignedTransaction) {
			EthereumSignedTransaction estx = (EthereumSignedTransaction) stx;
			showMsg("Transfer %s ETH", KMCStringUtil.beautiNumber(estx.getTransferAmt(-18)));
			showMsg("From %s to %s", estx.getFrom(), estx.getTo());
			showMsg("Gas price %s gwei, limit %s", KMCStringUtil.beautiNumber(String.valueOf(estx.getGwei())),
					KMCStringUtil.beautiNumber(String.valueOf(estx.getGasLimit())));
			showMsg("Signed Tx: %s", estx.getSignedTx());
			KMCClipboardUtil.setText(estx.getSignedTx(), "SignedTX");
		}
		pressEnterToContinue();
	}

	@Override
	protected void showMsg(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	@Override
	protected void askContinueOrExit(String question) throws Exception {
		log.trace("askContinueOrExit");
		if (!KMCInputUtil.confirm(question == null ? "Continue?" : question)) {
			exit();
		}
	}

	@Override
	protected void exit() throws Exception {
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				String[] cls = new String[] { "cmd.exe", "/c", "cls" };
				Runtime.getRuntime().exec(cls);
			} else if (SystemUtils.IS_OS_LINUX) {
				Runtime.getRuntime().exec("clear");
			} else {
				throw new NotImplementedException("Clear screen for " + SystemUtils.OS_NAME);
			}
		} catch (Exception e) {
			if (e instanceof NotImplementedException) {
				throw e;
			}
			if (Configuration.DEBUG) {
				e.printStackTrace();
			}
		}
		showMsg("Thanks for using our production");
		System.exit(0);
	}

	private int getMenuSelection() {
		log.trace("getMenuSelection");
		String input = KMCInputUtil.getInput("Action: ", 1);
		if (input == null) {
			return 0;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected void pressEnterToSkip() {
		showMsg("(press Enter to skip)");
	}

	protected void pressEnterToContinue() {
		showMsg(" ... Press Enter to continue ...");
		KMCInputUtil.getRawInput(null);
	}

	private int getMenuSelection(MenuManager mm, String msg) {
		log.trace("getMenuSelection");
		return getMenuSelection(mm, msg, mm.countMenus());
	}

	private int getMenuSelection(MenuManager mm, String msg, int maxSize) {
		log.trace("getMenuSelection");
		int selection;
		while (true) {
			mm.showOptionList(msg);
			selection = getMenuSelection();
			if (selection < 1 || selection > maxSize) {
				showMsg("Invalid option");
				continue;
			}
			return selection;
		}
	}
}
