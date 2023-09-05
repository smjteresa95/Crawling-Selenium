package com.example.data_collection.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ssg_data")
@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@ToString
public class SsgDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name= "subcategory_name")
    private String subcategoryName;

    @Column(unique = true, name = "sales_name")
    private String salesName;

    @Column(name = "actual_price")
    private int actualPrice;

    @Column(name="discount_price")
    private int discountPrice;

    @Column(name="discount_rate")
    private int discountRate;

    @Column(name="rating")
    private Double rating;

    @Column(name = "image")
    private String image;

    @Column(name="product_link", columnDefinition = "TEXT")
    private String productLink;

    @Column(name="nutri_image")
    private String nutriImage;

    @Column(name = "product_name")
    private String productName;

    @Column(name="quantity")
    private String quantity;

    @Column(name="ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name="nutri_facts", columnDefinition = "TEXT")
    private String nutriFacts;

    @Column(name="report_num")
    private String reportNum;

    @Column(name="serving_size")
    private String servingSize;

    @Column(name="kcal")
    private String kcal;

    @Column(name="sodium")
    private String sodium;

    @Column(name="carb")
    private String carb;

    @Column(name="sugar")
    private String sugar;

    @Column(name="fat")
    private String fat;

    @Column(name="trans_fat")
    private String transFat;

    @Column(name="saturated_fat")
    private String saturatedFat;

    @Column(name="cholesterol")
    private String cholesterol;

    @Column(name="protein")
    private String protein;



}
