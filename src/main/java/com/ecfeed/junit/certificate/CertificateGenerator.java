package com.ecfeed.junit.certificate;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.ecfeed.junit.runner.CryptographyHelper;

public final class CertificateGenerator {
	
	private final static String CRYPTO_PROVIDER = "BC";
	private final static String CRYPTO_ALGORITHM_KEY = "EC";
	private final static String CRYPTO_ALGORITHM_SIGNATURE = "SHA256withECDSA";
	private final static String CRYPTO_EC_NAMED_CURVE = "prime256v1";
	
	private final static String STORE_TYPE = "PKCS12";
	
	private final static String STORE_SERVER_PASSWORD = "changeit";
	private final static String STORE_SERVER_CONNECTION_ALIAS = "connection";
	private final static String STORE_SERVER_VALIDATION_ALIAS = "ca";
	
	private final static String STORE_CLIENT_PASSWORD = "changeit";
	private final static String STORE_CLIENT_CONNECTION_ALIAS = "connection";
	private final static String STORE_CLIENT_VALIDATION_ALIAS = "ca";
	
	private final static String CERTIFICATE_ISSUER = "CN=ecfeed.com, OU=EcFeed, O=EcFeed AS, L=Oslo, C=NO";
	private final static String CERTIFICATE_ISSUER_MAIL = "mail@ecfeed.com";
	private final static String CERTIFICATE_ISSUER_DOMAIN = "ecfeed.com";
	
	private static String fServerStorePath() {return ".ecFeed/keystore";}
	private static String fClientStorePath(String name) {return ".ecFeed/user/" + name + "/security";}
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static void generateServerStore() throws Exception {
		
		if (Files.exists(Paths.get(fServerStorePath()))) {
			throw new RuntimeException("The server keystore file already exists.");
		}
		
		certificateServerGenerate();
	}
	
	public static void generateClientStore(String... credentials) {
		
		if (Files.exists(Paths.get(fServerStorePath()))) {
			
			for (String account : credentials) {
				
				if (Files.exists(Paths.get(fClientStorePath(account)))) {
					throw new RuntimeException("The client keystore file already exists: " + account);
				}
				
				certificateClientGenerate(account);
			}
			
		} else {
			throw new RuntimeException("The server keystore file does not exists. Please create it first.");
		}
		
	}
	
	private static void certificateServerGenerate() {
		KeyPair keyPair = getKeyPair();
		
		JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
				new X500Name(CERTIFICATE_ISSUER), 
				new BigInteger("1"), 
				Date.from(ZonedDateTime.now().toInstant()), 
				Date.from(ZonedDateTime.now().plusYears(10).toInstant()), 
				new X500Name(CERTIFICATE_ISSUER), 
				keyPair.getPublic()
		);
		
		try {
			GeneralNamesBuilder generalNamesBuilder = new GeneralNamesBuilder();
			generalNamesBuilder.addName(new GeneralName(GeneralName.rfc822Name, CERTIFICATE_ISSUER_MAIL));
			generalNamesBuilder.addName(new GeneralName(GeneralName.dNSName, CERTIFICATE_ISSUER_DOMAIN));
			
			certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));
			certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true, new X509KeyUsage(X509KeyUsage.keyCertSign | X509KeyUsage.cRLSign | X509KeyUsage.keyAgreement | X509KeyUsage.keyEncipherment));
//			certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.18"), true, generalNamesBuilder.build());

			ContentSigner certificateContentSigner = new JcaContentSignerBuilder(CRYPTO_ALGORITHM_SIGNATURE).build(keyPair.getPrivate());
			Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(certificateContentSigner));
			
			certificateServerSave(certificate, keyPair.getPrivate());
		} catch (CertIOException | OperatorCreationException | CertificateException e) {
			throw new RuntimeException("The server certificate could not be created.", e);
		}
			
	}
	
	private static void certificateClientGenerate(String credentials) {
		
		KeyPair keyPair = CryptographyHelper.getKeyPair();
		
		JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
				new X500Name(CERTIFICATE_ISSUER), 
				new BigInteger("" + ThreadLocalRandom.current().nextLong()), 
				Date.from(ZonedDateTime.now().toInstant()), 
				Date.from(ZonedDateTime.now().plusYears(1).toInstant()), 
				new X500Name("CN=" + credentials), 
				keyPair.getPublic()
		);
		
		try {
			certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(false));
			certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true, new X509KeyUsage(X509KeyUsage.keyAgreement | X509KeyUsage.keyEncipherment | X509KeyUsage.digitalSignature));
			
			ContentSigner certificateContentSigner = new JcaContentSignerBuilder(CRYPTO_ALGORITHM_SIGNATURE).build(getServerPrivateKeyFromKeyStore());
			Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(certificateContentSigner));
			
			certificateClientKeySave(certificate, keyPair.getPrivate(), credentials);
		} catch (OperatorCreationException | CertificateException | IOException e) {
			throw new RuntimeException("The client certificate could not be created: " + credentials, e);
		}
	}
	
	private static void certificateServerSave(Certificate certificate, PrivateKey privateKey) {
		Path keyStorePath = Paths.get(fServerStorePath());
		
		try {
			Files.createDirectories(keyStorePath.getParent());
		} catch (IOException e) {
			throw new RuntimeException("The server key store path could not be created.", e);
		}
		
		try {
			KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
			char[] keyStorePassword = STORE_SERVER_PASSWORD.toCharArray();
			
			if (Files.exists(keyStorePath)) {
				keyStore.load(Files.newInputStream(keyStorePath), keyStorePassword);
			} else {
				keyStore.load(null, keyStorePassword);
			}
			
			keyStore.setKeyEntry(STORE_SERVER_CONNECTION_ALIAS, privateKey, STORE_SERVER_PASSWORD.toCharArray(), new Certificate[] {certificate});
			keyStore.setCertificateEntry(STORE_SERVER_VALIDATION_ALIAS, certificate);
			
			keyStore.store(Files.newOutputStream(keyStorePath), keyStorePassword);	
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new RuntimeException("The server key store could not be created.", e);
		}
		
	}
	
	private static void certificateClientKeySave(Certificate certificate, PrivateKey privateKey, String credentials) {
		Path keyStorePath = Paths.get(fClientStorePath(credentials));
		char[] keyStorePassword = STORE_CLIENT_PASSWORD.toCharArray();
		
		try {
			Files.createDirectories(keyStorePath.getParent());
		} catch (IOException e) {
			throw new RuntimeException("The client key store path could not be created: " + credentials, e);
		}
		
		try {
			KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
			
			if (Files.exists(keyStorePath)) {
				keyStore.load(Files.newInputStream(keyStorePath), keyStorePassword);
			} else {
				keyStore.load(null, keyStorePassword);
			}
			
			keyStore.setKeyEntry(STORE_CLIENT_CONNECTION_ALIAS, privateKey, STORE_CLIENT_PASSWORD.toCharArray(), new Certificate[] {certificate});
			keyStore.setCertificateEntry(STORE_CLIENT_VALIDATION_ALIAS, getServerCertificateFromKeyStore());
			
			keyStore.store(Files.newOutputStream(keyStorePath), keyStorePassword);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new RuntimeException("The client key store could not be created: " + credentials, e);
		}
			
	}
	
	private static KeyPair getKeyPair() {
		KeyPairGenerator keyPairGenerator = null;
		KeyPair keyPair = null;
		 
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM_KEY, CRYPTO_PROVIDER);
			keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec(CRYPTO_EC_NAMED_CURVE), new SecureRandom());
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException("The key pair could not be generated.");
		} 
		
		return keyPair;	
	}
	
	private static PrivateKey getServerPrivateKeyFromKeyStore()  {
		PrivateKey privateKey = null;

		try {
			Path keyStorePath = Paths.get(fServerStorePath());
			char[] keyStorePassword = STORE_SERVER_PASSWORD.toCharArray();
			
			KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
			keyStore.load(Files.newInputStream(keyStorePath), keyStorePassword);
			
			privateKey = (PrivateKey) keyStore.getKey(STORE_SERVER_CONNECTION_ALIAS, keyStorePassword);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			throw new RuntimeException("The CA private key could not be extracted.", e);
		}
		
		return privateKey;
	}
	
	private static Certificate getServerCertificateFromKeyStore()  {
		Certificate certificate = null;

		try {
			Path keyStorePath = Paths.get(fServerStorePath());
			char[] keyStorePassword = STORE_SERVER_PASSWORD.toCharArray();
			
			KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
			keyStore.load(Files.newInputStream(keyStorePath), keyStorePassword);
			
			certificate = keyStore.getCertificate(STORE_SERVER_VALIDATION_ALIAS);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new RuntimeException("The CA certificate could not be extracted.", e);
		}
		
		return certificate;
	}
	
	public static void main(String[] args) throws Exception {
		generateServerStore();
		generateClientStore("server", "runner", "rap");
	}
	
	
}
