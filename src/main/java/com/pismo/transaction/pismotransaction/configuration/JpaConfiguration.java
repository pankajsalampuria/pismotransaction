package com.pismo.transaction.pismotransaction.configuration;

import com.pismo.transaction.pismotransaction.entity.AuditorAwareImpl;
import com.pismo.transaction.pismotransaction.entity.OperationType;
import com.pismo.transaction.pismotransaction.repository.OperationTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(
        dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfiguration {

    final static Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);


    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @Bean
    CommandLineRunner initDatabase(OperationTypeRepository repository) {

        return args -> {
            logger.info("Preloading " + repository.save(new OperationType(1l, "Normal Purchase")));
            logger.info("Preloading " + repository.save(new OperationType(2l, "Purchase with installments")));
            logger.info("Preloading " + repository.save(new OperationType(3l, "Withdrawal")));
            logger.info("Preloading " + repository.save(new OperationType(4l, "Credit Voucher")));

        };
    }
}
