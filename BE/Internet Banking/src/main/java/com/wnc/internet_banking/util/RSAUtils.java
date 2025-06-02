
package com.wnc.internet_banking.util;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    private static PrivateKey loadPrivateKey(String key) throws Exception {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("BANK_PRIVATE_KEY environment variable not set or empty");
        }
        key = key.replace("\\n", "\n");
        key = key.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        ASN1Primitive primitive = ASN1Primitive.fromByteArray(decoded);
        ASN1Sequence sequence = ASN1Sequence.getInstance(primitive);

        RSAPrivateKey rsaPrivateKey = RSAPrivateKey.getInstance(sequence);

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                rsaPrivateKey.getModulus(),
                rsaPrivateKey.getPublicExponent(),
                rsaPrivateKey.getPrivateExponent(),
                rsaPrivateKey.getPrime1(),
                rsaPrivateKey.getPrime2(),
                rsaPrivateKey.getExponent1(),
                rsaPrivateKey.getExponent2(),
                rsaPrivateKey.getCoefficient()
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static PublicKey loadPublicKey(String publicKey) throws Exception {
        String key = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static String sign(String data, String privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(loadPrivateKey(privateKey));
        privateSignature.update(data.getBytes());
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String data, String signatureStr, String publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(loadPublicKey(publicKey));
        publicSignature.update(data.getBytes());
        byte[] signature = Base64.getDecoder().decode(signatureStr);
        return publicSignature.verify(signature);
    }
}
