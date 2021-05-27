package com.weaponlin.inf.prpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ProviderApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
