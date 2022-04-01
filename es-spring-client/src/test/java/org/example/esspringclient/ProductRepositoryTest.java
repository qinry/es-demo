package org.example.esspringclient;

import org.example.esspringclient.core.ElasticsearchTestConfiguration;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Import(ElasticsearchTestConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Test
    void repository_successfully_save_product() {
        Product product = Product.builder()
                .id(1L)
                .name("自行车")
                .price(Money.ofMinor(CurrencyUnit.of("CNY"), 12000))
                .build();
        Product savedProduct = productRepository.save(product);
        Product queriedProduct = productRepository.findByName(product.getName());
        assertThat(queriedProduct.getId(), is(notNullValue()));
        assertThat(queriedProduct.getName(), equalTo(product.getName()));
        assertThat(queriedProduct.getPrice(), equalTo(product.getPrice()));
    }
}