package com.example.data_collection.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass

@Setter
@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor
@ToString
public class BaseDataEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "sales_name")
    private String salesName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "actual_price")
    private int actualPrice;

    @Column(name="discount_price")
    private int discountPrice;

    @Column(name="discount_rate")
    private int discountRate;

    // rating의 경우 값을 가지고 올 수 없는 사이트가 있다.
    // 참조 데이터 타입을 써야 null을 넣는게 가능하다.
    @Column(name="rating", nullable = true)
    private Double rating;

    @Column(name = "image")
    private String image;

    @Column(name="product_link", columnDefinition = "TEXT")
    private String productLink;

    @Column(name="nutri_image")
    private String nutriImage;


    @Column(name = "product_name")
    private String productName;

    @Column(name="serving_size")
    private String servingSize;

    @Column(name="ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name="nutri_facts")
    private String nutriFacts;


}
