package com.example.data_collection.service.seleniumsearchservice;

import com.example.data_collection.config.SearchHtmlTagConfigFactory;
import com.example.data_collection.domain.PublicDataRepository;
import com.example.data_collection.domain.searchentity.SearchElevenStRawData;
import com.example.data_collection.domain.searchentity.SearchElevenStRawDataRepository;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(1)
public class ElevenStSearchCrawlerService extends BaseSearchCrawler<SearchElevenStRawData, Long> {

    private final SearchElevenStRawDataRepository searchElevenStRawDataRepository;

    private final PublicDataRepository publicDataRepository;

    private static final String SITE_NAME = "eleven";

    @Autowired
    ElevenStSearchCrawlerService(SearchHtmlTagConfigFactory htmlTag,
                                 SearchElevenStRawDataRepository searchElevenStRawDataRepository,
                                 WebDriver driver,
                                 PublicDataRepository publicDataRepository) throws IllegalAccessException {
        super(htmlTag, driver, SITE_NAME);
        this.searchElevenStRawDataRepository = searchElevenStRawDataRepository;
        this.publicDataRepository = publicDataRepository;
    }

    @Override
    SearchElevenStRawData createRawDataInstance() {
        return new SearchElevenStRawData();
    }

    @Override
    SearchElevenStRawDataRepository getRawDataRepository() {
        return searchElevenStRawDataRepository;
    }


    @Override
    double getRating(int index, List<WebElement> ratings, JavascriptExecutor js) {
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
    public void searchProduct(String productName){

        //get product name from ApiRawData and type it into the input tag.
        driver.findElement(By.xpath(tag.getSearchTag())).sendKeys(productName);

        //click search button
        driver.findElement(By.xpath(tag.getSearchButtonTag())).click();

        // Clear field to empty it from any previous data
        // clear() method won't work at this site.
        driver.findElement(By.xpath(tag.getSearchTag())).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));

    }

    @Override
    public void searchThenCrawlProductInfo() {
        //get product name to search, put them into the list.
        List<String> apiProductNameList = publicDataRepository.findAllProductNames();

        driver.get(tag.getSiteHome());

        for(String productName : apiProductNameList){

            searchProduct(productName);

            //검색결과가 존재하면 크롤링해서 DB에 값 저장하고, 그렇지 않으면 넘기기.
            try {
                crawlCurrentPage(tag.getProductNameTag(), tag.getBrandTag(), tag.getPriceTag(), tag.getImageTag(), tag.getLinkTag(), tag.getRatingTag(), tag.getImageAttribute(), tag.getLinkAttribute());
            } catch(Exception e) {
                System.out.println("Product not found");
                e.printStackTrace();
            }

        }

    }
}
