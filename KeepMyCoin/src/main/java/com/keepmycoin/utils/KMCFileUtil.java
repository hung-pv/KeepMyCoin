package com.keepmycoin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import com.keepmycoin.Configuration;

public class KMCFileUtil {
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
}
