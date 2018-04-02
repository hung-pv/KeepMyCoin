package com.keepmycoin.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.ArrayUtils;

import com.keepmycoin.AbstractApplicationSkeleton;
import com.keepmycoin.IKeepMyCoin;

public class KMCReflectionUtil {
	public static <T extends IKeepMyCoin> Method getDeclaredMethod(Class<T> clz, String methodName, Class<?>...parameterTypes)
			throws NoSuchMethodException {
		Method med;
		try {
			med = getDeclaredMethodOrDefault(clz, methodName, parameterTypes);
			if (med == null) {
				med = getDeclaredMethodOrDefault(AbstractApplicationSkeleton.class, methodName, parameterTypes);
				if (med == null) {
					med = getDeclaredMethodOrDefault(IKeepMyCoin.class, methodName, parameterTypes);
					if (med == null) {
						clz.getDeclaredMethod(methodName, parameterTypes); // throw NoSuchMethodException
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

	private static Method getDeclaredMethodOrDefault(Class<?> clz, String methodName, Class<?>...parameterTypes) {
		try {
			return clz.getDeclaredMethod(methodName, parameterTypes);
		} catch (Throwable t) {
			return findAnyMethodByName(clz, methodName);
		}
	}
	
	private static Method findAnyMethodByName(Class<?> clz, String methodName) {
		try {
			return Arrays.asList(clz.getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName)).sorted(new Comparator<Method>() {
				@Override
				public int compare(Method o1, Method o2) {
					return o1.getParameterCount() - o2.getParameterCount();
				}
			}).findFirst().orElse(null);
		} catch (Throwable t) {
			return null;
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

	public static void invokeMethodBypassSecurity(Object instance, Method med, Object...methodArgs) throws Exception {
		if (med.isAccessible())
			return;
		med.setAccessible(true);
		while (med.getParameterCount() > methodArgs.length) {
			methodArgs = ArrayUtils.add(methodArgs, null);
		}
		med.invoke(instance, methodArgs);
		med.setAccessible(false);
	}
}
