package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ecfeed.junit.runner.CryptographyHelper;

@Tag("Cryptography")
@DisplayName("Cryptography methods")
public class CryptographyTest {

	@Test
	@DisplayName("Generate secure message")
	@Disabled("The test is incredibly resource and time consming (even for a single byte)")
	void generateSecureMessage() {		
		System.out.println(CryptographyHelper.getSecureRandomMessage(1));
	}
	
	@Test
	@DisplayName("Too short random message")
	void generateTooShortRandomMessageTest() {
		
		assertThrows(IllegalArgumentException.class,
				() -> CryptographyHelper.getRandomMessage(0),
				() -> "A message shorter than one should not be generated");
		
	}
	
	@Test
	@DisplayName("Compare random messages")
	void compareRandomMessagesTest() {
		byte[] message1 = CryptographyHelper.getRandomMessage(2048);
		byte[] message2 = CryptographyHelper.getRandomMessage(2048);
		
		assertFalse(Arrays.equals(message1, message2),
				() -> "Generating identical messages is possible but highly improbable");
		
	}
	
	@Test
	@DisplayName("Compare key pairs")
	void compareKeyPairsTest() {
		KeyPair keyPair1 = CryptographyHelper.getKeyPair();
		KeyPair keyPair2 = CryptographyHelper.getKeyPair();
		
		assertAll("Compound",
				() -> {
					assertFalse(Arrays.equals(CryptographyHelper.getByteArrayFromKey(keyPair1.getPublic()), CryptographyHelper.getByteArrayFromKey(keyPair2.getPublic())),
							() -> "Generating identical public keys is possible but highly improbable");
				},
				() -> {
					assertFalse(Arrays.equals(CryptographyHelper.getByteArrayFromKey(keyPair1.getPrivate()), CryptographyHelper.getByteArrayFromKey(keyPair2.getPrivate())),
							() -> "Generating identical private keys is possible but highly improbable");
				}
		);
		
	}
	
	@Test
	@DisplayName("Tranfer key using TLS")
	void keyTransferTest() {
		KeyPair keyPair = CryptographyHelper.getKeyPair();
		
		PublicKey publicKeySent = keyPair.getPublic();	
		byte[] publicKeyArray = CryptographyHelper.getByteArrayFromKey(publicKeySent);
		PublicKey publicKeyReceived = CryptographyHelper.getPublicKeyFromByteArray(publicKeyArray);
		
		assertTrue(Arrays.equals(CryptographyHelper.getByteArrayFromKey(publicKeySent), CryptographyHelper.getByteArrayFromKey(publicKeyReceived)),
				() -> "The transferred public key must be the same as the received key");
		
	}
	
	@Test
	@DisplayName("Transfer message using TLS")
	void messageTransferTest() {
		KeyPair keyPair = CryptographyHelper.getKeyPair();
		
		byte[] plaintextSent = CryptographyHelper.getRandomMessage(200);
		byte[] plaintextReceived = null;
		byte[] ciphertext = null;
		
		try {
			ciphertext = CryptographyHelper.encryptMessage(keyPair.getPublic(), plaintextSent);
		} catch (Exception e) {
			fail(() -> "The text could not be encrypted");
		}
		
		try {
			plaintextReceived = CryptographyHelper.decryptMessage(keyPair.getPrivate(), ciphertext);
		} catch (Exception e) {
			fail(() -> "The text could not be decrypted");
		}
		
		assertFalse(Arrays.equals(plaintextSent, ciphertext),
				() -> "The plaintext must be different than ciphertext");
		
		assertTrue(Arrays.equals(plaintextSent, plaintextReceived),
				() -> "The sent plaintext must be the same as the received plaintext");
		
	}
	
	@Test
	@DisplayName("Validate key (correct)")
	void validateCorrectKeyTest() {
		KeyPair keyPair = CryptographyHelper.getKeyPair();
		
		try {
			CryptographyHelper.validateKeyComplementarity(keyPair.getPublic(), keyPair.getPrivate(), 200);
		} catch (Exception e) {
			fail(() -> "The key pair could not be validated");
		}
		
	}
	
	@Test
	@DisplayName("Validate key (incorrect)")
	void validateIncorrectKeyTest() {
		KeyPair keyPair1 = CryptographyHelper.getKeyPair();
		KeyPair keyPair2 = CryptographyHelper.getKeyPair();
		
		assertThrows(Exception.class, 
				() -> {
					CryptographyHelper.validateKeyComplementarity(keyPair1.getPublic(), keyPair2.getPrivate(), 200);
				},
				() -> "The key pair is incorrect, and therefore, should not be validated"
		);
		
	}
	
	@Test
	@DisplayName("Compare fingerprints")
	void checkFingerprintTest() {
		KeyPair keyPair = CryptographyHelper.getKeyPair();
		
		String publicKeyHash = CryptographyHelper.getKeyFingerprint(keyPair.getPublic());
		String privateKeyHash = CryptographyHelper.getKeyFingerprint(keyPair.getPrivate());
		
		assertFalse(publicKeyHash.equals(privateKeyHash),
				() -> "The public key fingerprint and the private key fingerprint should differ");
		
	}
	
}
