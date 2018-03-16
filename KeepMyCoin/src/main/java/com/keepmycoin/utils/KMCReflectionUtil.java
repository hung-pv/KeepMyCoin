package com.keepmycoin.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.keepmycoin.AbstractApplicationSkeleton;
import com.keepmycoin.IKeepMyCoin;

public class KMCReflectionUtil {
	public static <T extends IKeepMyCoin> Method getDeclaredMethod(Class<T> clz, String methodName)
			throws NoSuchMethodException {
		Method med;
		try {
			try {
				med = clz.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException e) {
				try {
					med = AbstractApplicationSkeleton.class.getDeclaredMethod(methodName);
				} catch (NoSuchMethodException e1) {
					try {
						med = IKeepMyCoin.class.getDeclaredMethod(methodName);
					} catch (NoSuchMethodException e2) {
						throw e2;
					}
				}
			}
			return med;
		} catch (Exception e) {
			if (e instanceof NoSuchMethodException) {
				throw (NoSuchMethodException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public static <A extends Annotation, T extends IKeepMyCoin> boolean isMethodHasAnnotation(Method med,
			Class<A> clzAnnotation, Class<T> clz) {
		if (med.getAnnotation(clzAnnotation) != null) {
			return true;
		}
		try {
			med = clz.getDeclaredMethod(med.getName());
			if (med.getAnnotation(clzAnnotation) != null) {
				return true;
			}
		} catch (NoSuchMethodException e) {
		}
		try {
			med = AbstractApplicationSkeleton.class.getDeclaredMethod(med.getName());
			if (med.getAnnotation(clzAnnotation) != null) {
				return true;
			}
		} catch (NoSuchMethodException e) {
		}
		try {
			med = IKeepMyCoin.class.getDeclaredMethod(med.getName());
			if (med.getAnnotation(clzAnnotation) != null) {
				return true;
			}
		} catch (NoSuchMethodException e) {
		}
		return false;
	}

	public static void invokeMethodBypassSecurity(Object instance, Method med) throws Exception {
		if (med.isAccessible())
			return;
		med.setAccessible(true);
		med.invoke(instance);
		med.setAccessible(false);
	}
}
