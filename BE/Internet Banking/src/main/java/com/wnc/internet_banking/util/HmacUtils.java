package com.wnc.internet_banking.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacUtils {

    public static String hmacSha256(String data, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] bytes = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }
}
