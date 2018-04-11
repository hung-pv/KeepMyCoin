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
				if (file.isDirectory()) continue;
				AbstractKMCData kmcData = KMCFileUtil.readFileToKMCData(file);
				if (kmcData != null)
					result.add(kmcData);
			}
		}
		return result;
	}

	public boolean exists() {
		return this.drive != null && this.drive.exists() && this.drive.isDirectory();
	}

	public boolean isValid() {
		return this.exists() && this.getIdFile().exists();
	}

	public File getIdFile() {
		return this.getFile(ID_FILE_NAME);
	}
}
