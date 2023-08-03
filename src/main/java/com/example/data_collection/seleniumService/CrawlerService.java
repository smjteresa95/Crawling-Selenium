package com.example.data_collection.seleniumService;

import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class CrawlerService {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final RawDataRepository rawDataRepository;

    private int targetPageNumber = 2;

    private String mealKitUrl = "https://shinsegaemall.ssg.com/disp/category.ssg?dispCtgId=6000139913";

    //제품 전체정보를 포함하는 상위요소
    private String productTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]";

    private String productNameTag = productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_tit\"]";

    private String brandTag = productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_brand\"]";

    private String priceTag = productTag + "/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row mnsditem_ty_newpr\"]/div[@class=\"new_price\"]/em[@class=\"ssg_price\"]";


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
    public void crawlAllProductsAndSave(){

        driver.get(mealKitUrl);

        List<WebElement> pages = driver.findElements(By.xpath("//div[@class='com_paginate notranslate']//a"));
        int totalPages = pages.size();

        do{
            crawlCurrentPageData();

            moveOnToNextPage();


        } while(targetPageNumber <= totalPages);


    }

    public void crawlCurrentPageData(){

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));

        for(int i=0; i< productNames.size(); i++){
            RawData rawData = new RawData();

            rawData.setProductName(productNames.get(i).getText());
            rawData.setBrand(brands.get(i).getText());
            rawData.setPrice(prices.get(i).getText());

            save(rawData);
        }
    }

    public void moveOnToNextPage() {

        String nextPageButtonTag = "//div[@class='com_paginate notranslate']//a[contains(text(), '" + targetPageNumber + "')]";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextPageButtonTag)));

        WebElement pageLink = driver.findElement(By.xpath(nextPageButtonTag));
        pageLink.click();

        targetPageNumber++;

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
