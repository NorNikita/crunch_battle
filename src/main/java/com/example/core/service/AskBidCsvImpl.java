package com.example.core.service;

import com.example.core.client.FtxClient;
import com.example.core.model.OrderBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AskBidCsvImpl implements AskBidCsv {
    private static final String marketName = "BTC-PERP";
    private final FtxClient ftxClient;

    private static List<List<Double>> top10Bid = new ArrayList<>(10);
    private static List<List<Double>> top10Ask = new ArrayList<>(10);

    @Autowired
    public AskBidCsvImpl(FtxClient ftxClient) {
        this.ftxClient = ftxClient;
    }

    @Override
    public void subscribeAndGetInfo() {
        final Map<String,String> params = new HashMap<>();
        params.put("depth", Integer.toString(10));


        try (PrintWriter result = new PrintWriter("src/main/resources/bid_ask.txt")) {
            //Asks   price   amount   Bids   price   amount
            StringBuilder rows = new StringBuilder();
            while (true) {
                log.info("get new data! \n");
                OrderBook orderBook = ftxClient.executeGetMethod("/markets/" + marketName + "/orderbook", params);
                defineNewTopAsk(orderBook.getResult().getAsks());
                defineNewTopBid(orderBook.getResult().getBids());

                for (int i = 0; i < orderBook.getResult().getAsks().size(); i++) {
                    List<Double> currentAsks = orderBook.getResult().getAsks().get(i);
                    List<Double> currentBids = orderBook.getResult().getBids().get(i);
                    rows.append("    ");
                    rows.append(currentAsks.get(0));
                    rows.append("  ");
                    rows.append(currentAsks.get(1));
                    rows.append("    ");
                    rows.append(currentBids.get(0));
                    rows.append("  ");
                    rows.append(currentBids.get(1));
                    rows.append("  \n");
                }
                result.write(rows.toString());
                rows = new StringBuilder();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
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