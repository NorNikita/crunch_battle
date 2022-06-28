package com.example.core.service;

import com.example.core.model.OrderBook;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TopAskBidImpl implements TopAskBid {
    private static final String marketName = "BTC-PERP";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    private static List<List<Double>> top10Bid = new ArrayList<>(10);
    private static List<List<Double>> top10Ask = new ArrayList<>(10);

    public TopAskBidImpl(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void subscribeAndGetInfo() {
    }

    @Override
    @SneakyThrows
    public void getTopOrderBookAndSendToTopicWebSocket(final OrderBook orderBook) {
        if (orderBook.getResult() == null) {
            return;
        }
        defineNewTopAsk(orderBook.getResult().getAsks());
        defineNewTopBid(orderBook.getResult().getBids());

        simpMessagingTemplate.convertAndSend("/topic/bid", objectMapper.writeValueAsString(top10Bid));
        simpMessagingTemplate.convertAndSend("/topic/ask", objectMapper.writeValueAsString(top10Ask));
    }

    private void defineNewTopAsk(List<List<Double>> data) {
        List<List<Double>> all = new ArrayList<>();
        all.addAll(data);
        all.addAll(top10Ask);
        top10Ask = all.stream()
                .sorted((pair1, pair2) -> pair1.get(0) < pair2.get(0) ? 0 : 1)
                .limit(10)
                .collect(Collectors.toList());
    }

    private void defineNewTopBid(List<List<Double>> data) {
        List<List<Double>> all = new ArrayList<>();
        all.addAll(data);
        all.addAll(top10Bid);
        top10Bid = all.stream()
                .sorted((pair1, pair2) -> pair1.get(0) < pair2.get(0) ? 1 : 0)
                .limit(10)
                .collect(Collectors.toList());
    }
}