package com.keepmycoin.console;

import java.lang.reflect.Method;

import com.keepmycoin.IKeepMyCoin;
import com.keepmycoin.KeepMyCoinConsole;
import com.keepmycoin.annotation.Continue;
import com.keepmycoin.annotation.RequiredKeystore;
import com.keepmycoin.utils.KMCReflectionUtil;

public class Option {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Option.class);

	private String displayText;
	private String processMethod;

	public Option(String displayText, String processMethod) {
		this.displayText = displayText;
		this.processMethod = processMethod;
	}

	public String getDisplayText() {
		return displayText;
	}

	public <T extends IKeepMyCoin> void processMethod(IKeepMyCoin instance) {
		try {
			Method med = KMCReflectionUtil.getDeclaredMethod(instance.getClass(), this.processMethod);

			if (KMCReflectionUtil.isMethodHasAnnotation(med, RequiredKeystore.class, instance.getClass())) {
				Method medLoadKeystore = KMCReflectionUtil.getDeclaredMethod(instance.getClass(), "loadKeystore");
				KMCReflectionUtil.invokeMethodBypassSecurity(instance, medLoadKeystore);
			}

			med.invoke(instance);

			if (instance instanceof KeepMyCoinConsole) {
				if (KMCReflectionUtil.isMethodHasAnnotation(med, Continue.class, instance.getClass())) {
					Method medLaunchMenu = KMCReflectionUtil.getDeclaredMethod(instance.getClass(), "launchMenu");
					KMCReflectionUtil.invokeMethodBypassSecurity(instance, medLaunchMenu);
				}
			}
		} catch (Exception e) {
			if (e instanceof NoSuchMethodException) {
				log.fatal("NoSuchMethodException", e);
				System.err.println("Under construction !!!");
			} else {
				throw new RuntimeException(e);
			}
		}
	}
}
