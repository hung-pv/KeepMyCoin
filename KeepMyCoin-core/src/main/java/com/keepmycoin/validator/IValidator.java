package com.keepmycoin.validator;

import com.keepmycoin.utils.KMCInputUtil.IConvert;

public interface IValidator<TC> {
	String describleWhenInvalid();
	default boolean isValid(String input, IConvert<TC> converter) {
		return isValid(converter.convert(input));
	}
	boolean isValid(TC input);
}