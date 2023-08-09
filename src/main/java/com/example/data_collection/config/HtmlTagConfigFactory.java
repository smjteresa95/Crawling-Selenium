package com.example.data_collection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HtmlTagConfigFactory {

    public String getNextPageButtonXPathForCurrentPage(int currentPage){
        return "//div[@class='com_paginate notranslate']//a[contains(text(), '" + (currentPage + 1) + "')]";
    }

    public HtmlTagConfig getHtmlTagForSite(String siteName, int currentPage) throws IllegalAccessException {

        switch(siteName){
            case "ssg":
                String productTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]";

                return HtmlTagConfig.builder()
                        .siteUrl("https://shinsegaemall.ssg.com/disp/category.ssg?dispCtgId=")

                        .productNameTag(productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_tit\"]")
                        .brandTag(productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_brand\"]")
                        .priceTag(productTag + "/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row mnsditem_ty_newpr\"]/div[@class=\"new_price\"]/em[@class=\"ssg_price\"]")
                        .imageTag("//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_thmb\"]/a/div[@class=\"mnsditem_thmb_imgbx\"]/img[@class='i1']")
                        .linkTag("//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_thmb\"]/a[@class=\"mnsditem_thmb_link clickable\"]")
                        .ratingTag("//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_sideinfo\"]/div[@class=\"mnsditem_review\"]/div[@class='mnsditem_review_score']")
                        .discountRateTag("//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row.mnsditem_ty_newpr\"]/div[@class=\"discount_rate\"]")

                        .categoryNameTag("//*[@id=\"area_disp_ctg_title\"]/h2/a[@class=\"notranslate clickable\"]")

                        .nextGroupButtonXPath("//div[@class='com_paginate notranslate']//a[@class='btn_next on' and @title='다음']")
                        .nextPageButtonXPath(getNextPageButtonXPathForCurrentPage(currentPage))

                        .imageAttribute("src")
                        .linkAttribute("href")
                        .build();
            default:
                throw new IllegalAccessException("Unsupported site: " + siteName);
        }
    }

}
