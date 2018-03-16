package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import com.keepmycoin.console.MenuManager;
import com.keepmycoin.exception.OSNotImplementedException;
import com.keepmycoin.utils.KMCClipboardUtil;
import com.keepmycoin.utils.KMCFileUtil;
import com.keepmycoin.utils.KMCInputUtil;
import com.keepmycoin.utils.Validator;

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

		List<File> fValidRoots = KMCFileUtil.getFileRoots().stream().filter(r -> r.listFiles().length == 0).collect(Collectors.toList());
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
		}
		
		mm.showOptionList("\n==========\n\nHello! Today is a beautiful day, what do want to do?");
		int selection = getMenuSelection();
		if (!validateMenuSelection(selection, mm)) {
			showMsg("Invalid option!");
			launchMenu();
			return;
		}
		mm.getOptionBySelection(selection).processMethod(this);
	}

	@Override
	protected void generateNewKeystore_getInitPwd() throws Exception {
		log.trace("generateNewKeystore_getInitPwd");
		String pwd = KMCInputUtil.getPassword_required("Passphrase (up to 16 chars): ", Configuration.DEBUG ? 1 : 8);
		KMCInputUtil.requireConfirmation(pwd);
		generateNewKeystore_fromInitPwd(pwd);
	}

	@Override
	protected void generateNewKeystore_confirmSavedMnemonic(String mnemonic, byte[] keyWithBIP39Encode, byte[] key,
			String pwd) throws Exception {
		log.trace("generateNewKeystore_confirmSavedMnemonic");
		while (!KMCInputUtil.confirm("Did you saved the mnemonic to somewhere?")) {
			showMsg("Please carefully save it!");
		}
		if (!Configuration.DEBUG) {
			KMCClipboardUtil.clear();	
		}
		generateNewKeystore_confirmMnemonic(mnemonic, keyWithBIP39Encode, key, pwd);
	}

	@Override
	protected void generateNewKeystore_confirmMnemonic(String mnemonic, byte[] keyWithBIP39Encode, byte[] key,
			String pwd) throws Exception {
		log.trace("generateNewKeystore_confirmMnemonic");
		showMsg("For sure you already saved these seed words, you have to typing these words again:");
		KMCInputUtil.requireConfirmation(mnemonic);
		showMsg("Good job! Keep these seed words safe");
		generateNewKeystore_save(mnemonic, keyWithBIP39Encode, key, pwd);
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
			if ("cancel".equalsIgnoreCase(mnemonic)) {
				return;
			}
		} while (!Validator.isValidSeedWords(mnemonic));

		String pwd = KMCInputUtil.getPassword("Passphrase: ");
		if (pwd == null) {
			showMsg("Cancelled");
			return;
		}
		
		restoreKeystore_processUsingInput(mnemonic, pwd);
	}

	private int getMenuSelection() {
		String input = KMCInputUtil.getInput("Action: ", 1);
		if (input == null) {
			return 0;
		}
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private boolean validateMenuSelection(int option, MenuManager mm) {
		return option < 1 ? false : option <= mm.countMenus();
	}

	@Override
	protected void showMsg(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

}
