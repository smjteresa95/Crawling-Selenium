package com.example.data_collection.service.seleniumsearchservice;

import com.example.data_collection.config.SearchHtmlTagConfigFactory;
import com.example.data_collection.domain.PublicDataRepository;
import com.example.data_collection.domain.searchentity.SearchSsgRawData;
import com.example.data_collection.domain.searchentity.SearchSsgRawDataRepository;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Order(2)
public class SsgSearchCrawlerService extends BaseSearchCrawler<SearchSsgRawData, Long> {

    private final SearchSsgRawDataRepository searchSsgRawDataRepository;

    private final PublicDataRepository publicDataRepository;


    private static final String SITE_NAME = "ssg";

    @Autowired
    public SsgSearchCrawlerService(SearchSsgRawDataRepository searchSsgRawDataRepository,
                                   SearchHtmlTagConfigFactory htmlTag,
                                   WebDriver driver,
                                   PublicDataRepository publicDataRepository) throws IllegalAccessException {
        super(htmlTag, driver, SITE_NAME);
        this.searchSsgRawDataRepository = searchSsgRawDataRepository;
        this.publicDataRepository = publicDataRepository;
    }

    @Override
    protected SearchSsgRawData createRawDataInstance() {
        return new SearchSsgRawData();
    }

    @Override
    protected JpaRepository<SearchSsgRawData, Long> getRawDataRepository() {
        return searchSsgRawDataRepository;
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
