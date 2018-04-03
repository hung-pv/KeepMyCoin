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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class Configuration {

	static {
		String resourceName = "config.properties";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
			props.load(resourceStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		double appVersion;
		try {
			appVersion = Double.parseDouble(props.getProperty("kmc.version"));
		} catch (NumberFormatException e) {
			appVersion = 1.1;
		}
		APP_VERSION = appVersion;
	}

	public static boolean DEBUG;
	public static boolean MODE_CONSOLE = false;
	public static File KMC_FOLDER;
	public static final String EXT_DEFAULT = "kmc";
	public static String EXT = EXT_DEFAULT;
	public static final String KEYSTORE_NAME = new String(Base64.getDecoder().decode("S0VFUF9USElTX1NBRkUua21j"),
			StandardCharsets.UTF_8);
	public static final double APP_VERSION;
	public static final int TIME_OUT_SEC = 180;
}
