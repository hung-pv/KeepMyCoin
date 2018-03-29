package com.keepmycoin;

import javax.script.ScriptEngine;

import com.keepmycoin.utils.KMCJavaScriptUtil;

public class JavaScript {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JavaScript.class);
	
	public static final ScriptEngine ENGINE_AES;
	public static final ScriptEngine ENGINE_MEW;
	
	static {
		log.info("Setting up Script Engines");
		ScriptEngine engine = null;
		try {
			engine = KMCJavaScriptUtil.setupNewScriptEngine("aes256-engine", "aes256-ext");
		} catch (Exception e) {
			log.fatal("Unable to load JS engine for AES", e);
			System.exit(1);
		}
		ENGINE_AES = engine;
		engine = null;
		try {
			engine = KMCJavaScriptUtil.setupNewScriptEngine("etherwallet-master");
		} catch (Exception e) {
			log.fatal("Unable to load JS engine for AES", e);
			System.exit(1);
		}
		ENGINE_MEW = engine;
		log.info("Script Engines had been setup successfully");
	}
	
	public static void initialize() {
	}
}
