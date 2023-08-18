package com.example.data_collection.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="api_data")

@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor @ToString
public class ApiData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @Column(name="FOOD_CD")
//    private String code;

    @Column(name="GROUP_NAME")
    private String groupName;

    @Column(name ="GROUP_DETAIL")
    private String groupDetail;

    @Column(name="PRODUCT_NAME")
    private String productName;

    @Column(name="MAKER_NAME")
    private String maker;

    @Column(name = "SERVING_SIZE")
    private int servingSize;

    @Column(name="CALORIES")
    private double calories;

    @Column(name="PROTEIN")
    private double protein;

    @Column(name="FAT")
    private double fat;

    @Column(name="CARBOHYDRATES")
    private double carbohydrates;

    @Column(name="SUGARS")
    private double sugars;

    @Column(name = "SODIUM")
    private double sodium;

    @Column(name = "CHOLESTEROL")
    private double cholesterol;

    @Column(name="SATURATED_FAT")
    private double saturatedFat;

    @Column(name = "TRANS_FAT")
    private double transFat;


}
