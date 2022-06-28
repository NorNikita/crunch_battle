package com.example.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Login implements Serializable {

    public final String op;
    public final Arguments args;

    @Builder
    @AllArgsConstructor
    public static class Arguments {
        public final String key;
        public final String sign;
        public final Long time;
    }
}
