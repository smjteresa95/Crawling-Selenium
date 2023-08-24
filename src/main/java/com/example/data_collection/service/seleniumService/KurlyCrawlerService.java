package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.KurlyRawData;
import com.example.data_collection.domain.entity.KurlyRawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import com.example.data_collection.service.WebDriverService;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Order(4)
public class KurlyCrawlerService extends BaseCrawler<KurlyRawData, Long> {

    String soupCode = "911001";

    private final KurlyRawDataRepository kurlyRawDataRepository;

    private static final String SITE_NAME = "kurly";
    private static final int CURRENT_PAGE = 1;

    KurlyCrawlerService(KurlyRawDataRepository kurlyRawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.kurlyRawDataRepository = kurlyRawDataRepository;
    }

    @Override
    KurlyRawData createRawDataInstance() {
        return new KurlyRawData();
    }

    @Override
    JpaRepository<KurlyRawData, Long> getRawDataRepository() {
        return kurlyRawDataRepository;
    }

    @Override
    double getRating(int index, List<WebElement> ratings, JavascriptExecutor js) {
        return 0;
    }

    @Override
    String getBrand(int index, List<WebElement> brands) {
        return null;
    }

    @Override
    public void startCrawlingAllProducts() {

        List<String> categoryCodes = Arrays.asList(soupCode);
        for(String code : categoryCodes) {
            driver.get(tag.getSiteUrl() + code);
        }
        while (true){
            try {
                crawlProductsAndSave();
            } catch (NoMorePagesException e){
                break;
            }
        }
    }
}
