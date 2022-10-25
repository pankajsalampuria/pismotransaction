package com.pismo.transaction.pismotransaction.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pismo"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfo("Pismo Sample REST API",
                        "Pismo transaction api assignment.",
                        "1.0.0",
                        "N/A",
                        new Contact("Pankaj Salampuria",
                                "N/A", "pankaj.salampuria@gmail.com"),
                        "License of API", "N/A",
                        Collections.emptyList()))

                ;
    }
}
