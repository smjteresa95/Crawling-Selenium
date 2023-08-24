package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.OasisRawData;
import com.example.data_collection.domain.entity.OasisRawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import com.example.data_collection.service.WebDriverService;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Order(1)
public class OasisCrawlerService extends BaseCrawler<OasisRawData, Long>{

    private final OasisRawDataRepository rawDataRepository;
    String snackCode = "25";
    private static final int CURRENT_PAGE = 1;
    private static final String SITE_NAME = "oasis";

    OasisCrawlerService(OasisRawDataRepository rawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.rawDataRepository = rawDataRepository;
    }

    @Override
    OasisRawData createRawDataInstance() {
        return new OasisRawData();
    }

    @Override
    JpaRepository<OasisRawData, Long> getRawDataRepository() {
        return rawDataRepository;
    }

    @Override
    String getBrand(int index, List brands) {
        return null;
    }

    @Override
    double getRating(int index, List ratings, JavascriptExecutor js) {
        return 0;
    }


    @Override
    public void startCrawlingAllProducts() {
        List<String> categoryCodes = Arrays.asList(snackCode);

        for(String code : categoryCodes){
            driver.get(tag.getSiteUrl() + code);

            currentPage = 1;

            while(true){
                try {
                    try {
                        // If the current page is a multiple of 10, move to the next group
                        if (currentPage % 10 == 0) {
                            goToNextGroup(); // Click on the 'next' button to move to the next group
                            waitForFiveSeconds();
                        }
                        // For all other pages, move to the next page within the group
                        else {
                            goToNextPageWithinGroup(); // Click on the 'currentPage + 1' button to move to the next page within the group
                            waitForFiveSeconds();
                        }

                    } catch (Exception e){
                        throw new NoMorePagesException("Unexpected error: " + e.getMessage());
                    }
                } catch (NoMorePagesException e){
                    break;
                }
            }

        }

    }
}
