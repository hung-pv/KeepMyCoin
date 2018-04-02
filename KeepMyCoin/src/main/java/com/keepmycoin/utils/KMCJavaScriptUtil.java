package com.keepmycoin.utils;

import org.apache.commons.lang3.StringUtils;

public class KMCJavaScriptUtil {
	public static String buildJavaScriptArray(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		sb.append(StringUtils.join(KMCArrayUtil.unsignedBytes(buffer), ','));
		sb.append(" ]");
		return sb.toString();
	}
}
