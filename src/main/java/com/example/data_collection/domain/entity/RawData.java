package com.example.data_collection.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="rawdata")
@AllArgsConstructor @Builder
@Getter
@NoArgsConstructor
@ToString
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rawdata_id")
    private long rawdataId;

    //상품 판매처
    @Column(name="site")
    private String site;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name= "subcategory_name")
    private String subcategoryName;

    @Column(name = "sales_name")
    private String salesName;

    @Column(name = "actual_price")
    private int actualPrice;

    @Column(name="discount_price")
    private int discountPrice;

    @Column(name="discount_rate")
    private int discountRate;

    @Column(name="rating", nullable = true)
    private Double rating;

    @Column(name = "image")
    private String image;

    @Column(name="product_link", columnDefinition = "TEXT")
    private String productLink;

    @Column(name="nut_image")
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
    private Double servingSize;

    @Column(name="kcal")
    private Double kcal;

    @Column(name="sodium")
    private Double sodium;

    @Column(name="carbohydrate")
    private Double carb;

    @Column(name="sugar")
    private Double sugar;

    @Column(name="fat")
    private Double fat;

    @Column(name="trans_fat")
    private Double transFat;

    @Column(name="saturated_fat")
    private Double saturatedFat;

    @Column(name="cholesterol")
    private Double cholesterol;

    @Column(name="protein")
    private Double protein;

}
