package com.example.core.controllers;

import com.example.core.client.FtxClient;
import com.example.core.model.OrderBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderBookController {

    @Autowired
    private FtxClient ftxClient;
    @Autowired
    private ObjectMapper mapper;

    private static final String marketName = "BTC-PERP";

    @GetMapping("/order")
    public ResponseEntity<OrderBook> getLimitOrderBook(@RequestParam(name = "depth") Integer value) throws JsonProcessingException {
        Map<String,String> params = new HashMap<>();
        params.put("depth", Integer.toString(value));

        OrderBook result = ftxClient.executeGetMethod("/markets/" + marketName + "/orderbook", params);

        return ResponseEntity.ok(result);
    }
}
