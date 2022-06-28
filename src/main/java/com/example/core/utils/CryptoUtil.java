package com.example.core.utils;

import com.google.common.io.BaseEncoding;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CryptoUtil {

    public static String sign(String payload, byte[] secret) {
        return encode(
                hmacSha256(secret).doFinal(payload.getBytes(UTF_8))
        );
    }

    public static String encode(byte[] some) {
        return BaseEncoding.base16().lowerCase().encode(some);
    }

    public static Mac hmacSha256(byte[] secret) {
        try {
            String algorithmName = SecretKeyAlgorithm.HMAC_SHA256.getName();
            Mac mac = Mac.getInstance(algorithmName);
            Key spec = new SecretKeySpec(secret, algorithmName);
            mac.init(spec);
            return mac;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
