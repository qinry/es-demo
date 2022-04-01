package org.example.esnativeclient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ESNativeClient7Application {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) throws IOException, InterruptedException {
        // Create the low-level client
        try (RestClient restClient = RestClient.builder(
                new HttpHost("10.119.6.176", 9200)).build();
             // Create the transport with a Jackson mapper
             ElasticsearchTransport transport = new RestClientTransport(
                     restClient, new JacksonJsonpMapper())) {

            // And create the API client
            ElasticsearchClient client = new ElasticsearchClient(transport);

            // Create Index
            BooleanResponse resp = client.indices().exists(e -> e.index("products"));
            if (!resp.value()) {
                client.indices().create(c -> c
                        .index("products")
                        .mappings(m -> m
                                .properties("name", Property.of(o -> o
                                                .text(t -> t
                                                        .store(true)
                                                        .index(true)
                                                        .analyzer("ik_smart"))
                                        )
                                )
                        ).settings(s -> s
                                .numberOfShards("3")
                                .numberOfReplicas("2")
                        ).aliases("<products{now/M}>", a -> a)
                );

                client.index(c -> c
                        .index("products")
                        .id("1")
                        .document(Product.builder().name("bicycle").build()));
            }
            // Search
            SearchResponse<Product> search1 = client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .term(t -> t
                                            .field("name")
                                            .value(v -> v.stringValue("bicycle"))
                                    )
                            ),
                            Product.class);

            for (Hit<Product> hit : search1.hits().hits()) {
                processProduct(hit.source());
            }

            SearchResponse<Product> search2 = client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .match(m -> m
                                            .field("name")
                                            .query("bicycle")
                                    )
                            ),
                            Product.class);

            for (Hit<Product> hit : search2.hits().hits()) {
                processProduct(hit.source());
            }

            SearchResponse<Product> search3 = client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .matchAll(v -> v.queryName("name"))
                            ),
                            Product.class);

            for (Hit<Product> hit : search3.hits().hits()) {
                processProduct(hit.source());
            }

            SearchResponse<Product> search4 = client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .prefix(p -> p
                                            .field("name")
                                            .value("bi")
                                    )
                            ),
                            Product.class);
            for (Hit<Product> hit : search4.hits().hits()) {
                processProduct(hit.source());
            }

            // highlight search and boolean search
            SearchResponse<Product> search5 = client.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .matchAll(v -> v)
                                            )
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("name")
                                                            .value(v -> v.stringValue("bicycle"))
                                                    )
                                            )
                                    )
                            )
                            .highlight(h -> h
                                    .fields("name", HighlightField.of(o -> o
                                            .preTags("<em>")
                                            .postTags("</em")))
                                    ),
                            Product.class);
            for (Hit<Product> hit : search5.hits().hits()) {
                processProduct(hit.source());
                System.out.println(hit.highlight());
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void processProduct(Product source) throws JsonProcessingException {
        String jsonStr = OBJECT_MAPPER.writeValueAsString(source);
        System.out.println(jsonStr);
    }
}
