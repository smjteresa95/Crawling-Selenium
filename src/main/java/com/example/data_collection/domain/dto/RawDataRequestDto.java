package com.example.data_collection.domain.dto;

import com.example.data_collection.domain.entity.RawData;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor
public class RawDataRequestDto {

    private String site;

    private String categoryName;

    private String subcategoryName;

    private String salesName;

    private int actualPrice;

    private int discountPrice;

    private int discountRate;

    private Double rating;

    private String image;

    private String productLink;

    private String nutriImage;

    private String productName;

    private String quantity;

    private String ingredients;

    private String nutriFacts;

    public RawData toEntity(){
        return RawData.builder()
                .site(site)
                .categoryName(categoryName)
                .subcategoryName(subcategoryName)
                .salesName(salesName)
                .actualPrice(actualPrice)
                .discountPrice(discountPrice)
                .discountRate(discountRate)
                .rating(rating)
                .image(image)
                .productLink(productLink)
                .nutriImage(nutriImage)
                .productName(productName)
                .quantity(quantity)
                .ingredients(ingredients)
                .nutriFacts(nutriFacts)
                .build();
    }
}
