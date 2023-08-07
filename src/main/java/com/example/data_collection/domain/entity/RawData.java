package com.example.data_collection.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raw_data")

@Setter @Getter
@AllArgsConstructor @Builder
@NoArgsConstructor

public class RawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private String price;

//    @Column(name = "image")
//    private String image;
//
//    @Column(name="product_link")
//    private String productLink;
}
