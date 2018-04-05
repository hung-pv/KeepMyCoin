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

import java.util.Arrays;

public class Option {

	@SuppressWarnings("unused")
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

	public String getDisplayText() {
		return displayText;
	}
	
	public String getProcessMethod() {
		return processMethod;
	}

	public Object[] getMethodArgs() {
		return methodArgs;
	}

	public Class<?>[] getMethodParameterTypes() {
		return methodParameterTypes;
	}
}
