package com.example.data_collection.config;

import lombok.*;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor @Builder
@NoArgsConstructor
@Getter
public class HtmlTagConfig {

    private String siteUrl;

    private String productNameTag;
    private String brandTag;
    private String priceTag;
    private String imageTag;
    private String linkTag;
    private String ratingTag;
    private String discountRateTag;
    private String categoryNameTag;

    private String nextGroupButtonXPath;
    private String nextPageButtonXPath;

    private String imageAttribute;
    private String linkAttribute;

}


