package com.bootdo.signature.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Base64;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.springframework.data.util.Pair;

public class CAUtil {
	
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }
    
	public static Pair<String, String> genCSR(String subject, String alg,String provider)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException, OperatorCreationException, IOException {
        String signalg="";
        int alglength=0;
        String keyAlg="";
        if(alg.toUpperCase().equals("RSA1024")){
            signalg="SHA1WithRSA";
            alglength=1024;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("RSA2048")){
            signalg="SHA1WithRSA";
            alglength=2048;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("SM2")){
            signalg="SM3withSM2";
            alglength=256;
            keyAlg="SM2";
        }
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlg);
        keyGen.initialize(alglength);
        KeyPair kp = keyGen.generateKeyPair();
        PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(new X500Name(subject),SubjectPublicKeyInfo.getInstance(kp.getPublic().getEncoded()));
        JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(signalg);
        jcaContentSignerBuilder.setProvider(provider);
        ContentSigner contentSigner = jcaContentSignerBuilder.build(kp.getPrivate());
        PKCS10CertificationRequest csr = builder.build(contentSigner);
        String csrString = Base64.getEncoder().encodeToString(csr.getEncoded());
        csrString = toPEM(csrString, "-----BEGIN CERTIFICATE REQUEST-----", "-----END CERTIFICATE REQUEST-----");
        String privateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        privateKey = toPEM(privateKey, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        Pair<String, String> pair = Pair.of(privateKey, csrString);
        return pair;
    }
	
	private static String toPEM(String content,String prefix,String suffix) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append("\n");
        int csrlen = content.length() / 64 + 1;
        for(int i = 0; i < csrlen; i++) {
        	int startIndex = i * 64;
        	int endIndex = startIndex + 64;
        	if(endIndex > content.length() - 1) {
        		endIndex = content.length() - 1;
        	}
        	sb.append(content.substring(startIndex, endIndex));
        	sb.append("\n");
        }
        sb.append(suffix);
        sb.append("\n");
        return sb.toString();
	}
 
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, OperatorCreationException, IOException {
		Pair<String, String> xxx = genCSR("C=CN, ST=JX, L=GZ, O=JXUST, OU=XA, CN=last-player/emailAddress=838331634@qq.com", "RSA2048","BC");
		System.out.println(xxx);
	}
}
