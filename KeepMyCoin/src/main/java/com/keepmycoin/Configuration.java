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
		APP_VERSION = Double.parseDouble(props.getProperty("kmc.version"));
	}

	public static boolean DEBUG;
	public static boolean MODE_CONSOLE;
	public static File KMC_FOLDER;
	public static final String EXT_DEFAULT = "kmc";
	public static String EXT = EXT_DEFAULT;
	public static final String KEYSTORE_NAME = new String(Base64.getDecoder().decode("S0VFUF9USElTX1NBRkUua21j"),
			StandardCharsets.UTF_8);
	public static final double APP_VERSION;
	public static final int TIME_OUT_SEC = 180;
}
