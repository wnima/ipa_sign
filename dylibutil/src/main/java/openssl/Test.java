package openssl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAbsentContent;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.PKCS12Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class Test {
	static final String KEYSTORE_FILE = "E:/signature/publicCertificate.crt";
	static final String KEYSTORE_INSTANCE = "PKCS12";
	static final String KEYSTORE_PWD = "";
	static final String KEYSTORE_ALIAS = "1";

	public static String getPrivateKeyInfo() {
		String privKeyFileString = "E:/signature/publicCertificate.crt";
		String privKeyPswdString = "";
		String keyAlias = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			FileInputStream fileInputStream = new FileInputStream(privKeyFileString);
			char[] nPassword = null;
			if ((privKeyPswdString == null) || privKeyPswdString.trim().equals("")) {
				nPassword = null;
			} else {
				nPassword = privKeyPswdString.toCharArray();
			}
			keyStore.load(fileInputStream, nPassword);
			fileInputStream.close();
			System.out.println("keystore type=" + keyStore.getType());

			Enumeration<String> enumeration = keyStore.aliases();

			if (enumeration.hasMoreElements()) {
				keyAlias = (String) enumeration.nextElement();
				System.out.println("alias=[" + keyAlias + "]");
			}
			System.out.println("is key entry=" + keyStore.isKeyEntry(keyAlias));
			PrivateKey prikey = (PrivateKey) keyStore.getKey(keyAlias, nPassword);
			System.out.println("private key = " + prikey);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyAlias;
	}

	public static void main(String[] args) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		FileInputStream in = new FileInputStream("E:/udid.mob");
		byte[] udidbytes = new byte[in.available()];
		in.read(udidbytes);
		in.close();
//		verify(udidbytes);

		String privateKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCsjwSCazUcXoN1"
				+ "QMX8Ks6JgpyFyvDNoUxm0wPp4xUwl4C2f/3OKFW1CqrwpIZMgDbpNmFRGKDgLnj4"
				+ "AMbonr6tJUP+HEXSHajdWb00JQdvQTiZ9zw4Z0XMduebr0Lo+VNBJiodYIuZNbkY"
				+ "gI2eSiWZ620Pxgq5V+rWl8pFNUeXFaFhDaMpXybZFaZxBr0vqLXsQRZNTQUzMPWj"
				+ "Eqp/r3QwCkr0E+5Tta1EXPYmG4vZbMEfa26m/fwL5Q5p+Y0QX3G/QUFp+jLYJlV/"
				+ "bJ1qGs5jaeoklCBCh720OCYDx0D+GYBygxRG4WRzG2O0Ni6qSUYBdB7KooMKx5co"
				+ "eUCEWbU7AgMBAAECggEAFhtMbJjnePbyyHa/5oplY2CERO+24eEkNJgcDD63XD5M"
				+ "kxTaoyqvsD3poGKzgeF7J218EjOhiJbrkHopAWw06WG40Dk3CLAcDh+NCb0ksrAc"
				+ "XD08Tc2NN6CUE3HklIw8ikAlbRYNTFeBmw39FqZRIuLyWe75OL12rH9NvogQ394D"
				+ "q54QyB25docYrlj43gCANJBkk0b7HgD9KpoC9XMduacQHj4YdEz1cvGaTa+Bkm5d"
				+ "oKU9POBeiCEkDNcRwltWuFKYavFU6zyvEY42SvXraNDi/RWg1JGXhX5DaXLnErPs"
				+ "H3xB0694ExWeiVet1zgPXtGOYW96fpufejq0i9EGQQKBgQDXvTOtYwk9N1/HL9mu"
				+ "Rytr5nuoAn1WM3teKCLG6hDiBA2BtC+UHeLSkQtLceuTPIk7Uv3CC7YmxPhbmsM0"
				+ "EpWHUSPfPfVepiLTuLp9SsidJEK60JTQsFg4Ca37b6UpHNMNqwmwzvRxe0PxUaN2"
				+ "Poj0N8jCsjLrDT9Ly/BV+p2L2QKBgQDMwuYk6GilkZgjJNSJgxi5dtABwV9nnt4b"
				+ "0ogNEXhr+VlNS6enlWZBB78Hig4Wjn/BWLhhSn4ArjyzW9y/ZYuujoRMK/csW26k"
				+ "WGcAQRkp2HOKIV0p8ILR67xD9Kg8irZS9wcw+kOsB6UmEWZ5CKjS29BHJgoANHkb"
				+ "ftyeuOMBMwKBgDPdBS5aj58Kz9intTIDY/nlh7alGpJ9f9vr1ChznqlnBgQ3V/TI"
				+ "ln2+ZrO1aEeWFvuPAPgELr71PStwchrzmMTWCcSiNXmSgO7bCuIR91ZnoC8e85eT"
				+ "vTGDijLW7SKMDmTLyGGb7wHU+0lpMd28PNpRCs9bXYhKP/wtw9I3lGZJAoGBAKmT"
				+ "8A44/BHDFoBkjDCmhd9zaqlJgL3McJ4SKeLIyTaC91ZwhZTgwiKxS5/u0eQUd/Gk"
				+ "jG7mxpvBsTvJpHROoPQby61Z7AasYmFxZTxsrW5eeLG5F/MX4QABf5W4FWyuZHkp"
				+ "ZKdHXKwoWLnCR1+NsGOQ8Fk8Q6QP0QzYb9ZzwGzJAoGBAMECGg59RDiKrqCpoBA8"
				+ "7HrUJpdrKgv472gEcsgHhNDUcN40XBwnyNq5qnn9ruFmo9wNRr8IjnO4f6MrHz+3"
				+ "BTYm14cavCymVhGssEC1e45T6do5nRh+DuJ4JDpOaVc9ZB4z+C4deyUj3jSAxv7r" + "Zkg6HbuCwvNXMmrvT6aV0NnC";

		String udidmob = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n"
				+ "<plist version=\"1.0\">\r\n" + "    <dict>\r\n" + "        <key>PayloadContent</key>\r\n"
				+ "        <dict>\r\n" + "            <key>URL</key>\r\n"
				+ "            <string>https://www.baidu.com</string>\r\n"
				+ "            <key>DeviceAttributes</key>\r\n" + "            <array>\r\n"
				+ "                <string>UDID</string>\r\n" + "                <string>IMEI</string>\r\n"
				+ "                <string>ICCID</string>\r\n" + "                <string>VERSION</string>\r\n"
				+ "                <string>PRODUCT</string>\r\n" + "            </array>\r\n" + "        </dict>\r\n"
				+ "        <key>PayloadOrganization</key>\r\n" + "        <string>https://www.baidu.com</string>\r\n"
				+ "        <key>PayloadDisplayName</key>\r\n" + "        <string>查询设备UDID</string>\r\n"
				+ "        <key>PayloadVersion</key>\r\n" + "        <integer>1</integer>\r\n"
				+ "        <key>PayloadUUID</key>\r\n" + "        <string>超级签名</string>\r\n"
				+ "        <key>PayloadIdentifier</key>\r\n" + "        <string>dev.skyfox.profile-service</string>\r\n"
				+ "        <key>PayloadDescription</key>\r\n" + "        <string>本文件仅用来获取设备ID</string>\r\n"
				+ "        <key>PayloadType</key>\r\n" + "        <string>Profile Service</string>\r\n"
				+ "    </dict>\r\n" + "</plist>\r\n" + "";

		// getPrivateKeyInfo();
		CertificateFactory caf = CertificateFactory.getInstance("X.509");
		X509Certificate cacert = (X509Certificate) caf.generateCertificate(new FileInputStream("E:/ca.pem"));
		CertificateFactory cerf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cerf.generateCertificate(new FileInputStream("E:/cert.crt"));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privatekey = keyFactory
				.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
		
		byte[] signdata = sign(udidmob.getBytes("UTF-8"), privatekey, cert, cacert);
		
		FileOutputStream out = new FileOutputStream("E:/sign.mob");
		out.write(signdata);
		out.flush();
		out.close();
	}

	public static byte[] sign(byte[] signText, PrivateKey privateKey, X509Certificate certificate,
			X509Certificate caCertificate) throws Exception {
		List<X509Certificate> certList = new ArrayList<X509Certificate>();
		CMSTypedData msg = new CMSProcessableByteArray(signText);
		certList.add(caCertificate);
		certList.add(certificate);
		Store<X509Certificate> certs = new JcaCertStore(certList);
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
		ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA")
				.setProvider(BouncyCastleProvider.PROVIDER_NAME).build(privateKey);
		gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
				new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build())
						.build(sha1Signer, certificate));
		gen.addCertificates(certs);
		CMSSignedData sigData = gen.generate(msg, true);
		return sigData.getEncoded();
	}
}
