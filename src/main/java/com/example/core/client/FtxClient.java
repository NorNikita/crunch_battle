package com.example.core.client;

import com.example.core.model.OrderBook;
import com.google.common.io.BaseEncoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class FtxClient {

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.secret}")
    private String secretKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://ftx.com/api")
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(16 * 1024 * 1024))
                    .build()
            ).build();



    public OrderBook executeGetMethod(String relativePath, Map<String, String> params) {
        return executeRequest(relativePath, RequestMethod.GET.name(), params);
    }

    public OrderBook executeRequest(String relativePath, String methodName, Map<String, String> params) {
        return webClient
                .get()
                .uri(builder -> {
                    UriBuilder path = builder.path(relativePath);
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        path.queryParam(entry.getKey(), entry.getValue());
                    }
                    return path.build();
                })
                .headers(h -> {
                    String ts = Long.toString(Clock.systemUTC().millis());
                    String payload = ts + methodName + "/api" + relativePath + "/?depth=" + params.get("depth");
                    String signature = sign(payload, secretKey.getBytes());
                    h.add("FTX-KEY", apiKey);
                    h.add("FTX-SIGN", signature);
                    h.add("FTX-TS", ts);
                    h.add("Content-Type", "application/json");
                })
                .exchange()
                .flatMap(response -> response.bodyToMono(OrderBook.class))
                .block();
    }

    private static String sign(String payload, byte[] secret) {
        return encode(
                hmacSha256(secret).doFinal(payload.getBytes(UTF_8))
        );
    }

    private static Mac hmacSha256(byte[] secret) {
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

    private static String encode(byte[] some) {
       return BaseEncoding.base16().lowerCase().encode(some);
    }

    /**
     * представление параметров запроса, нужно для кодировки
     * @param params
     * @return
     */
    private String asString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for(Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            if (params.size() > 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
}
