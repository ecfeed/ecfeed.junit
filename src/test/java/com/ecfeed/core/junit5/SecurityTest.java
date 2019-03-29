package com.ecfeed.core.junit5;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ecfeed.junit.runner.CryptographyHelper;
import com.ecfeed.core.webservice.client.SecurityHelper;

@Tag("Security")
@DisplayName("Security methods")
public class SecurityTest {
	
	@Test
	@DisplayName("Load default keystore")
	@Disabled("The default keystore could be unavailable during the test")
	void getDefaultKeyStoreTest() {
		SecurityHelper.getKeyStore();
	}
	
	@Test
	@DisplayName("Load keystore")
	void getKeyStoreTest() {
		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getKeyStore(keyStorePath);
	}

	@Test
	@DisplayName("Load erroneous keystore")
	void getErroneousKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("default");
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getKeyStore(keyStorePath),
				() -> "The path to the store is invalid, and therefore, the store should not be loaded");
		
	}
	
	@Test
	@DisplayName("Get certificate from store")
	void getCertificateFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getCertificate(keyStorePath, "ca");
	}
	
	@Test
	@DisplayName("Get invalid certificate from store")
	void getInvalidCertificateFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificate(keyStorePath, "default"),
				() -> "The certificate alias is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get certificate from file")
	void getCertificateFromFileTest() {
		SecurityHelper.getCertificateFromFile("src/test/resources/dummy.crt");
	}
	
	@Test
	@DisplayName("Get certificate from file (wrong file)")
	void getCertificateFromFileWrongFileTest() {

		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificateFromFile("src/test/resources/dummy.pub"),
				() -> "The certificate file is erroneous and therefore, it should not be retrieved");
		
		;
	}
	
	@Test
	@DisplayName("Get certificate from file (wrong path)")
	void getCertificateFromFileWrongPathTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificateFromFile("src/test/resources/default"),
				() -> "The certificate path is erroneous and therefore, it should not be retrieved");

	}
	
	@Test
	@DisplayName("Get public key from store")
	void getPublicKeyFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getPublicKey(keyStorePath, "connection");
	}
	
	@Test
	@DisplayName("Get invalid public key from store")
	void getInvalidPublicKeyFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getKeyStore(keyStorePath);

		Optional<String> keyStorePathEmpty = Optional.of("");
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKey(keyStorePathEmpty, "default"),
				() -> "The public key alias is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get public key from OpenSSL file")
	void getPublicKeyFromOpenSSLFileTest() {
		SecurityHelper.getPublicKeyFromFileOpenSSL("src/test/resources/dummy.pub");
	}
	
	@Test
	@DisplayName("Get public key from OpenSSL file (wrong file)")
	void getPublicKeyFromOpenSSLFileWrongFileTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKeyFromFileOpenSSL("testResources/dummy.crt"),
				() -> "The OpenSSL public key file is erroneous and therefore, it should not be retrieved");
		
		;
	}
	
	@Test
	@DisplayName("Get public key from OpenSSL file (wrong path)")
	void getPublicKeyFromOpenSSLFileWrongPathTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKeyFromFileOpenSSL("testResources/default"),
				() -> "The OpenSSL public key file  is erroneous and therefore, it should not be retrieved");

	}
	
	@Test
	@DisplayName("Get private key from store")
	void getPrivateKeyFromKeyStoreTest() {
		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getKeyStore(keyStorePath);
		SecurityHelper.getPrivateKey("connection", "changeit");	
	}
	
	@Test
	@DisplayName("Get invalid private key from store (alias)")
	void getInvalidAliasPrivateKeyFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getKeyStore(keyStorePath);
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPrivateKey("default", "changeit"),
				() -> "The private key alias is invalid, and therefore, it should not be retrieved");
		
	}
	 
	@Test
	@DisplayName("Get invalid private key from store (password)")
	void getInvalidPasswordPrivateKeyFromKeyStoreTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		SecurityHelper.getKeyStore(keyStorePath);
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPrivateKey("client", "default"),
				() -> "The private key password is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get private key from PKCS8 file")
	void getPrivateKeyFromPKCS8FileTest() {
		SecurityHelper.getPrivateKeyFromFilePKCS8("src/test/resources/dummy.pkcs8");
	}
	
	@Test
	@DisplayName("Get public key from OpenSSL file (wrong file)")
	void getPrivateKeyFromOPKCS8FileWrongFileTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKeyFromFileOpenSSL("testResources/dummy.crt"),
				() -> "The PKCS8 private key file is erroneous and therefore, it should not be retrieved");
		
		;
	}
	
	@Test
	@DisplayName("Get private key from PKCS file (wrong path)")
	void getPrivateKeyFromPKCS8FileWrongPathTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKeyFromFileOpenSSL("src/test/resources/default"),
				() -> "The PKCS8 private key file is erroneous and therefore, it should not be retrieved");

	}
	
	@Test
	@DisplayName("Get and validate key pair from store")
	void getKeyPairFromKeyStoreAndValidateThemTest() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		PublicKey publicKey = SecurityHelper.getPublicKey(keyStorePath, "connection");
		PrivateKey privateKey = SecurityHelper.getPrivateKey("connection", "changeit");
		 
		try {
			CryptographyHelper.validateKeyComplementarity(publicKey, privateKey, 200);
		} catch (Exception e) {
			fail("Failed to validate the key pair");
		}
	}
	
	@Test
	@DisplayName("Validate certificate")
	void getAndValidateCertificate() {

		Optional<String> keyStorePath = Optional.of("src/test/resources/security");
		Certificate certificate = SecurityHelper.getCertificate(keyStorePath, "ca");
		PublicKey publicKey = SecurityHelper.getPublicKey(keyStorePath, "ca");
		
		try {
			certificate.verify(publicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
			fail("Failed to validate the certificate");
		}
	}
	
}
