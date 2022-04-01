package org.example.esspringclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "products", writeTypeHint = WriteTypeHint.DEFAULT)
public class Product {

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Field(type = FieldType.Text, store = true, analyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Long, store = true)
    private Money price;
}
