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
import java.util.ArrayList;
import java.util.List;

import com.keepmycoin.IKeepMyCoin;
import com.keepmycoin.KeepMyCoinConsole;
import com.keepmycoin.annotation.Continue;
import com.keepmycoin.annotation.RequiredKeystore;
import com.keepmycoin.utils.KMCReflectionUtil;

public class MenuManager {
	
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MenuManager.class);

	private List<Option> options = new ArrayList<>();
	private KeepMyCoinConsole consoleInstance;

	public MenuManager(KeepMyCoinConsole consoleInstance) {
		this.consoleInstance = consoleInstance;
	}

	public void add(Option option) {
		this.options.add(option);
	}

	public void add(CharSequence displayText, String processMethod, Object...args) {
		this.add(new Option(displayText, processMethod, args));
	}

	public void showOptionList(String header) {
		if (header != null)
			System.out.println(header);
		for (int i = 0; i < this.options.size(); i++) {
			System.out.println(
					String.format("%s%d. %s", header != null ? " " : "", i + 1, this.options.get(i).getDisplayText()));
		}
	}

	public Option getOptionBySelection(int selected) {
		return this.options.get(selected - 1);
	}

	public int countMenus() {
		return this.options.size();
	}

	public <T extends IKeepMyCoin> void processSelectedOption(int selected) throws Exception {
		this.processSelectedOption(this.getOptionBySelection(selected));
	}

	public <T extends IKeepMyCoin> void processSelectedOption(Option option) throws Exception {
		if (option == null || option.getProcessMethod() == null || this.consoleInstance == null) return;
		try {
			Method med = KMCReflectionUtil.getDeclaredMethod(this.consoleInstance.getClass(), option.getProcessMethod(), option.getMethodParameterTypes());

			if (KMCReflectionUtil.isMethodHasAnnotation(med, RequiredKeystore.class, this.consoleInstance.getClass())) {
				Method medLoadKeystore = KMCReflectionUtil.getDeclaredMethod(this.consoleInstance.getClass(), "loadKeystore");
				KMCReflectionUtil.invokeMethodBypassSecurity(this.consoleInstance, medLoadKeystore);
			}

			KMCReflectionUtil.invokeMethodBypassSecurity(this.consoleInstance, med, option.getMethodArgs());

			if (this.consoleInstance instanceof KeepMyCoinConsole) {
				if (KMCReflectionUtil.isMethodHasAnnotation(med, Continue.class, this.consoleInstance.getClass())) {
					Method medLaunchMenu = KMCReflectionUtil.getDeclaredMethod(this.consoleInstance.getClass(), "launchMenu");
					KMCReflectionUtil.invokeMethodBypassSecurity(this.consoleInstance, medLaunchMenu);
				}
			}
		} catch (Exception e) {
			if (e instanceof NoSuchMethodException) {
				log.fatal("NoSuchMethodException", e);
				System.err.println("Under construction !!!");
			} else {
				Throwable caused = e.getCause();
				if (caused instanceof InvocationTargetException) {
					log.fatal("Unhandled exception", caused.getCause());
				} else {
					log.fatal("Unhandled exception", e);
				}
				System.exit(1);
			}
		}
	}
}
