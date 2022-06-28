package com.example.core.controllers;

import com.example.core.client.FtxClient;
import com.example.core.model.OrderBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class OrderBookController {

    @Autowired
    private FtxClient ftxClient;

    private static final String marketName = "BTC-PERP";

    @GetMapping("/order")
    public ResponseEntity<OrderBook> getLimitOrderBook(@RequestParam(name = "depth") Integer value) throws JsonProcessingException {
        Map<String, String> params = new HashMap<>();
        params.put("depth", Integer.toString(value));

        OrderBook result = ftxClient.executeHttpRequest("/markets/" + marketName + "/orderbook", RequestMethod.GET.name(), params);

        return ResponseEntity.ok(result);
    }
}
