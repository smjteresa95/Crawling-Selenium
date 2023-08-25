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

    public String getKurlyNextPageButtonXpathForCurrentPage(int currentPage){
        return String.format("//*[@id=\"container\"]/div[2]/div[2]/div[3]/a[matches(text(), '^/d+$') and text()='%d']", currentPage+1);
    }

    public String getOasisNextPageButtonXpathForCurrentPage(int currentPage){
        return String.format("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@href = 'javascript:page(%d);']", currentPage+1);
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
//                        .discountRateTag("//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row.mnsditem_ty_newpr\"]/div[@class=\"discount_rate\"]")

                        .categoryNameTag("//*[@id=\"area_disp_ctg_title\"]/h2/a[@class=\"notranslate clickable\"]")

                        .nextGroupButtonXPath("//div[@class='com_paginate notranslate']//a[@class='btn_next on' and @title='다음']")
                        .nextPageButtonXPath(getSSGNextPageButtonXPathForCurrentPage(currentPage))

                        .imageAttribute("src")
                        .linkAttribute("href")

                        .build();

            case "eleven":
                return HtmlTagConfig.builder()
                        .siteUrl("https://www.11st.co.kr/category/DisplayCategory.tmall?method=getDisplayCategory2Depth&dispCtgrNo=")

                        .productNameTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[4]/ul/li[4]/div/div[2]/div[1]/div[1]/a/strong")
                        .brandTag("//*/li[contains(@id, 'thisClick_')]/div/div[4]/p[1]/a")
                        .priceTag("//*/li[contains(@id, 'thisClick_')]/div/div[3]/div[1]/span[1]/strong")
                        .imageTag("//*/li[contains(@id, 'thisClick_')]/div/div[1]/a/img")
                        .linkTag("//*/li[contains(@id, 'thisClick_')]/div/div[2]/p[1]/a")
                        .ratingTag("//*/li[contains(@id, 'thisClick_')]/div/div[2]/div/div[1]/div[1]/span[contains(@class, 'selr_star')]")
//                        .discountRateTag("//*/li[contains(@id, 'thisClick_')]/div/div[3]/div[1]/span[2]/span[@class='sale']")

                        .categoryNameTag("//*[@id=\"layBody\"]/div[1]/div/div[1]/h3")

                        .nextGroupButtonXPath("//*[@id=\"list_paging\"]/a[@data-log-actionid-label=\"page\"]")
                        .nextPageButtonXPath(getElevenNextPageButtonXPathForCurrentPage(currentPage))

                        .imageAttribute("src")
                        .linkAttribute("href")

                        .build();

            case "kurly":
                return HtmlTagConfig.builder()
                        .siteUrl("https://www.kurly.com/categories/")

                        .productNameTag("//*[@id=\"container\"]/div/div[2]/div[2]/a[1]/div[2]/span[@class=\"css-1dry2r1 e1c07x488\"]")
                        //.brandTag()는 따로 값을 따올 수 없고 괄호[] 안에 있는 값이 브랜드 명이므로, 상품명을 받아와서 그 안에서 브랜드 명 뽑아오기.
                        .priceTag("//*[@id=\"container\"]/div/div[2]/div[2]/a[35]/div[2]/div[1]/div/span[@class='sales-price css-18tpqqq ei5rudb1']\"")
                        .imageTag("//*[@id=\"container\"]/div/div[2]/div[2]/a[1]/div[1]/div/span/img[@alt='상품 이미지']")
                        .linkTag("//*[@id=\"container\"]/div/div[2]/div[2]/a[contains(@class, 'css')]")
                        //.ratingTag() 마켓컬리는 평점이 존재하지 않음

                        .categoryNameTag("//*[@id='container']/h3[contains(@class, 'css-i804ml')]")

                        .nextPageButtonXPath(getKurlyNextPageButtonXpathForCurrentPage(currentPage))
                        .nextGroupButtonXPath("//*[@id=\"container\"]/div[2]/div[2]/div[3]/a[13]/img[contains(@alt,'다음 페이지')]")

                        .imageAttribute("src")
                        .linkAttribute("href")
                        .build();

            case "oasis":
                return HtmlTagConfig.builder()
                        .siteUrl("https://www.oasis.co.kr/product/list?categoryId=")

                        .productNameTag("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[contains(@class, 'swiper-slide pId')]/div/div[2]/div[1]/div[3]/div[1]/span/a[@class='listTit']")
                        //.brandTag() 따로 값을 따올 수 없다.
                        .priceTag("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[2]/div/div[2]/div[1]/div[5]/span[2]/b")
//                        .imageTag() 상세 페이지로 들어가서 가지고 와야 한다.
                        .linkTag("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[contains(@class, 'swiper-slide pId')]/div/div[2]/div[1]/div[3]/div[1]/span/a[@class='listTit']")
                        .ratingTag("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[2]/div/div[2]/div[3]/")

                        .nextPageButtonXPath(getOasisNextPageButtonXpathForCurrentPage(currentPage))
                        .nextGroupButtonXPath("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@class='pgBtn_next']")
                        .build();

            default:
                throw new IllegalAccessException("Unsupported site: " + siteName);
        }
    }

}
