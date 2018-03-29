package com.keepmycoin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class KMCJavaScriptUtil {
	private static final String ENGINE_NAME = "nashorn";
	
	static {
		System.setProperty("nashorn.args", "--language=es6");
	}
	
	public static String loadFile(String fileName) throws IOException {
		ClassLoader classLoader = KMCJavaScriptUtil.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName + ".js").getFile());
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
	
	public static ScriptEngine setupNewScriptEngine(String...jsFiles) throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName(ENGINE_NAME);
		StringBuilder script = new StringBuilder();
		for (String fileName : jsFiles) {
			script.append(loadFile(fileName));
			script.append('\n');
		}
		engine.eval(script.toString());
		return engine;
	}
	
	public static Object executeAndGetValue(String varName, String additionalScript, ScriptEngine scriptEngine) throws Exception {
		StringBuilder script = new StringBuilder();
		if (additionalScript != null) {
			script.append(additionalScript);
		}
		scriptEngine.eval(script.toString());
		return scriptEngine.get(varName);
	}
	
	public static String buildJavaScriptArray(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		sb.append(StringUtils.join(KMCArrayUtil.unsignedBytes(buffer), ','));
		sb.append(" ]");
		return sb.toString();
	}
}
