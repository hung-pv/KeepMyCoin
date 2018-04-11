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
package com.keepmycoin.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.keepmycoin.App;
import com.keepmycoin.data.AbstractKMCData;

public class KMCFileUtil {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KMCFileUtil.class);
	
	public static File getCurrentJar() {
		try {
			File f = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			if (f.isDirectory() || !f.getName().endsWith(".jar")) {
				log.info("Not a JAR file");
			}
			return f;
		} catch (Exception e) {
			log.error("Error while trying to get current JAR");
			return null;
		}
	}
	
	public static boolean isFileExists(File file) {
		return file != null && file.exists() && file.isFile();
	}

	public static boolean isFileExt(File file, String...exts) {
		if (file == null) {
			return false;
		}
		String name = file.getName().toLowerCase();
		for (String ext : exts) {
			if (name.endsWith("." + ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private static String[] candidateClassNames = new String[] { "KeyStore", "Wallet", "Account", "Note" };

	public static AbstractKMCData readFileToKMCData(File file) throws Exception {
		if (file == null)
			return null;
		String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		for (String clzName : candidateClassNames) {
			if (content.contains(String.format("\"%s\"", clzName))) {
				try {
					return (AbstractKMCData) KMCJsonUtil.parse(content,
							Class.forName("com.keepmycoin.data." + clzName));
				} catch (Exception e) {
					log.error("Error while reading KMC data file " + file.getAbsolutePath(), e);
				}
			}
		}
		return null;
	}

	public static void writeFile(File file, AbstractKMCData data) throws Exception {
		writeFile(file, KMCJsonUtil.toJSon(data), true);
	}

	public static void writeFile(File file, String content, boolean readonly) throws Exception {
		if (file.exists()) {
			throw new RuntimeException("File already exists");
		}
		FileUtils.write(file, content, StandardCharsets.UTF_8);
		if (readonly) {
			setReadonlyQuietly(file);
		}
	}

	public static void setReadonlyQuietly(File file) {
		try {
			file.setReadOnly();
		} catch (Throwable t) {
		}
	}
}
