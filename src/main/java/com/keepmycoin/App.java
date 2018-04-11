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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.js.JavaScript;

public class App {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		log.debug("App launch");
		System.out.println(String.format("KMC version: %f", Configuration.APP_VERSION));
		initialize(args);
		log.debug("initialize done");
		
		log.debug("Launch console/interface");
		IKeepMyCoin kmcProcessor = new KeepMyCoinConsole(); // ? : new KeepMyCoinGUI();
		kmcProcessor.launch();
	}
	
	private static void initialize(String[] args) throws Exception {
		log.trace("initialize");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JavaScript.initialize();
			}
		}).start();

		List<String> larg = Arrays.asList(args).stream().map(a -> a.trim()).filter(a -> a.length() > 0)
				.collect(Collectors.toList());
		if (larg.size() < 1)
			return;
		Configuration.DEBUG = larg.contains("debug");

		//
		File fixedKMCDevice = null;
		if (larg.stream().anyMatch(a -> a.startsWith("device="))) {
			String folder = larg.stream().filter(a -> a.startsWith("device=")).findAny().get().split("=")[1];
			if (StringUtils.isNotBlank(folder)) {
				fixedKMCDevice = new File(folder);
				if (!fixedKMCDevice.exists()) {
					fixedKMCDevice = null;
				} else {
					log.info("KMC Device is fixed to " + fixedKMCDevice.getAbsolutePath());
				}
			}
		}
		Configuration.KMC_DEVICE = fixedKMCDevice;

		//
		File fixedKMCStorage = null;
		if (larg.stream().anyMatch(a -> a.startsWith("kmc="))) {
			String folder = larg.stream().filter(a -> a.startsWith("kmc=")).findAny().get().split("=")[1];
			if (StringUtils.isNotBlank(folder)) {
				fixedKMCStorage = new File(folder);
				if (!fixedKMCStorage.exists()) {
					fixedKMCStorage = null;
				} else {
					log.info("KMC folder is fixed to " + fixedKMCStorage.getAbsolutePath());
				}
			}
		}
		if (fixedKMCStorage == null && Configuration.KMC_DEVICE != null) {
			fixedKMCStorage = Paths.get(Configuration.KMC_DEVICE.getAbsolutePath(), Configuration.KMC_FOLDER_DEFAULT).toFile();
			if (!fixedKMCStorage.exists()) {
				fixedKMCStorage = null;
			} else {
				log.info("KMC folder is fixed to " + fixedKMCStorage.getAbsolutePath());
			}
		}
		Configuration.KMC_FOLDER = fixedKMCStorage;

		//
		if (larg.stream().anyMatch(a -> a.startsWith("ext="))) {
			String ext = larg.stream().filter(a -> a.startsWith("ext=")).findAny().get().split("=")[1];
			if (StringUtils.isNotBlank(ext)) {
				Configuration.EXT = ext;
			}
		}
	}
}
