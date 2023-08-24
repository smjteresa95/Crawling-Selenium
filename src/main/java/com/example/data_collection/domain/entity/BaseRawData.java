package com.example.data_collection.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class BaseRawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private int price;

    @Column(name = "image")
    private String image;

    @Column(name="product_link", length = 10000)
    private String productLink;

    // rating의 경우 값을 가지고 올 수 없는 사이트가 있다.
    // 참조 데이터 타입을 써야 null을 넣는게 가능하다.
    @Column(name="rating", nullable = true)
    private Double rating;

//    @Column(name="discount_rate")
//    private double discountRate;

}
