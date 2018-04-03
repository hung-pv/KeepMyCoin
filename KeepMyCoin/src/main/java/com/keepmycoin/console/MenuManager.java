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

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

	private List<Option> options = new ArrayList<>();

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
}
