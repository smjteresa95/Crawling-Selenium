package com.example.data_collection.config;

import lombok.*;

@AllArgsConstructor @Builder
@NoArgsConstructor
@Getter
public class HtmlTagConfig {

    private String siteUrl;

    //데이터 찾기 위한 태그
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

    //검색을 위한 태그
    private String siteHome;
    private String searchTag;
    private String searchButtonTag;

}


