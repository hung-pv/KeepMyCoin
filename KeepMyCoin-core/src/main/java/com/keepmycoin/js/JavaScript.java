package com.keepmycoin.js;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;

import com.keepmycoin.utils.KMCJavaScriptUtil;

public class JavaScript {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JavaScript.class);
	private static final String ENGINE_NAME = "nashorn";

	public static final JavaScript ENGINE_AES;
	public static final JavaScript ENGINE_MEW;

	static {
		log.info("Setting up Script Engines");
		System.setProperty("nashorn.args", "--language=es6");

		JavaScript engine = null;
		try {
			engine = new JavaScript("aes256-engine", "aes256-ext");
		} catch (Exception e) {
			log.fatal("Unable to load JS engine of AES", e);
			System.exit(1);
		}
		ENGINE_AES = engine;
		engine = null;
		try {
			engine = new JavaScript("etherwallet-master", "mew-ext");
		} catch (Exception e) {
			log.fatal("Unable to load JS engine of MEW", e);
			System.exit(1);
		}
		ENGINE_MEW = engine;
		log.info("Script Engines had been setup successfully");
	}

	public static void initialize() {
	}

	private final ScriptEngine engine;

	public JavaScript(String... resourceFileNames) {
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			this.engine = factory.getEngineByName(ENGINE_NAME);
			StringBuilder script = new StringBuilder();
			for (String fileName : resourceFileNames) {
				script.append(loadFileFromResource(fileName));
				script.append('\n');
			}
			this.engine.eval(script.toString());
		} catch (Exception e) {
			throw new RuntimeException("Unable to load JS from files", e);
		}
	}
	
	public void execute(CharSequence script) throws ScriptException {
		this.engine.eval(script.toString());
	}
	
	public String getVariableValue(String varName) {
		return String.valueOf(this.engine.get(varName));
	}
	
	public String executeAndGetValue(CharSequence script, String outputVarName) throws ScriptException {
		this.execute(script);
		return this.getVariableValue(outputVarName);
	}
	
	public String[] executeAndGetValues(CharSequence script, String...outputVarNames) throws ScriptException {
		this.execute(script);
		return (String[])Arrays.asList(outputVarNames).stream().map(v -> this.getVariableValue(v)).toArray();
	}

	private static String loadFileFromResource(String fileName) throws IOException {
		if (fileName == null)
			return null;
		if (!fileName.toLowerCase().endsWith(".js"))
			fileName += ".js";
		ClassLoader classLoader = KMCJavaScriptUtil.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
}
