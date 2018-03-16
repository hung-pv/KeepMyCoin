package com.keepmycoin.console;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {

	private List<Option> options = new ArrayList<>();

	public void add(Option option) {
		this.options.add(option);
	}

	public void add(String displayText, String processMethod) {
		this.options.add(new Option(displayText, processMethod));
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
