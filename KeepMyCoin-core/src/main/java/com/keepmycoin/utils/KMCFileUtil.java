package com.keepmycoin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import com.keepmycoin.Configuration;
import com.keepmycoin.data.AbstractKMCData;

public class KMCFileUtil {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KMCFileUtil.class);

	public static List<File> getFileRoots() {
		if (!Configuration.DEBUG)
			return Arrays.asList(File.listRoots());
		List<File> roots = new ArrayList<>();
		if (SystemUtils.IS_OS_WINDOWS) {
			roots.add(new File("C:\\USB1"));
			roots.add(new File("C:\\USB2"));
		} else {
			roots.add(new File("/tmp/USB1"));
			roots.add(new File("/tmp/USB2"));
		}
		roots.forEach(f -> {
			if (!f.exists()) {
				try {
					FileUtils.forceMkdir(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return roots;
	}

	public static boolean isFileExt(File file, String ext) {
		if (file == null || ext == null) {
			return false;
		}
		return file.getName().toLowerCase().endsWith("." + ext.toLowerCase());
	}

	private static String[] candidateClassNames = new String[] { "KeyStore", "Wallet" };

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
