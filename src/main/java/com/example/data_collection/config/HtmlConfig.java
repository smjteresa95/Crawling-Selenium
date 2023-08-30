package com.example.data_collection.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class HtmlConfig {
    private String siteUrl;
    private String itemList;
    private String link;
    private String detailTab;

    private String salesName;
    private String actualPrice;
    private String discountPrice;
    private String discountRate;
    private String rating;
    private String image;
    private String nutriImage;

    private String iframe;
    private String tablePath;
    private String rowPath;
    private String titlePath;
    private String valuePath;

    private String nextPageButton;
    private String nextGroupButton;

}
