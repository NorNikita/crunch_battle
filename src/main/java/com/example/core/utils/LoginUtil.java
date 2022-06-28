package com.example.core.utils;

import com.example.core.model.Login;

import javax.validation.constraints.NotNull;
import java.time.Clock;

import static com.example.core.utils.CryptoUtil.sign;

public class LoginUtil {

    public static Login getLoginEntityWs(@NotNull String apiKey, @NotNull String secretKey) {
        long millis = Clock.systemUTC().millis();
        String ts = Long.toString(millis);
        return Login.builder()
                .op("login")
                .args(Login.Arguments.builder()
                        .key(apiKey)
                        .sign(sign(ts + "websocket_login", secretKey.getBytes()))
                        .time(millis)
                        .build())
                .build();
    }
}
