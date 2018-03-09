package com.keepmycoin.console.menu;

import java.lang.reflect.Method;

public class Option {
	
	public static Class<?> clzMainConsole;

	private String displayText;
	private String processMethod;
	private boolean requiredKeystore;

	public Option(String displayText, String processMethod) {
		this.displayText = displayText;
		this.processMethod = processMethod;
	}
	
	public Option(String displayText, String processMethod, boolean requiredKeystore) {
		this.displayText = displayText;
		this.processMethod = processMethod;
		this.requiredKeystore = requiredKeystore;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public boolean isRequireKeystore() {
		return this.requiredKeystore;
	}

	public void processMethod() {
		try {
			if (this.requiredKeystore) {
				Method medLoadKeystore = clzMainConsole.getDeclaredMethod("loadKeystore");
				medLoadKeystore.setAccessible(true);
				medLoadKeystore.invoke(null);
			}
			
			Method med = clzMainConsole.getDeclaredMethod(this.processMethod);
			med.setAccessible(true);
			med.invoke(null);
		} catch (Exception e) {
			if (e instanceof NoSuchMethodException) {
				System.err.println("Under construction !!!");
			} else {
				throw new RuntimeException(e);
			}
		}
	}
}
