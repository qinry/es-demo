package org.example.esspringclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Arrays;

@SpringBootApplication
@EnableElasticsearchRepositories
public class ESSpringClientApplication {
    public static void main(String[] args) {
       new SpringApplicationBuilder()
                .sources(ESSpringClientApplication.class)
                .web(WebApplicationType.NONE)
                .build(args)
               .run();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.indentOutput(true);
    }

    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
                Arrays.asList(new NumberToMoney(), new MoneyToNumber()));
    }

    @Bean
    CommandLineRunner run(ElasticsearchOperations elasticsearchOperations, ProductService productService) {
        return new ClientRunner(elasticsearchOperations, productService);
    }
}