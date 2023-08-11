package com.example.data_collection.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.ElevenStRawData;
import com.example.data_collection.domain.entity.ElevenStRawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(1)
public class ElevenStCrawlerService extends BaseCrawler<ElevenStRawData, Long> {

    private String mealKitcode = "1129418";

    private static final int CURRENT_PAGE = 1;

    private static final String SITE_NAME = "eleven";

    private final ElevenStRawDataRepository elevenStRawDataRepository;

    ElevenStCrawlerService(ElevenStRawDataRepository elevenStRawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.elevenStRawDataRepository = elevenStRawDataRepository;
    }

    @Override
    protected ElevenStRawData createRawDataInstance() {
        return new ElevenStRawData();
    }

    @Override
    protected ElevenStRawDataRepository getRawDataRepository() {
        return elevenStRawDataRepository;
    }

    @Override
    protected double getDiscountRate(int index, List<WebElement> discountRates) {
        if(index < discountRates.size() && discountRates.get(index) != null) {
            String discountText = discountRates.get(index).getText();
            discountText = discountText.replace("최저가", "").replace("%", "").trim();
            return Double.parseDouble(discountText);

        } else {
            return 0.0;
        }
    }

    @Override
    protected double getRating(int index, List<WebElement> ratings, JavascriptExecutor js) {

        Pattern pattern = Pattern.compile("([0-9]*[0-9])?[0-9]+");
        Matcher matcher = pattern.matcher(ratings.get(index).getText());

        if(index >= ratings.size()) {
            return 0.0; // 또는 다른 적절한 기본값 반환
        }

        //문자열에서 숫자와 소숫점 패턴을 검색하여 해당 부분만 추출 한 후, 그 값을 double형으로 바꾼다.
        if(index < ratings.size() && ratings.get(index) != null && matcher.find()){
            String extracted = matcher.group();
            double value = Double.parseDouble(extracted);
            return value;
        }
        return 0.0;
    }

    @Override
    public void crawlAllProductsByCategory() {

        List<String> categoryCodes = Arrays.asList(mealKitcode);

        for(String code : categoryCodes){
            driver.get(tag.getSiteUrl() + code);

            currentPage = 1;

            while(true){
                try {
                    crawlProductsAndSave();
                } catch (NoMorePagesException e){
                    break;
                }
            }

        }
    }

}
