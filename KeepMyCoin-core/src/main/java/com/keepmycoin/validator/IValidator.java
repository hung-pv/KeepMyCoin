package com.keepmycoin.validator;

public interface IValidator<TC> {
	String describleWhenInvalid();
	boolean isValid(TC input);
}