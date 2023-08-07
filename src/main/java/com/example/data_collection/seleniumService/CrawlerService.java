package com.example.data_collection.seleniumService;

import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;



@Service
public class CrawlerService {

    int currentPage = 1;
    private WebDriver driver;
    private WebDriverWait wait;
    private final RawDataRepository rawDataRepository;


    private String pagingTag = "//div[@class='com_paginate notranslate']";
    private String currentPageTag = pagingTag + "//strong[@title='현재위치']";



    //제품 전체정보를 포함하는 상위요소
    private String mealkitProductTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]";
    private String mealkitProductNameTag = mealkitProductTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_tit\"]";
    private String mealkitBrandTag = mealkitProductTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_brand\"]";
    private String mealkitPriceTag = mealkitProductTag + "/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row mnsditem_ty_newpr\"]/div[@class=\"new_price\"]/em[@class=\"ssg_price\"]";
    private String imageTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_thmb\"]/a/div[@class=\"mnsditem_thmb_imgbx\"]/img[@class='i1']";


    //밀키트
    private String mealKitUrl = "https://shinsegaemall.ssg.com/disp/category.ssg?dispCtgId=6000139913";

    //냉장/냉동식품
    private String frozenUrl = "https://shinsegaemall.ssg.com/disp/category.ssg?dispCtgId=6000139879";

    @Autowired
    public CrawlerService(WebDriver driver, WebDriverWait wait, RawDataRepository rawDataRepository){
        this.driver = driver;
        this.wait = wait;
        this.rawDataRepository=rawDataRepository;
    }

    public void save(RawData rawData){
        rawDataRepository.save(rawData);
    }


    @PostConstruct
    public void crawlAllProductsAndSave() {

        driver.get(frozenUrl);


        while (true) {
            crawlCurrentPage(mealkitProductNameTag, mealkitBrandTag, mealkitPriceTag, imageTag);

            // If the current page is a multiple of 10, move to the next group
            if (currentPage % 10 == 0) {
                moveOnToNextGroup(); // Click on the 'next' button to move to the next group
                waitForFiveSeconds();
            }
            // For all other pages, move to the next page within the group
            else {
                moveToNextPageWithinGroup(); // Click on the 'currentPage + 1' button to move to the next page within the group
                waitForFiveSeconds();
            }
        }
    }


    private boolean isLastPageWithinGroup(){
        WebElement currentPageElement = driver.findElement(By.xpath(currentPageTag));
        int currentPageNumber = Integer.parseInt(currentPageElement.getText());
        return currentPageNumber % 10 == 0;
    }


    public void crawlCurrentPage(String productNameTag, String brandTag, String priceTag, String imageTag){

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));
        List<WebElement> images = driver.findElements(By.xpath(imageTag));

//        int minSize = Math.min(Math.min(productNames.size(), brands.size()), prices.size());

        int minSize = Math.min(Math.min(Math.min(productNames.size(), brands.size()), prices.size()), images.size());

        List<String> imgSrcs = new ArrayList<>();
        for(WebElement image : images){
            imgSrcs.add(image.getAttribute("src"));
        }

        for(int i=0; i< minSize; i++){
            RawData rawData = new RawData();

            rawData.setProductName(productNames.get(i).getText());
            rawData.setBrand(brands.get(i).getText());
            rawData.setPrice(prices.get(i).getText());
            rawData.setImage(imgSrcs.get(i));

            save(rawData);
        }
        waitForFiveSeconds();
    }


    //다음 버튼이 있는지 확인하는 로직추가
    public boolean isNextButtonExistWithinGroup(){

        try {
            String nextPageButtonXPath = "//div[@class='com_paginate notranslate']//a[contains(text(), '" + currentPage + "')]";
            driver.findElement(By.xpath(nextPageButtonXPath));
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
    }

    public boolean isNextButtonExist() {
        try {
            driver.findElement(By.xpath(pagingTag + "//a[@class='btn_next on' and @title='다음']"));
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
    }


    public void moveOnToNextGroup() {

        String pageButtonXPath = pagingTag + "//a[@class='btn_next on' and @title='다음']";

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pageButtonXPath)));

            WebElement pageButton = driver.findElement(By.xpath(pageButtonXPath));
//            pageButton.click();
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", pageButton);

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );

            currentPage++;

        } catch (TimeoutException e){
            System.out.println("No more page to navigate");
        }


    }


    public void moveToNextPageWithinGroup(){

        String nextPageButtonXPath = "//div[@class='com_paginate notranslate']//a[contains(text(), '" + (currentPage+1) + "')]";

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextPageButtonXPath)));

            WebElement pageButton = driver.findElement(By.xpath(nextPageButtonXPath));
            //        pageButton.click();
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", pageButton);


            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );

            currentPage++;

        } catch (TimeoutException e){
            System.out.println("No more page to navigate");
        }
    }

    public void waitForFiveSeconds(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    @PreDestroy
    public void quitDriver(){
        if(driver != null){
            driver.quit();
        }
    }




}
