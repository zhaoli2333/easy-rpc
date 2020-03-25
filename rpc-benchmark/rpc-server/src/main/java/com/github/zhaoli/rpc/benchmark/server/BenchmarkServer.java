package com.github.zhaoli.rpc.benchmark.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhaoli
 * @date 2018/8/23
 */
@Slf4j
@SpringBootApplication
public class BenchmarkServer implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(BenchmarkServer.class);
        app.setWebEnvironment(false);
        app.run(args);
        log.info("server launched");
        new CountDownLatch(1).await();
    }
    
    @Override
    public void run(String... strings) throws Exception {
        
    }
}