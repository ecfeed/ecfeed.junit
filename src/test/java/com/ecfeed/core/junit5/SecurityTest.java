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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ecfeed.junit.runner.CryptographyHelper;
import com.ecfeed.junit.runner.SecurityHelper;

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
		SecurityHelper.getKeyStore("testResources/security");
	}

	@Test
	@DisplayName("Load erroneous keystore")
	void getErroneousKeyStoreTest() {
		
		assertThrows(IllegalArgumentException.class, 
				() -> SecurityHelper.getKeyStore("default"),
				() -> "The path to the store is invalid, and therefore, the store should not be loaded");
		
	}
	
	@Test
	@DisplayName("Get certificate from store")
	void getCertificateFromKeyStoreTest() {
		SecurityHelper.getCertificate("testResources/security", "ca");	
	}
	
	@Test
	@DisplayName("Get invalid certificate from store")
	void getInvalidCertificateFromKeyStoreTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificate("testResources/security", "default"),
				() -> "The certificate alias is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get certificate from file")
	void getCertificateFromFileTest() {
		SecurityHelper.getCertificateFromFile("testResources/dummy.crt");
	}
	
	@Test
	@DisplayName("Get certificate from file (wrong file)")
	void getCertificateFromFileWrongFileTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificateFromFile("testResources/dummy.pub"),
				() -> "The certificate file is erroneous and therefore, it should not be retrieved");
		
		;
	}
	
	@Test
	@DisplayName("Get certificate from file (wrong path)")
	void getCertificateFromFileWrongPathTest() {
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getCertificateFromFile("testResources/default"),
				() -> "The certificate path is erroneous and therefore, it should not be retrieved");

	}
	
	@Test
	@DisplayName("Get public key from store")
	void getPublicKeyFromKeyStoreTest() {
		SecurityHelper.getPublicKey("testResources/security", "connection");	
	}
	
	@Test
	@DisplayName("Get invalid public key from store")
	void getInvalidPublicKeyFromKeyStoreTest() {
		SecurityHelper.getKeyStore("testResources/security");
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPublicKey("", "default"),
				() -> "The public key alias is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get public key from OpenSSL file")
	void getPublicKeyFromOpenSSLFileTest() {
		SecurityHelper.getPublicKeyFromFileOpenSSL("testResources/dummy.pub");
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
		SecurityHelper.getKeyStore("testResources/security");
		SecurityHelper.getPrivateKey("connection", "changeit");	
	}
	
	@Test
	@DisplayName("Get invalid private key from store (alias)")
	void getInvalidAliasPrivateKeyFromKeyStoreTest() {
		SecurityHelper.getKeyStore("testResources/security");
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPrivateKey("default", "changeit"),
				() -> "The private key alias is invalid, and therefore, it should not be retrieved");
		
	}
	 
	@Test
	@DisplayName("Get invalid private key from store (password)")
	void getInvalidPasswordPrivateKeyFromKeyStoreTest() {
		SecurityHelper.getKeyStore("testResources/security");
		
		assertThrows(RuntimeException.class,
				() -> SecurityHelper.getPrivateKey("client", "default"),
				() -> "The private key password is invalid, and therefore, it should not be retrieved");
		
	}
	
	@Test
	@DisplayName("Get private key from PKCS8 file")
	void getPrivateKeyFromPKCS8FileTest() {
		SecurityHelper.getPrivateKeyFromFilePKCS8("testResources/dummy.pkcs8");
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
				() -> SecurityHelper.getPublicKeyFromFileOpenSSL("testResources/default"),
				() -> "The PKCS8 private key file is erroneous and therefore, it should not be retrieved");

	}
	
	@Test
	@DisplayName("Get and validate key pair from store")
	void getKeyPairFromKeyStoreAndValidateThemTest() {

		PublicKey publicKey = SecurityHelper.getPublicKey("testResources/security", "connection");
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
		
		Certificate certificate = SecurityHelper.getCertificate("testResources/security", "ca");	
		PublicKey publicKey = SecurityHelper.getPublicKey("testResources/security", "ca");
		
		try {
			certificate.verify(publicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
			fail("Failed to validate the certificate");
		}
	}
	
}
