package com.ecfeed.junit.runner;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class CryptographyHelper {

	private final static String CRYPTO_PROVIDER = "BC";
	private final static String CRYPTO_ALGORITHM_KEY = "EC";
	private final static String CRYPTO_ALGORITHM_CIPHER = "ECIES";
	private final static String CRYPTO_EC_NAMED_CURVE = "prime256v1";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	private CryptographyHelper() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static byte[] getSecureRandomMessage(int length) {
		
		if (length < 1) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("cryptographyHelperMinimalLengthError"));
			Logger.exception(exception);
			throw exception;
		}
		
		byte[] plaintext = new byte[length];
		
		try {
			SecureRandom.getInstanceStrong().nextBytes(plaintext);
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("cryptographyHelperNoSuchAlgorithmSecureNumber"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		return plaintext;	
	}
	
	public static byte[] getRandomMessage(int length) {
		
		if (length < 1) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("cryptographyHelperMinimalLengthError"));
			Logger.exception(exception);
			throw exception;
		}
		
		byte[] plaintext = new byte[length];
		
		ThreadLocalRandom.current().nextBytes(plaintext);
		
		return plaintext;	
	}
	
	public static KeyPair getKeyPair() {
		KeyPairGenerator keyPairGenerator = null;
		KeyPair keyPair = null;
		 
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM_KEY, CRYPTO_PROVIDER);
			keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec(CRYPTO_EC_NAMED_CURVE), new SecureRandom());
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("cryptographyHelperKeyGenerationError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} 
		
		return keyPair;	
	}
	
	public static String getKeyFingerprint(Key key) {
		return DigestUtils.sha256Hex(key.getEncoded());
	}
	
	public static byte[] getByteArrayFromKey(Key key) {
		return key.getEncoded();
	}
	
	public static PublicKey getPublicKeyFromByteArray(byte[] keyArray) {	
		PublicKey publicKey = null;
		
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(CRYPTO_ALGORITHM_KEY, CRYPTO_PROVIDER);
			KeySpec keySpecification = new X509EncodedKeySpec(keyArray);
			publicKey = keyFactory.generatePublic(keySpecification);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("cryptographyHelperKeyRetrievalError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} 
		
		return publicKey;
	}
	
	public static byte[] encryptMessage(Key key, byte[] plaintext) {
		Cipher cipher = null;
		byte[] ciphertext = null;
		
		try {
			cipher = Cipher.getInstance(CRYPTO_ALGORITHM_CIPHER, CRYPTO_PROVIDER);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			ciphertext = cipher.doFinal(plaintext);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("cryptographyHelperMessageEncryptionError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		return ciphertext;
	}
	
	public static byte[] decryptMessage(Key key, byte[] ciphertext) {
		Cipher cipher = null;
		byte[] plaintext = null;

		try {
			cipher = Cipher.getInstance(CRYPTO_ALGORITHM_CIPHER, CRYPTO_PROVIDER);
			cipher.init(Cipher.DECRYPT_MODE, key);
			plaintext = cipher.doFinal(ciphertext);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("cryptographyHelperMessageDecryptionError"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		return plaintext;
	}
	
	public static void validateKeyComplementarity(PublicKey publicKey, PrivateKey privateKey, int strength) {
		byte[] plaintext = getRandomMessage(strength);
		
		decryptMessage(privateKey, encryptMessage(publicKey, plaintext));
	}
	
	public static int getMaxKeySize(String algorithm) throws NoSuchAlgorithmException {
		int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength(algorithm);
		return maxKeySize;
	}
	
}
