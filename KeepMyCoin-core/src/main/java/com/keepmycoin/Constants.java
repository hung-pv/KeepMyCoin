package com.keepmycoin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
	
	public static final double APP_VERSION = 0.1;

	public static final String FILE_IMG_EXT = "dll";
	public static final String FILE_WALLET_EXT = "ocx";
	public static final String FILE_2FA_EXT = "2fa";
	
	//TODO remove this
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yy");
	public static String getAdditionalDetailInformation() {
		return String.format("JVM %s, version %f, at %s", Runtime.class.getPackage().getImplementationVersion(),
				APP_VERSION, sdf.format(new Date()));
	}
}
