package com.example.data_collection.config;

import org.springframework.stereotype.Component;

@Component
public class HtmlConfigFactory {

    public String getOasisNextPageButtonXpath(int currentPage){
        return String.format("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@href = 'javascript:page(%d);']", currentPage+1);
    }

    public String getSsgNextPageButtonXpath(int currentPage){
        return "//div[@class='com_paginate notranslate']//a[contains(text(), '" + (currentPage + 1) + "')]";
    }

    public HtmlConfig getTagForSite(String siteName, int currentPage) throws IllegalAccessException {
        switch (siteName){
            case "oasis":
                return HtmlConfig.builder()
                        .siteUrl("https://www.oasis.co.kr/product/list?categoryId=")
                        .itemList("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li")
                        .link(".//div/div[1]/a")
                        .detailTab("//*[@id=\"pViewTab\"]/ul/li[2]/a[@class='tabBtn btn03']")

                        .salesName("div.textSubject > h1")
                        .actualPrice("dd[class*='dr']")
                        .discountPrice("//*[@id=\"sec_product_view\"]/div[2]/div/div[2]/div[2]/dl/div[1]/dd[2]/span/b")
                        .discountRate("strong[class*='dr']")
                        .rating("detailStar")
                        .image("//*[@id=\"sec_product_view\"]/div[2]/div/div[1]/div[1]/img")
                        .nutriImage("//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[@class='conts_productInfo_notice img']/div/img")

                        .tablePath("//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody")
                        .rowPath("//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody" + "/tr")
                        .titlePath("./td[1]/font/strong")
                        .valuePath("./td[3]")

                        .nextPageButton(getOasisNextPageButtonXpath(currentPage))
                        .nextGroupButton("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@class='pgBtn_next']")

                        .build();

            case "ssg":
                return HtmlConfig.builder()
                        .siteUrl("https://www.ssg.com/disp/category.ssg?ctgId=")
                        .link("")
                        .detailTab("")

                        .salesName("//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[2]/h2/span/span[@class='cdtl_info_tit_txt']")
                        .actualPrice("//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_old_price']/em[@class='ssg_price']")
                        .discountPrice("//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_new_price notranslate']/em[@class='ssg_price']")
                        .discountRate("//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_new_price notranslate']/em[@class='ssg_percent']")
                        .rating("//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[3]/dl/dd/div/a/div[1]/span[2]/em")
                        .image("//*[@id=\"mainImg\"]")
                        .nutriImage("//*[@id=\"wrap_ifr\"]/div/div[@Class='cdtl_tmpl_guide']/div/div/div[2]/img[contains(@alt, '품질표시이미지')]")

                        .nextPageButton(getSsgNextPageButtonXpath(currentPage))
                        .nextGroupButton("//div[@class='com_paginate notranslate']//a[@class='btn_next on' and @title='다음']")

                        .build();

            default:
                throw new IllegalAccessException("Unsupported site: " + siteName);
        }
    }
}
