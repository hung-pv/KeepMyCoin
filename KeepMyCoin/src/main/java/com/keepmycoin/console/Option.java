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
package com.keepmycoin.console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.keepmycoin.IKeepMyCoin;
import com.keepmycoin.KeepMyCoinConsole;
import com.keepmycoin.annotation.Continue;
import com.keepmycoin.annotation.RequiredKeystore;
import com.keepmycoin.utils.KMCReflectionUtil;

public class Option {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Option.class);

	private String displayText;
	private String processMethod;
	private Object[] methodArgs;
	private Class<?>[] methodParameterTypes;

	public Option(CharSequence displayText, String processMethod, Object...methodArgs) {
		this.displayText = displayText.toString();
		this.processMethod = processMethod;
		
		if (Arrays.asList(methodArgs).stream().allMatch(a -> a == null)) {
			methodArgs = new Object[0];
		}
		this.methodArgs = methodArgs;
		this.methodParameterTypes = new Class<?>[this.methodArgs.length];
		for(int i = 0; i < this.methodArgs.length; i++) {
			this.methodParameterTypes[i] = this.methodArgs[i] == null ? null : this.methodArgs[i].getClass();
		}
	}
	
	public Option(String displayText, String processMethod) {
		this(displayText, processMethod, new Object[0]);
	}

	public String getDisplayText() {
		return displayText;
	}

	public <T extends IKeepMyCoin> void processMethod(IKeepMyCoin instance) throws Exception {
		if (this.processMethod == null) return;
		try {
			Method med = KMCReflectionUtil.getDeclaredMethod(instance.getClass(), this.processMethod, this.methodParameterTypes);

			if (KMCReflectionUtil.isMethodHasAnnotation(med, RequiredKeystore.class, instance.getClass())) {
				Method medLoadKeystore = KMCReflectionUtil.getDeclaredMethod(instance.getClass(), "loadKeystore");
				KMCReflectionUtil.invokeMethodBypassSecurity(instance, medLoadKeystore);
			}

			KMCReflectionUtil.invokeMethodBypassSecurity(instance, med, this.methodArgs);

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
				Throwable caused = e.getCause();
				if (caused instanceof InvocationTargetException) {
					throw (Exception) caused.getCause();
				} else {
					throw e;
				}
			}
		}
	}
}
