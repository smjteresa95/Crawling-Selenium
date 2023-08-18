package com.example.data_collection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HtmlTagConfigFactory {

    public String getSSGNextPageButtonXPathForCurrentPage(int currentPage){
        return "//div[@class='com_paginate notranslate']//a[contains(text(), '" + (currentPage + 1) + "')]";
    }

    public String getElevenNextPageButtonXPathForCurrentPage(int currentPage){
        return String.format("//*[@id='list_paging']/span/a[text()='%d']", currentPage + 1);
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
                        .nextPageButtonXPath(getSSGNextPageButtonXPathForCurrentPage(currentPage))

                        .imageAttribute("src")
                        .linkAttribute("href")

                        .siteHome("https://shinsegaemall.ssg.com/")
                        .searchTag("//*[@id=\"ssg-query\"]")
                        .searchButtonTag("//*[@id=\"ssg-query-btn\"]/i")
                        .build();

            case "eleven":
                return HtmlTagConfig.builder()
                        .siteUrl("https://www.11st.co.kr/category/DisplayCategory.tmall?method=getDisplayCategory2Depth&dispCtgrNo=")

//                        .productNameTag("//*/li[contains(@id, 'thisClick_')]/div/div[2]/p/a")
                        .productNameTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[4]/ul/li[4]/div/div[2]/div[1]/div[1]/a/strong")
                        .brandTag("//*/li[contains(@id, 'thisClick_')]/div/div[4]/p[1]/a")
                        .priceTag("//*/li[contains(@id, 'thisClick_')]/div/div[3]/div[1]/span[1]/strong")
                        .imageTag("//*/li[contains(@id, 'thisClick_')]/div/div[1]/a/img")
                        .linkTag("//*/li[contains(@id, 'thisClick_')]/div/div[2]/p[1]/a")
                        .ratingTag("//*/li[contains(@id, 'thisClick_')]/div/div[2]/div/div[1]/div[1]/span[contains(@class, 'selr_star')]")
                        .discountRateTag("//*/li[contains(@id, 'thisClick_')]/div/div[3]/div[1]/span[2]/span[@class='sale']")

                        .categoryNameTag("//*[@id=\"layBody\"]/div[1]/div/div[1]/h3")

                        .nextGroupButtonXPath("//*[@id=\"list_paging\"]/a[@data-log-actionid-label=\"page\"]")
                        .nextPageButtonXPath(getElevenNextPageButtonXPathForCurrentPage(currentPage))

                        .imageAttribute("src")
                        .linkAttribute("href")

                        .siteHome("https://www.11st.co.kr/")
                        .searchTag("//*[@id=\"tSearch\"]/form/fieldset/input")
                        .searchButtonTag("//*[@id=\"tSearch\"]/form/fieldset/button")

                        .build();

            default:
                throw new IllegalAccessException("Unsupported site: " + siteName);
        }
    }

}
