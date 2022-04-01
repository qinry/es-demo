package org.example.esspringclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
    @Resource
    private ProductRepository productRepository;

    public Optional<Product> queryProductByName(String name) {
        Optional<Product> queriedProduct = Optional.ofNullable(productRepository.findByName(name));
        queriedProduct.ifPresent(o -> {
            log.info("query product by repository: {}", o);
        });

        return queriedProduct;
    }

    public void deleteAll() {
        productRepository.deleteAll();
        log.info("index products deleted all");
    }

    public void save(Product product) {
        Product savedProduct = productRepository.save(product);
        log.info("input params: {} and repository save product: {}", product, savedProduct);
    }
}
