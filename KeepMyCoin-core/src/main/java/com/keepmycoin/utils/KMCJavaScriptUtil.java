package com.keepmycoin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;

public class KMCJavaScriptUtil {
	public static String loadFile(String fileName) throws IOException {
		ClassLoader classLoader = KMCJavaScriptUtil.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName + ".js").getFile());
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
	
	public static Object executeAndGetValue(String varName, String additionalScript, String...files) throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		StringBuilder script = new StringBuilder();
		for (String fileName : files) {
			script.append(loadFile(fileName));
			script.append('\n');
		}
		if (additionalScript != null) {
			script.append(additionalScript);
		}
		engine.eval(script.toString());
		return engine.get(varName);
	}
}
