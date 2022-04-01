package org.example.esspringclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class SmokeTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void should_not_null_when_inject_repository() {
        assertThat(productRepository).isNotNull();
    }
}
