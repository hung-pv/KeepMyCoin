package com.keepmycoin.test;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.keepmycoin.KMCDevice;
import com.keepmycoin.utils.KMCFileUtil;

public class ExternalDevices {
	
	@Test
	public void testDetectDevices() {
		List<File> roots = KMCFileUtil.getFileRoots();
		assertNotNull(roots);
		assertFalse(roots.isEmpty());
	}

	@Test
	public void testUSBConnected() {
		File rootsContainsUsbId = null;
		for (File root : KMCFileUtil.getFileRoots()) {
			File id = Paths.get(root.getAbsolutePath(), KMCDevice.ID_FILE_NAME).toFile();
			if (id.exists()) {
				rootsContainsUsbId = id;
				break;
			}
		}
		assertNotNull(rootsContainsUsbId);
	}
}
