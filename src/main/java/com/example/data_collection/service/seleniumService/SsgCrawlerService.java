package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.SSGRawData;
import com.example.data_collection.domain.entity.SSGRawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import com.example.data_collection.service.WebDriverService;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

//지정해준 카테고리 내의 모든 상품 정보 크롤링해서 DB에 저장
@Service
@Order(1)
public class SsgCrawlerService extends BaseCrawler<SSGRawData, Long> {

    //밀키트
    private String mealKitCode = "6000139913";

    //냉장/냉동식품
    private String frozenCode = "6000139879";

    //도시락/델리
    private String deliCode = "6000139914";

    private static final int CURRENT_PAGE = 1;

    private static final String SITE_NAME = "ssg";

    private final SSGRawDataRepository SSGRawDataRepository;


    @Autowired
    public SsgCrawlerService(SSGRawDataRepository SSGRawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.SSGRawDataRepository = SSGRawDataRepository;
    }


    @Override
    SSGRawData createRawDataInstance() {
        return new SSGRawData();
    }

    @Override
    SSGRawDataRepository getRawDataRepository(){
        return SSGRawDataRepository;
    }

    @Override
    double getDiscountRate(int index, List<WebElement> discountRates) {
        if(index < discountRates.size() && discountRates.get(index) != null) {
            String discountText = discountRates.get(index).getText().replace("%", "").trim();
            try{
                return Double.parseDouble(discountText);
            }catch(NumberFormatException e){
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    @Override
    double getRating(int index, List<WebElement> ratings, JavascriptExecutor js) {
        if(index < ratings.size() && ratings.get(index) != null) {
            String ratingStr = (String) js.executeScript("return arguments[0].childNodes[arguments[0].childNodes.length-1].textContent.trim()", ratings.get(index));
            Double rating = 0.0;
            try {
                rating = Double.parseDouble(ratingStr);
            } catch (NumberFormatException | NullPointerException e) {
                // ignore, since the default value is already set to 0.0
            }
            return rating;
        } else {
            return 0.0;
        }
    }

    @Override
    public void startCrawlingAllProducts(){

        List<String> categoryCodes = Arrays.asList(mealKitCode);

        for (String code : categoryCodes) {

            driver.get(tag.getSiteUrl() + code);

            currentPage = 1;

            while(true) {
                try {
                    crawlProductsAndSave();
                } catch (NoMorePagesException e){
                    break;
                }
            }
        }
    }


}