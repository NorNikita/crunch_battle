package com.example.core.client;

import com.example.core.model.OrderBook;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.core.utils.CryptoUtil.sign;

@Service
public class FtxClient {

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.secret}")
    private String secretKey;
    @Value("${url.ws}")
    private String wsBaseUrl;
    @Value("${url.http}")
    private String httpBaseUrl;

    @Autowired
    private FtxWebSocketClientSession ftxWebSocketClientSession;

    ExecutorService pool = Executors.newFixedThreadPool(1);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://ftx.com/api")
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer
                            .defaultCodecs()
                            .maxInMemorySize(16 * 1024 * 1024))
                    .build()
            ).build();

    @SneakyThrows
    public void subscribeOnTopic() {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

        Session session = webSocketContainer.connectToServer(ftxWebSocketClientSession, URI.create(wsBaseUrl));

        String groupedOrderBook =  "{\"op\": \"subscribe\", \"channel\": \"orderbookGrouped\"," +
                " \"market\": \"BTC-PERP\", \"grouping\": \"500\"}";

        pool.submit(() -> {
            System.out.println("subscribed");
            session.getAsyncRemote().sendText(groupedOrderBook);
        });
    }

    public OrderBook executeHttpRequest(String relativePath, String methodName, Map<String, String> params) {
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
}
