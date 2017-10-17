package com.piggy.bank.piggybankgroovy;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public static String urlSafeEncode(byte[] bytes) {
		byte[] encodedBypts = Base64.encodeBase64(bytes);

		String s = new String(encodedBypts, UTF_8);
		s = s.replace('+', '-');
		s = s.replace('/', '_');
		s = s.replace("=", "");
		return s;
	}

	public static byte[] urlSafeDecode(String s) {
		s = s.replace('-', '+');
		s = s.replace('_', '/');
		switch (s.length() % 4) {
		case 0:
			break;
		case 2:
			s = s + "==";
			break;
		case 3:
			s = s + "=";
			break;
		case 1:
		default:
			throw new IllegalArgumentException("Illegal base64 url safe string!");
		}

		return Base64.decodeBase64(s.getBytes(UTF_8));
	}

	public static String encode(byte[] bytes) {
		byte[] b = Base64.encodeBase64(bytes);
		return new String(b, UTF_8);
	}

	public static byte[] decode(String s) {
		byte[] b = Base64.decodeBase64(s.getBytes(UTF_8));
		return b;
	}

}
