package com.example.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderBook {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("result")
    private Result result;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        //sellers
        @JsonProperty("asks")
        private List<List<Double>> asks;

        //byers
        @JsonProperty("bids")
        private List<List<Double>> bids;
    }

}
