package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.keepmycoin.console.MenuManager;
import com.keepmycoin.data.Account;
import com.keepmycoin.data.Wallet;
import com.keepmycoin.exception.OSNotImplementedException;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCInputUtil;

public class KeepMyCoinConsole extends AbstractApplicationSkeleton {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KeepMyCoinConsole.class);

	@Override
	protected void preLaunch() throws Exception {
		log.trace("preLaunch");
		log.debug("Console mode");
		super.preLaunch();
	}

	@Override
	protected void findKMCDevice() throws Exception {
		log.trace("findKMCDevice");
		if (!SystemUtils.IS_OS_WINDOWS) {
			throw new OSNotImplementedException("detecting KMC Device");
		}
		List<File> roots = KMCFileUtil.getFileRoots();
		Device: for (File root : roots) {
			KMCDevice drive = new KMCDevice(root);
			if (!drive.isValid()) {
				continue Device;
			}
			try {
				File[] listOfFiles = root.listFiles();
				FileOnDevice: for (File file : listOfFiles) {
					if (file.isDirectory()) {
						showMsg("KMC Device %s should not contains any directory", root.getAbsolutePath());
						showMsg("SKIP this device");
						continue Device;
					} else { // File
						if (file.getName().equalsIgnoreCase(drive.getIdFile().getName())) {
							continue FileOnDevice;
						} else if (KMCFileUtil.isFileExt(file, Configuration.EXT_DEFAULT)
								|| KMCFileUtil.isFileExt(file, "cmd") //
								|| KMCFileUtil.isFileExt(file, "sh") //
								|| KMCFileUtil.isFileExt(file, "jar")) {
							continue FileOnDevice;
						} else if (file.getName().equalsIgnoreCase(Configuration.KEYSTORE_NAME)) {
							showMsg("WARNING: Found keystore file '%s' on your KMC Device",
									Configuration.KEYSTORE_NAME);
							showMsg("You should NOT save it here");
							showMsg("Push it to cloud storage service like Google Drive, Drop Box, Mediafire,...");
							showMsg("When need that file, download and save it to your local computer");
							continue FileOnDevice;
						} else {
							showMsg("KMC Device '%s' should not contains any file except *.%s so this device will be SKIPPED",
									root.getAbsolutePath(), Configuration.EXT_DEFAULT);
							continue Device;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue Device;
			}
			this.dvc = new KMCDevice(root);
			log.debug("KMC Device had been set to " + root.getAbsolutePath());
			return;
		}
		this.dvc = new KMCDevice(null);
	}

	@Override
	protected void setupKMCDevice() throws Exception {
		log.trace("setupKMCDevice");
		showMsg("Welcome, today is beautiful to see you :)");
		showMsg("How to setup:");
		showMsg(" 1. Prepare a new USB, format it, make sure it already cleared, no file remain");
		showMsg(" ... Press Enter to continue ...");
		KMCInputUtil.getRawInput(null);
		showMsg(" 2. Plug it in your computer");
		showMsg(" ... Press Enter to continue ...");
		KMCInputUtil.getRawInput(null);
		showMsg("Now I will list some devices that are detected from your computer");
		showMsg(" ... Press Enter to continue ...");
		KMCInputUtil.getRawInput(null);

		List<File> fValidRoots = KMCFileUtil.getFileRoots().stream().filter(r -> r.listFiles().length == 0)
				.collect(Collectors.toList());
		if (fValidRoots.isEmpty()) {
			showMsg("There is no USB device meet conditions, please check again");
			showMsg("Make sure it is completely EMPTY");
			showMsg("then run me again");
			System.exit(0);
		}

		while (true) {
			for (int i = 0; i < fValidRoots.size(); i++) {
				File fValidRoot = fValidRoots.get(i);
				showMsg("\t%d. %s", i + 1, fValidRoot.getAbsolutePath());
			}
			int selection;
			while (true) {
				selection = KMCInputUtil.getInt("Select a device: ");
				if (selection < 1 || selection > fValidRoots.size()) {
					showMsg("Invalid selection");
					continue;
				}
				break;
			}

			File selected = fValidRoots.get(selection - 1);
			if (!KMCInputUtil.confirm(//
					String.format("Are you sure to select '%s' ?", selected.getAbsolutePath()))) {
				showMsg("Select again !");
				continue;
			}

			this.dvc = new KMCDevice(selected);
			FileUtils.write(this.dvc.getIdFile(), "", StandardCharsets.UTF_8);
			showMsg("Setup done, from now on, your usb stick will be called as a 'KMC Device'");
			showMsg("KMC Device is a short name of KeepMyCoin device");
			showMsg("Now you can start by pressing Enter");
			KMCInputUtil.getRawInput(null);
			break;
		}
	}

	@Override
	protected void launchUserInterface() throws Exception {
		log.trace("launchUserInterface");
		log.debug("Console mode doesn't have user interface");
	}

	@Override
	protected void launchMenu() throws Exception {
		log.trace("launchMenu");
		MenuManager mm = new MenuManager();

		if (!isKeystoreExists()) {
			mm.add("Generate keystore", "generateNewKeystore");
			mm.add("Restore keystore", "restoreKeystore");
		} else {
			mm.add("Save a wallet", "saveAWallet");
			mm.add("Read a wallet", "readAWallet");
			mm.add("Save an account", "saveAnAccount");
			mm.add("Read an account", "readAnAccount");
		}

		int selection = getMenuSelection(mm, "\n==========\n\nHello! Today is a beautiful day, what do want to do?");
		mm.getOptionBySelection(selection).processMethod(this);
	}

	@Override
	protected void generateNewKeystore_getInitPassPharse() throws Exception {
		log.trace("generateNewKeystore_getInitPassPharse");
		String pwd = KMCInputUtil.getPassword_required("Passphrase (up to 16 chars): ", Configuration.DEBUG ? 1 : 8);
		KMCInputUtil.requireConfirmation(pwd);
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
	protected void loadKeystore_getPasspharse() throws Exception {
		log.trace("loadKeystore_getPasspharse");
		String pwd = KMCInputUtil.getPassword_required("Passphrase: ", 1);
		loadKeystore_processUsingPasspharse(pwd);
	}

	@Override
	public void saveAWallet() throws Exception {
		log.trace("saveAWallet");
		MenuManager mm = new MenuManager();
		for (WalletType wt : WalletType.values()) {
			mm.add(wt.getDisplayText(), null);
		}
		int selection = getMenuSelection(mm, "Select wallet type:");
		WalletType wt = WalletType.values()[selection - 1];

		showMsg("Enter your private key (will be encrypted):");
		showMsg("(press Enter to skip)");
		String privateKey = StringUtils.trimToNull(KMCInputUtil.getPassword(null));

		showMsg("Enter your mnemonic (will be encrypted):");
		showMsg("(press Enter to skip)");
		String mnemonic;
		while (true) {
			mnemonic = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));
			if (mnemonic == null) {
				break;
			}
			if (mnemonic.split("\\s").length % 12 != 0) {
				showMsg("Mnemonic incorrect (can not devided by 12)");
				showMsg("Again: ");
				continue;
			}
		}

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
			address = KMCInputUtil.getInput(null, 68);
			if (KMCInputUtil.confirm("You sure? Please confirm this address again!")) {
				break;
			}
			showMsg("Address:");
		}

		showMsg("PRIVATE note - this content can NOT be changed later (optional, will be encrypted):");
		showMsg("(press Enter to skip)");
		String privateNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		showMsg("PUBLIC note - this content can NOT be changed later (optional, will not be encrypted so should not contains private information):");
		showMsg("(press Enter to skip)");
		String publicNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		saveAWallet_saveInfo(address, privateKey, wt, mnemonic, publicNote, privateNote);
	}

	@Override
	protected void readAWallet_choose(List<Wallet> wallets) throws Exception {
		log.trace("readAWallet_choose");
		MenuManager mm = new MenuManager();
		for (Wallet w : wallets) {
			mm.add(String.format(" %s (%s)", w.getAddress(), w.getWalletType()), "readAWallet_read", w);
		}
		int selection = getMenuSelection(mm, "Select a wallet");
		mm.getOptionBySelection(selection).processMethod(this);
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
		MenuManager mm = new MenuManager();
		mm.add("Go back to main menu", null);
		mm.add("Copy private key to clipboard", "readAWallet_action", wallet, "copy", "private key");
		mm.add("Copy mnemonic to clipboard", "readAWallet_action", wallet, "copy", "mnemonic");
		mm.add("Show private key", "readAWallet_action", wallet, "show", "private key");
		mm.add("Show mnemonic", "readAWallet_action", wallet, "show", "mnemonic");
		mm.add("Show private note", "readAWallet_action", wallet, "show", "private note");
		mm.add("Show ALL private key, mnemonic and also private note", "readAWallet_action", wallet, "show", "all");

		int selection = getMenuSelection(mm, "* What do want to do?");
		mm.getOptionBySelection(selection).processMethod(this);
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
	}

	@Override
	public void saveAnAccount() throws Exception {
		log.trace("saveAnAccount");
		showMsg("Website:");
		showMsg("(press Enter to skip)");
		String website = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		showMsg("Account name:");
		String name = KMCInputUtil.getInput("Account Name", false, ".+", "Must not empty", null);

		showMsg("Public note:");
		showMsg("(press Enter to skip)");
		String publicNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		KMCClipboardUtil.clear();
		showMsg("NOTICE: Please DO NOT copy and paste password, just type it manually !!!");
		showMsg("Password (will be encrypted):");
		showMsg("(press Enter to skip)");
		String password = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		KMCClipboardUtil.clear();
		showMsg("NOTICE: Please DO NOT copy and paste 2fa private key, just type it manually !!!");
		showMsg("2FA private key (will be encrypted):");
		showMsg("(press Enter to skip)");
		String priv2FA = KMCInputUtil.getInput("2fa private key", true, "^[aA-zZ0-9]{4,}$",
				"Alphabet and numeric only, more than 4 characters", null);

		showMsg("Private note (will be encrypted):");
		showMsg("(press Enter to skip)");
		String privateNote = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));

		saveAnAccount_saveInfo(name, website, publicNote, password, priv2FA, privateNote);
	}

	@Override
	protected void readAnAccount_choose(List<Account> accounts) throws Exception {
		log.trace("readAnAccount_choose");
		MenuManager mm = new MenuManager();
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
		mm.getOptionBySelection(selection).processMethod(this);
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
		MenuManager mm = new MenuManager();
		mm.add("Go back to main menu", null);
		mm.add("Copy password to clipboard", "readAnAccount_action", account, "copy", "password");
		mm.add("Copy 2fa to clipboard", "readAnAccount_action", account, "copy", "2fa");
		mm.add("Show password", "readAnAccount_action", account, "show", "password");
		mm.add("Show 2fa", "readAnAccount_action", account, "show", "2fa");
		mm.add("Show private note", "readAnAccount_action", account, "show", "private note");
		mm.add("Show ALL password, 2fa and also private note", "readAnAccount_action", account, "show", "all");

		int selection = getMenuSelection(mm, "* What do want to do?");
		mm.getOptionBySelection(selection).processMethod(this);
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

	private void exit() throws Exception {
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
