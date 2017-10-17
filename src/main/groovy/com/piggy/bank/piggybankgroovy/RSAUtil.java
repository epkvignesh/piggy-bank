package com.piggy.bank.piggybankgroovy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAUtil {

	/**
	 * Return the kid from the provided public key
	 * 
	 * @param publicKey
	 * @return the kid from the key
	 */
	public static String getPublicKeyKid(PublicKey publicKey) {
		try {
			byte[] pubKeyBytes = publicKey.getEncoded();

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashOfPubKeyBytes = digest.digest(pubKeyBytes);

			return Base64Util.urlSafeEncode(hashOfPubKeyBytes);
		} catch (NoSuchAlgorithmException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}

	/**
	 * Return the kid from the provided public key
	 * 
	 * @param privateKey
	 * @return the kid from the key
	 */
	public static String getPrivateKeyKid(PrivateKey privateKey) {
		try {
			byte[] pubKeyBytes = privateKey.getEncoded();

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashOfPubKeyBytes = digest.digest(pubKeyBytes);

			return Base64Util.urlSafeEncode(hashOfPubKeyBytes);
		} catch (NoSuchAlgorithmException ex) {
			System.err.println(ex.getMessage());
		}
		return null;
	}
}
