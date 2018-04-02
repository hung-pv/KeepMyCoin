package com.keepmycoin.utils;

import java.io.Console;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.keepmycoin.TimeoutManager;
import com.keepmycoin.validator.IValidator;
import com.keepmycoin.validator.ValidateRegex;

public class KMCInputUtil {

	private static final Console csl = System.console();
	private static final Scanner in = new Scanner(System.in);

	public static String getRawInput(String ask) {
		TimeoutManager.renew();
		if (csl == null) {
			if (ask != null)
				o(ask);
			return StringUtils.trimToEmpty(in.nextLine());
		}
		return ask == null ? csl.readLine() : csl.readLine(ask);
	}

	public static String getInput(String ask, int maxLength) {
		TimeoutManager.renew();
		String input;
		if (csl != null) {
			input = ask == null ? csl.readLine() : csl.readLine(ask);
		} else {
			if (ask != null)
				o(ask);
			input = in.nextLine();
		}
		if (input != null) {
			input = input.trim();
			if (input.length() > maxLength) {
				input = null;
			}
		}
		return StringUtils.trimToNull(input);
	}

	public static boolean confirm(String msg) {
		o(msg);
		return "y".equalsIgnoreCase(getInput("Y/N: ", 1));
	}

	public static String getPassword(String msg) {
		String input;
		if (csl == null) {
			input = getRawInput(msg);
		} else {
			char[] cinput = msg == null ? csl.readPassword() : csl.readPassword(msg);
			if (cinput == null)
				return null;
			input = String.valueOf(cinput);
		}
		if (StringUtils.isBlank(input))
			return null;
		return input;
	}

	public static String getPassword_required(String msg, int min_length) {
		String pwd = getPassword(msg);
		while (true) {
			if (pwd == null) {
				o("Can not be empty");
				pwd = getPassword(msg == null ? "Again: " : msg);
				continue;
			}
			if (pwd.length() < min_length) {
				o("Minumum length required is %d chars", min_length);
				pwd = getPassword(msg == null ? "Again: " : msg);
				continue;
			}
			break;
		}
		if (pwd.length() > 16) {
			o("NOTICE: Your password is longer than 16 characters, will be cut off to the first 16 chars only");
			pwd = pwd.substring(0, 16);
		}
		return pwd;
	}

	/*-
	public static String getMnemonic(String msg) {
		String mnemonic = StringUtils.trimToNull(KMCInputUtil.getRawInput(null));
		if (mnemonic != null) {
			if (mnemonic.split("\\s").length % 12 != 0) {
				o("Mnemonic incorrect (can not devided by 12)");
				return null;
			}
			return mnemonic;
		}
		return null;
	}
	
	public static String getMnemonic_required(String msg) {
		String mnemonic = getMnemonic(msg);
		while (true) {
			if (mnemonic == null) {
				o("Can not be empty");
				mnemonic = getMnemonic(msg == null ? "Again: " : msg);
				continue;
			}
			break;
		}
		return mnemonic;
	}
	*/

	public static void requireConfirmation(String originalText) {
		while (true) {
			String confirm = getRawInput("Confirm: ");
			if (originalText == null && StringUtils.isEmpty(confirm))
				return;
			if (originalText.equals(confirm))
				return;
			o("Mismatch!");
		}
	}

	public static int getInt(String ask) {
		String input = getRawInput(ask);
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			o("NOT a valid number!");
			return getInt(ask == null ? "Again: " : ask);
		}
	}
	
	public static String getInput2faPrivateKey() {
		return  getInput("2fa private key", true, null, new ValidateRegex() {
			@Override
			public String describleWhenInvalid() {
				return "Alphabet and numeric only, more than 4 characters";
			}
			@Override
			public String getPattern() {
				return "^[aA-zZ0-9]{4,}$";
			}} , null);
	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static <RC> RC getInput(String name, boolean blankable, IConvert<RC> converter, IValidator<?>... validators) {
		// String regexPattern, String descripbleRegexPattern,
		String input;

		M: while (true) {
			input = getRawInput(null);
			if (StringUtils.isBlank(input) && blankable) {
				return null;
			}
			if (!blankable && StringUtils.isBlank(input)) {
				if (name == null) {
					o("Could not be empty, try again:");
				} else {
					o("%s could not be empty, try again:", name);
				}
				continue M;
			}
			V: for (IValidator iValidator : validators) {
				if (iValidator instanceof ValidateRegex || converter == null) {
					if (!iValidator.isValid(input)) {
						o(getMessageWhenInputInvalid(name, iValidator.describleWhenInvalid()));
						continue M;
					}
				} else {
					if (!iValidator.isValid(converter.convert(input))) {
						o(getMessageWhenInputInvalid(name, iValidator.describleWhenInvalid()));
						continue M;
					}
				}
			}
			break;
		}

		if (converter != null) {
			return converter.convert(input);
		}

		return (RC) input;
	}

	private static String getMessageWhenInputInvalid(String name, String describle) {

		StringBuilder sb = new StringBuilder();
		sb.append("Invalid format");
		if (name != null) {
			sb.append(" of ");
			sb.append(name);
		}
		if (describle == null) {
			sb.append(", please try again:");
		} else {
			sb.append(" !!!\n");
			sb.append(describle);
			sb.append("\\nPlease try again:");
		}
		return sb.toString();
	}

	private static void o(String pattern, Object... params) {
		System.out.println(String.format(pattern, params));
	}

	public static interface IConvert<TC> {
		TC convert(String input);
	}
}
