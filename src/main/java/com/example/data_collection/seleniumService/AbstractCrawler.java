package com.example.data_collection.seleniumService;

import com.example.data_collection.config.HtmlTagConfig;
import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.BaseRawData;
import com.example.data_collection.domain.DataSaver;
import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.exception.NoMorePagesException;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.tokens.Token;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractCrawler<T extends BaseRawData, ID extends Serializable> implements Crawler {

    protected final HtmlTagConfigFactory htmlTagFactory;
    protected WebDriverService webDriverService;
    protected final WebDriver driver;
    protected WebDriverWait wait;
    protected HtmlTagConfig tag;

    protected String siteName;
    protected int currentPage;

    private int totalPagePerGroup = 10;

    @Setter
    protected DataSaver<T> dataSaver;


    AbstractCrawler(HtmlTagConfigFactory htmlTag, WebDriverService webDriverService, String siteName, int currentPage) throws IllegalAccessException {

        this.siteName = siteName;
        this.currentPage = currentPage;

        this.htmlTagFactory = htmlTag;
        this.tag = htmlTag.getHtmlTagForSite(siteName, currentPage);

        this.webDriverService = webDriverService;
        this.driver = webDriverService.getDriver();
        this.wait = webDriverService.getWait();
    }


    protected abstract T createRawDataInstance();

    protected abstract JpaRepository<T, ID> getRawDataRepository();


    public void reloadHtmlConfig(){
        try{
            this.tag = htmlTagFactory.getHtmlTagForSite(siteName, currentPage);

        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }


    public void waitForFiveSeconds(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getCategoryName(){
        WebElement category = driver.findElement(By.xpath(tag.getCategoryNameTag()));
        return category.getText();
    }

    public void moveToNextPageOrGroup(String nextButtonXPath){
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextButtonXPath)));


            WebElement pageButton = driver.findElement(By.xpath(nextButtonXPath));

            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", pageButton);

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );

            currentPage++;
            reloadHtmlConfig();

        } catch (TimeoutException e){
            throw new NoMorePagesException("No more page to navigate");
        }
    }


    public void goToNextGroup() {
        String nextGroupButtonXPath = tag.getNextGroupButtonXPath();
        moveToNextPageOrGroup(nextGroupButtonXPath);

    }


    public void goToNextPageWithinGroup(){
        String nextPageButtonXPath = tag.getNextPageButtonXPath();
        moveToNextPageOrGroup(nextPageButtonXPath);
    }


    public void crawlCurrentPage(String productNameTag, String brandTag, String priceTag, String discountRateTag, String imageTag, String linkTag, String ratingTag, String imageAttr, String linkAttr){

        JpaRepository<T, ID> rawDataRepository = getRawDataRepository();

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));
        List<WebElement> images = driver.findElements(By.xpath(imageTag));
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
        List<WebElement> ratings = driver.findElements(By.xpath(ratingTag));
        List<WebElement> discountRates= driver.findElements(By.xpath(discountRateTag));

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
            rawData.setPrice(i < prices.size() ? prices.get(i).getText() : "");
            rawData.setImage(i < imgSrcs.size() ? imgSrcs.get(i) : "");
            rawData.setProductLink(i < linkHrefs.size() ? linkHrefs.get(i) : "");

            // discount rate 처리
            if(i < discountRates.size() && discountRates.get(i) != null) {
                rawData.setDiscountRate(discountRates.get(i).getText());
            } else {
                rawData.setDiscountRate("0%");
            }

            rawData.setCategoryName(getCategoryName());

            //rating 처리
            if(i < ratings.size() && ratings.get(i) != null) {
                String ratingStr = (String) js.executeScript("return arguments[0].childNodes[arguments[0].childNodes.length-1].textContent.trim()", ratings.get(i));
                Double rating = 0.0;
                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException | NullPointerException e) {
                    // ignore, since the default value is already set to 0.0
                }
                rawData.setRating(rating);
            } else {
                rawData.setRating(0.0);
            }

            rawDataRepository.save(rawData);
            System.out.println(rawData.toString());

        }
        waitForFiveSeconds();
    }

    public void crawlProductsAndSave() {

        try {

            try {
                crawlCurrentPage(tag.getProductNameTag(), tag.getBrandTag(), tag.getPriceTag(), tag.getDiscountRateTag(), tag.getImageTag(), tag.getLinkTag(), tag.getRatingTag(), tag.getImageAttribute(), tag.getLinkAttribute());

            } catch (Exception e){
                e.printStackTrace();
            }

            // If the current page is a multiple of 10, move to the next group
            if (currentPage % totalPagePerGroup == 0) {
                goToNextGroup(); // Click on the 'next' button to move to the next group
                waitForFiveSeconds();

            }
            // For all other pages, move to the next page within the group
            else {
                goToNextPageWithinGroup(); // Click on the 'currentPage + 1' button to move to the next page within the group
                waitForFiveSeconds();

            }

        } catch (Exception e){
            e.printStackTrace();
            throw new NoMorePagesException("Unexpected error: " + e.getMessage());
        }
    }



}
