package com.keepmycoin.validator;

public class ValidateNumberNotNegative<T extends Number> extends ValidateNormal<T> {

	@Override
	public String describleWhenInvalid() {
		return "Must not be negative";
	}

	@Override
	public boolean isValid(Number num) {
		if (num == null) return false;
		if (num instanceof Double) {
			Double d = (Double)num;
			return !d.isNaN() && !d.isInfinite() && d.doubleValue() >= 0.D;
		} else if (num instanceof Long) {
			return num.longValue() >= 0;
		} else if (num instanceof Integer) {
			return num.intValue() >= 0;
		} else if (num instanceof Byte) {
			return num.byteValue() >= 0;
		} else if (num instanceof Float) {
			return num.floatValue() >= 0;
		} else {
			return false;
		}
	}

}
