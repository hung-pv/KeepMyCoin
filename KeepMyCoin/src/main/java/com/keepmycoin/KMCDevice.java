package com.keepmycoin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.keepmycoin.data.AbstractKMCData;
import com.keepmycoin.utils.KMCFileUtil;

public class KMCDevice {

	public static final String ID_FILE_NAME = new String(Base64.getDecoder().decode("ZGV2aWNlLmF1dGg="),
			StandardCharsets.UTF_8);

	private File drive;

	public KMCDevice(File drive) {
		this.drive = drive;
	}

	public File getFile(String... path) {
		return this.drive == null ? null : Paths.get(this.drive.getAbsolutePath(), path).toFile();
	}

	public File[] getFiles() {
		return this.drive == null ? null : this.drive.listFiles();
	}

	public List<AbstractKMCData> getAllKMCFiles() throws Exception {
		List<AbstractKMCData> result = new ArrayList<>();
		File[] files = this.getFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				result.add(KMCFileUtil.readFileToKMCData(file));
			}
		}
		return result;
	}

	public boolean isValid() {
		return this.drive != null && this.drive.exists() && this.drive.isDirectory() && this.getIdFile().exists();
	}

	public String getAbsolutePath() {
		return this.drive == null ? null : this.drive.getAbsolutePath();
	}

	public File getIdFile() {
		return this.getFile(ID_FILE_NAME);
	}
}
