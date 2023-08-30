package com.example.data_collection.service.seleniumsearchservice;

import com.example.data_collection.config.SearchHtmlTagConfig;
import com.example.data_collection.config.SearchHtmlTagConfigFactory;
import com.example.data_collection.domain.searchentity.BaseSearchRawData;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

abstract public class BaseSearchCrawler<T extends BaseSearchRawData, ID extends Serializable> implements SearchCrawler{

    SearchHtmlTagConfigFactory htmlTagFactory;
    SearchHtmlTagConfig tag;
    WebDriver driver;
    WebDriverWait wait;
    String siteName;


     BaseSearchCrawler(SearchHtmlTagConfigFactory htmlTag, WebDriver driver, String siteName)
             throws IllegalAccessException {

        this.siteName = siteName;

        this.htmlTagFactory = htmlTag;
        this.tag = htmlTag.getHtmlTagForSite(siteName);

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    abstract T createRawDataInstance();

    abstract JpaRepository<T, ID> getRawDataRepository();

    abstract double getRating(int index, List<WebElement> ratings, JavascriptExecutor js);

    public int getPrice(int index, List<WebElement> prices){
        if(index < prices.size() && prices.get(index) != null) {
            String priceStr = prices.get(index).getText().replace(",", "");
            return Integer.parseInt(priceStr);

        } else {
            return 0;
        }
    }


    public String getCategoryName(){
        WebElement category = driver.findElement(By.xpath(tag.getCategoryNameTag()));
        return category.getText();
    }


    public void waitForFiveSeconds(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void searchProduct(String productName){
        //get product name from ApiRawData and type it into the input tag.
        driver.findElement(By.xpath(tag.getSearchTag())).sendKeys(productName);
        //click search button
        driver.findElement(By.xpath(tag.getSearchButtonTag())).click();
        // Clear field to empty it from any previous data
        driver.findElement(By.xpath(tag.getSearchTag())).clear();
    }

    public void crawlCurrentPage(String productNameTag, String brandTag, String priceTag, String imageTag,
                                 String linkTag, String ratingTag, String imageAttr, String linkAttr){

        JpaRepository<T, ID> rawDataRepository = getRawDataRepository();

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));
        List<WebElement> images = driver.findElements(By.xpath(imageTag));
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
        List<WebElement> ratings = driver.findElements(By.xpath(ratingTag));

        int size = productNames.size();

        List<String> imgSrcs = new ArrayList<>();
        for(WebElement image : images){
            imgSrcs.add(image.getAttribute(imageAttr));
        }

        List<String> linkHrefs = new ArrayList<>();
        for(WebElement link : links){
            linkHrefs.add(link.getAttribute(linkAttr));
        }

        //rating 가지고 오기 위함(자식 요소의 텍스트를 제외하고 특정요소의 텍스트만 가지고 오기 위해서 JavaScriptExecutor 사용)
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for(int i=0; i< size; i++){
            T rawData = createRawDataInstance();

            rawData.setProductName(i < productNames.size() ? productNames.get(i).getText() : "");
            rawData.setBrand(i < brands.size() ? brands.get(i).getText() : "");
            rawData.setImage(i < imgSrcs.size() ? imgSrcs.get(i) : "");
            rawData.setProductLink(i < linkHrefs.size() ? linkHrefs.get(i) : "");

            //price 처리 후 저장
            rawData.setPrice(i < prices.size() ? getPrice(i, prices) : 0);

            //rating 처리 후 저장
            rawData.setRating(i < ratings.size() ? getRating(i, ratings, js) : 0);

            rawData.setCategoryName(getCategoryName());

            rawDataRepository.save(rawData);
            System.out.println(rawData);

        }
        waitForFiveSeconds();
    }

}
