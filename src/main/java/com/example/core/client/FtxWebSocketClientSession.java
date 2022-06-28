package com.example.core.client;

import com.example.core.model.OrderBook;
import com.example.core.service.TopAskBid;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ClientEndpoint
public class FtxWebSocketClientSession {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TopAskBid topAskBid;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @OnOpen
    public void onOpen() {
        log.info("OPEN!");
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("MESSAGE: {}", message);
        OrderBook orderBook = objectMapper.readValue(message, OrderBook.class);
        this.pool.submit(() -> topAskBid.getTopOrderBookAndSendToTopicWebSocket(orderBook));
    }

    @OnClose
    public void onClose(Session session) {
        log.info("CLOSE");
    }
}
