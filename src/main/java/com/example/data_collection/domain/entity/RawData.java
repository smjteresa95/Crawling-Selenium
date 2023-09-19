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
    @Column(name = "product_id")
    private long productId;

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

    // rating의 경우 값을 가지고 올 수 없는 사이트가 있다.
    // 참조 데이터 타입을 써야 null을 넣는게 가능하다.
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
    private double servingSize;

    @Column(name="kcal")
    private double kcal;

    @Column(name="sodium")
    private double sodium;

    @Column(name="carbohydrate")
    private double carb;

    @Column(name="sugar")
    private double sugar;

    @Column(name="fat")
    private double fat;

    @Column(name="trans_fat")
    private double transFat;

    @Column(name="saturated_fat")
    private double saturatedFat;

    @Column(name="cholesterol")
    private double cholesterol;

    @Column(name="protein")
    private double protein;



}
