package com.example.data_collection.domain;

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
    private String price;

    @Column(name = "image")
    private String image;

    @Column(name="product_link")
    private String productLink;

    @Column(name="rating")
    private double rating;

    @Column(name="discount_rate")
    private String discountRate;

}
