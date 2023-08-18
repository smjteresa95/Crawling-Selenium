package com.example.data_collection.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
public class SearchHtmlTagConfigFactory {

    public SearchHtmlTagConfig getHtmlTagForSite(String siteName) throws IllegalAccessException {

        switch (siteName){
            case "ssg":
                return SearchHtmlTagConfig.builder()
                        .siteHome("https://shinsegaemall.ssg.com/")
                        .searchTag("//*[@id=\"ssg-query\"]")
                        .searchButtonTag("//*[@id=\"ssg-query-btn\"]/i")

                        .productNameTag("//*/li[contains(@id, 'item_unit_')]/div[2]/div[2]/div/a/em[@class=\"\"]")
                        .brandTag("//*/li[contains(@id, 'item_unit_')]/div[2]/div[2]/div/strong/em")

                        .priceTag("//*/li[contains(@id, 'item_unit_')]/div[2]/div[3]/div/em")
                        .imageTag("//*/li[contains(@id, 'item_unit_')]/div[1]/div[2]/a")
                        .linkTag("//*/li[contains(@id, 'item_unit_')]/div[2]/div[2]/div/a")
                        .ratingTag("//*/li[contains(@id, 'item_unit_')]/div[2]/div[5]/div/div/span/span")

                        .imageAttribute("src")
                        .linkAttribute("href")
                        .build();

            case "eleven":
                return SearchHtmlTagConfig.builder()
                        .siteHome("https://www.11st.co.kr/main")
                        .searchTag("//*[@id=\"tSearch\"]/form/fieldset/input")
                        .searchButtonTag("//*[@id=\"tSearch\"]/form/fieldset/button")

                        .productNameTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[1]/ul/li[1]/div/div[2]/div[1]/a/strong")

                        .priceTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[1]/ul/li[1]/div/div[2]/div[2]/dl/dd/span[1]")
                        .imageTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[1]/ul/li[1]/div/div[1]/a/img")
                        .linkTag("//*[@id=\"layBodyWrap\"]/div/div/div[3]/div/section[1]/ul/li[1]/div/div[2]/div[1]/a")
                        .ratingTag("")

                        .imageAttribute("src")
                        .linkAttribute("href")
                        .build();

            default:
                throw new IllegalAccessException("Unsupported site: " + siteName);
        }
    }
}
