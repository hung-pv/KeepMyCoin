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
package com.keepmycoin.js;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

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
		log.debug("Loading AES");
		try {
			engine = new JavaScript("aes256-engine", "aes256-ext");
		} catch (Exception e) {
			log.fatal("Unable to load JS engine of AES", e);
			System.exit(1);
		}
		ENGINE_AES = engine;
		engine = null;
		log.debug("Loading MEW");
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
				log.debug("Script length before: " + script.length());
				script.append(loadFileFromResource(fileName));
				log.debug("Script length after: " + script.length());
				script.append('\n');
			}
			log.debug("Total script length: " + script.length());
			this.engine.eval(script.toString());
		} catch (Exception e) {
			throw new RuntimeException("Unable to load JS from files", e);
		}
	}
	
	public void execute(CharSequence script, Object...scriptArgs) throws ScriptException {
		log.trace("execute");
		if (scriptArgs.length == 0) {
			this.engine.eval(script.toString());
		} else {
			String s = String.format(script.toString(), scriptArgs);
			log.debug("Script: " + s);
			this.engine.eval(s);
		}
		log.trace("after execute");
	}
	
	public String getVariableValue(String varName) {
		log.trace("getVariableValue");
		return String.valueOf(this.engine.get(varName));
	}
	
	public String executeAndGetValue(CharSequence script, String outputVarName, Object...scriptArgs) throws ScriptException {
		log.trace("executeAndGetValue");
		this.execute(script, scriptArgs);
		return this.getVariableValue(outputVarName);
	}
	
	public String[] executeAndGetValues(CharSequence script, String...outputVarNames) throws ScriptException {
		log.trace("executeAndGetValues");
		this.execute(script);
		return (String[])Arrays.asList(outputVarNames).stream().map(v -> this.getVariableValue(v)).toArray();
	}

	private static String loadFileFromResource(String fileName) throws IOException {
		log.trace("loadFileFromResource");
		if (fileName == null)
			return null;
		if (!fileName.toLowerCase().endsWith(".js"))
			fileName += ".js";
		log.debug("Loading from resource file " + fileName);
		ClassLoader classLoader = KMCJavaScriptUtil.class.getClassLoader();
		StringWriter writer = new StringWriter();
		IOUtils.copy(classLoader.getResourceAsStream(fileName), writer, StandardCharsets.UTF_8);
		return writer.toString();
	}
}
