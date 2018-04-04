package com.keepmycoin.test;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.junit.Test;

import com.keepmycoin.js.JavaScript;
import com.keepmycoin.utils.KMCNumberUtil;
import com.keepmycoin.utils.KMCStringUtil;
import com.keepmycoin.validator.ValidateMnemonic;

public class CoreAndUtils {
	@Test
	public void checkLoadJS() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
			@Override
			public String get() {
				JavaScript.initialize();
				return null;
			}
		});
		assertFalse(future.isDone());
		boolean done = false;
		for(int c = 1; c <=60; c++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			done = future.isDone();
			if (done) {
				break;
			}
		}
		assertTrue(done);
		if (!done) {
			future.cancel(true);
		}
	}

	@Test
	public void strUtils_beautiNumber() {
		single_strUtils_beautiNumber("", "");
		single_strUtils_beautiNumber("1", "1");
		single_strUtils_beautiNumber("1.0", "1.0");
		single_strUtils_beautiNumber("0.1", "0.1");
		single_strUtils_beautiNumber("0.1", ".1");
		single_strUtils_beautiNumber("100", "100");
		single_strUtils_beautiNumber("100.0", "100.0");
		single_strUtils_beautiNumber("1,000", "1000");
		single_strUtils_beautiNumber("1,000.0", "1000.0");
		single_strUtils_beautiNumber("1,000.0", "1,000.0");
		single_strUtils_beautiNumber("1,000.0000", "1,000.0000");
	}
	
	private void single_strUtils_beautiNumber(String expected, String num) {
		assertEquals(expected, KMCStringUtil.beautiNumber(num));
	}
	
	@Test
	public void number_shiftTheDot() {
		assertEquals("18000000000000000000", KMCNumberUtil.shiftTheDot("18.0", 18));
		assertEquals("18,000,000,000,000,000,000", KMCNumberUtil.shiftTheDot("18.0", 18, true));
		assertEquals("18000000000000000000", KMCNumberUtil.shiftTheDot("18.", 18));
		assertEquals("18000000000000000000", KMCNumberUtil.shiftTheDot("18", 18));
		assertEquals("1800000000000000000", KMCNumberUtil.shiftTheDot("1.8", 18));
		assertEquals("800000000000000000", KMCNumberUtil.shiftTheDot("0.8", 18));
		boolean err = false;
		try {
			KMCNumberUtil.shiftTheDot(".8", 18);
		} catch (Exception e) {
			err = true;
		}
		assertTrue(err);
		
		assertEquals("18",	KMCNumberUtil.shiftTheDot("18000000000000000000", -18));
		assertEquals("1.8",	KMCNumberUtil.shiftTheDot( "1800000000000000000", -18));
		assertEquals("0.8",	KMCNumberUtil.shiftTheDot(  "800000000000000000", -18));
		
		assertEquals("18",	KMCNumberUtil.shiftTheDot("18000000000000000000.0", -18));
		assertEquals("1.8",	KMCNumberUtil.shiftTheDot( "1800000000000000000.0", -18));
		assertEquals("0.8",	KMCNumberUtil.shiftTheDot(  "800000000000000000.0", -18));

		assertEquals("18",	KMCNumberUtil.shiftTheDot("18000000000000000000.0000", -18));
		assertEquals("1.8",	KMCNumberUtil.shiftTheDot( "1800000000000000000.0000", -18));
		assertEquals("0.8",	KMCNumberUtil.shiftTheDot(  "800000000000000000.0000", -18));
		
		assertEquals("18",	KMCNumberUtil.shiftTheDot("180000", -4));
		assertEquals("1.8",	KMCNumberUtil.shiftTheDot( "18000", -4));
		assertEquals("0.8",	KMCNumberUtil.shiftTheDot(  "8000", -4));

		assertEquals("18.0000505",	KMCNumberUtil.shiftTheDot("180000.505", -4));
		assertEquals("1,800.0000505",	KMCNumberUtil.shiftTheDot("18000000.505", -4, true));
		assertEquals("18.0000505",	KMCNumberUtil.shiftTheDot("180,000.505", -4));
		assertEquals("1.8000505",	KMCNumberUtil.shiftTheDot("18000.505", -4));
		assertEquals("0.8000505",	KMCNumberUtil.shiftTheDot("8000.505", -4));
		assertEquals("0.8000505",	KMCNumberUtil.shiftTheDot("8,000.505", -4));
		assertEquals("0.0000005",	KMCNumberUtil.shiftTheDot("0.005", -4));
	}
	
	@Test
	public void number_convertBigIntegerToHex() {
		single_number_convertBigIntegerToHex("100", "256");
		single_number_convertBigIntegerToHex("3d", "61");
		single_number_convertBigIntegerToHex("5208", "21000");
		single_number_convertBigIntegerToHex("12d5084f70348000", KMCNumberUtil.shiftTheDot("1.357", 18));
	}
	
	private void single_number_convertBigIntegerToHex(String expected, String num) {
		BigInteger bi1 = new BigInteger(expected, 16);
		BigInteger bi2 = new BigInteger(KMCNumberUtil.convertBigIntegerToHex(new BigInteger(num, 10)), 16);
		assertEquals(bi1,  bi2);
	}
	
	@Test
	public void validate_mnemonic() {
		assertTrue(new ValidateMnemonic().isValid(JsEngine.MNEMONIC));
	}
}
