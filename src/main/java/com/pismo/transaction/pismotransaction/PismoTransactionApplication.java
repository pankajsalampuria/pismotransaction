package com.pismo.transaction.pismotransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class PismoTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PismoTransactionApplication.class, args);
    }

}
