package com.github.zhaoli.rpc.benchmark.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
@SpringBootApplication
@Slf4j
public class BenchmarkClient implements CommandLineRunner {
    @Autowired
    private EasyClient client;

    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(BenchmarkClient.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        client.run(strings);
        client.run(20,2000,1000,1000);
//        System.exit(0);
    }
}