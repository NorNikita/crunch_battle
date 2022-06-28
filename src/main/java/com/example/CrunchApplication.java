package com.example;

import com.example.core.client.FtxClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrunchApplication implements CommandLineRunner {

    @Autowired
    private FtxClient ftxClient;

    @Override
    public void run(String... args) throws Exception {
        ftxClient.subscribeOnTopic();
    }

    public static void main(String[] args) {
        SpringApplication.run(CrunchApplication.class, args);
    }
}

