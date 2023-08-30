package com.example.data_collection.domain.dto;

import com.example.data_collection.domain.entity.OasisDataEntity;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor
public class OasisDataRequestDto {

    private String categoryName;

    private String subcategoryName;

    private String salesName;

    private String brand;

    private int actualPrice;

    private int discountPrice;

    private int discountRate;

    private Double rating;

    private String image;

    private String productLink;

    private String nutriImage;

    private String productName;

    private String servingSize;

    private String ingredients;

    private String nutriFacts;

    public OasisDataEntity toEntity(){
        return OasisDataEntity.builder()
                .categoryName(categoryName)
                .subcategoryName(subcategoryName)
                .salesName(salesName)
                .brand(brand)
                .actualPrice(actualPrice)
                .discountPrice(discountPrice)
                .discountRate(discountRate)
                .rating(rating)
                .image(image)
                .productLink(productLink)
                .nutriImage(nutriImage)
                .productName(productName)
                .servingSize(servingSize)
                .ingredients(ingredients)
                .nutriFacts(nutriFacts)
                .build();
    }
}
