package com.keepmycoin;

import java.io.File;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.utils.KMCClipboardUtil;

public class App {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		log.debug("App launch");
		initialize(args);
		log.debug("initialize done");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				KMCClipboardUtil.clear();
			}
		});

		log.debug("Launch console/interface");
		// IKeepMyCoin kmcProcessor = Configuration.MODE_CONSOLE ? new
		// KeepMyCoinConsole() : new KeepMyCoinGUI();
		IKeepMyCoin kmcProcessor = new KeepMyCoinConsole();
		kmcProcessor.launch();
	}

	private static void initialize(String[] args) throws Exception {
		log.trace("initialize");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		List<String> larg = Arrays.asList(args).stream().map(a -> a.trim()).filter(a -> a.length() > 0)
				.collect(Collectors.toList());
		if (larg.size() < 1)
			return;
		Configuration.DEBUG = larg.contains("debug");
		Configuration.MODE_CONSOLE = larg.contains("console") || larg.contains("c");

		File fixedKMCFolder = null;
		if (larg.stream().anyMatch(a -> a.startsWith("kmc="))) {
			String folder = larg.stream().filter(a -> a.startsWith("kmc=")).findAny().get().split("=")[1];
			if (StringUtils.isNotBlank(folder)) {
				fixedKMCFolder = new File(folder);
				if (!fixedKMCFolder.exists()) {
					fixedKMCFolder = null;
				} else {
					log.info("KMC Device is fixed to " + fixedKMCFolder.getAbsolutePath());
				}
			}
		}
		Configuration.KMC_FOLDER = fixedKMCFolder;
	}
}
