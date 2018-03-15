package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Configuration {
	public static boolean DEBUG;
	public static boolean MODE_CONSOLE;
	public static File KMC_FOLDER;
	public static final String EXT_DEFAULT = "kmc";
	public static final String KEYSTORE_NAME = new String(Base64.getDecoder().decode("S0VFUF9USElTX1NBRkUua21j"), StandardCharsets.UTF_8);
	public static final double APP_VERSION = 0.2;
}
