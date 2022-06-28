package com.example.core.service;

import com.example.core.model.OrderBook;

public interface TopAskBid {

    void subscribeAndGetInfo();

    void getTopOrderBookAndSendToTopicWebSocket(final OrderBook orderBook);
}
