package com.example.data_collection.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Builder
@NoArgsConstructor
@Getter
public class SearchHtmlTagConfig {

    //검색을 위한 태그
    private String siteHome;
    private String searchTag;
    private String searchButtonTag;

    //데이터 저장을 위한 태그
    private String productNameTag;
    private String brandTag;

    private String priceTag;
    private String imageTag;
    private String linkTag;
    private String ratingTag;

    private String categoryNameTag;

    private String imageAttribute;
    private String linkAttribute;
}
