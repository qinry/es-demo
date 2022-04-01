package org.example.esspringclient;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientRunner implements CommandLineRunner {

    private final ElasticsearchOperations elasticsearchOperations;

    private final ProductService productService;

    public ClientRunner(ElasticsearchOperations elasticsearchOperations, ProductService productService) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        productService.deleteAll();
        // 准备数据
        Product product = Product.builder()
                .id(1L)
                .name("Bicycle")
                .price(Money.ofMinor(CurrencyUnit.of("CNY"), 12000))
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Motorcycle")
                .price(Money.ofMinor(CurrencyUnit.of("CNY"), 300000))
                .build();

        // [1]
        productService.save(product);

        // [2]
        saveProduct(product2);
        log.info("Product(id=2) exists: {}", elasticsearchOperations.exists("2", Product.class));

        // [3]
        productService.queryProductByName("Bicycle");

        TimeUnit.SECONDS.sleep(1);
        Criteria criteria = new Criteria("name").is("Motorcycle");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        for (SearchHit<Product> hit : elasticsearchOperations.search(criteriaQuery, Product.class).getSearchHits()) {
            processProduct(hit.getContent());
        }
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(PageRequest.of(0, 20))
                .withSorts(SortBuilders.fieldSort("price").order(SortOrder.ASC))
                .build();
        for (SearchHit<Product> hit : elasticsearchOperations.search(nativeSearchQuery, Product.class).getSearchHits()) {
            processProduct(hit.getContent());
        }
    }

    private void saveProduct(Product product) {
        IndexQuery idxQuery = new IndexQueryBuilder()
                .withId(product.getId().toString())
                .withObject(product)
                .build();
        String docId = elasticsearchOperations.index(idxQuery, IndexCoordinates.of("products"));

        log.info("template save product:{} and saved product docId: {}", product, docId);
    }

    private void processProduct(Product content) {
        log.info("query product by template:{}", content);
    }
}
