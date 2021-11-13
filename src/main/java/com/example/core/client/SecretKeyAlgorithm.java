package com.example.core.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecretKeyAlgorithm {
    HMAC_SHA256("HmacSHA256");
    private final String name;
}