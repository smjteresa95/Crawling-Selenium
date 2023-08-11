package com.example.data_collection.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.SSGRawData;
import com.example.data_collection.domain.entity.SSGRawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Order(2)
public class SSGCrawlerService extends BaseCrawler<SSGRawData, Long> {

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
    public SSGCrawlerService(SSGRawDataRepository SSGRawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.SSGRawDataRepository = SSGRawDataRepository;
    }


    @Override
    protected SSGRawData createRawDataInstance() {
        return new SSGRawData();
    }

    @Override
    protected SSGRawDataRepository getRawDataRepository(){
        return SSGRawDataRepository;
    }

    @Override
    protected double getDiscountRate(int index, List<WebElement> discountRates) {
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
    protected double getRating(int index, List<WebElement> ratings, JavascriptExecutor js) {
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
    public void crawlAllProductsByCategory(){

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