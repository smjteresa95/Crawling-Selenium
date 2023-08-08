package com.example.data_collection.seleniumService;

import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;


@Service
public class CrawlerService {

    int currentPage = 1;
    private WebDriver driver;
    private WebDriverWait wait;
    private final RawDataRepository rawDataRepository;


    private String pagingTag = "//div[@class='com_paginate notranslate']";


    //제품 전체정보를 포함하는 상위요소
    private String productTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_maininfo\"]";
    private String productNameTag = productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_tit\"]";
    private String brandTag = productTag + "/a/div[@class=\"mnsditem_tit\"]/span[@class=\"mnsditem_goods_brand\"]";
    private String priceTag = productTag + "/a/div[@class=\"mnsditem_pricewrap\"]/div[@class=\"mnsditem_price_row mnsditem_ty_newpr\"]/div[@class=\"new_price\"]/em[@class=\"ssg_price\"]";
    private String imageTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_thmb\"]/a/div[@class=\"mnsditem_thmb_imgbx\"]/img[@class='i1']";
    private String linkTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_thmb\"]/a[@class=\"mnsditem_thmb_link clickable\"]";
    private String ratingTag = "//div[@class=\"mnsditem_goods\"]/div[@class=\"mnsditem_detail\"]/div[@class=\"mnsditem_sideinfo\"]/div[@class=\"mnsditem_review\"]/div[@class='mnsditem_review_score']";
    private String categoryNameTag = "//*[@id=\"area_disp_ctg_title\"]/h2/a[@class=\"notranslate clickable\"]";


    //밀키트
    private String mealKitCode = "6000139913";

    //냉장/냉동식품
    private String frozenCode = "6000139879";

    //도시락/델리
    private String deliCode = "6000139914";

    private String ssgurl = "https://shinsegaemall.ssg.com/disp/category.ssg?dispCtgId=";

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
    public void startCrawling(){
        crawlAllProductsByCategory();
    }

    public void crawlAllProductsByCategory(){

        List<String> categoryCodes = Arrays.asList(mealKitCode, frozenCode, deliCode);

        for (String code : categoryCodes) {

            driver.get(ssgurl+code);
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

    public void crawlProductsAndSave() {

            crawlCurrentPage(productNameTag, brandTag, priceTag, imageTag, linkTag, ratingTag);

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



    public String getCategoryName(){
        WebElement category = driver.findElement(By.xpath(categoryNameTag));
        return category.getText();
    }

    public void crawlCurrentPage(String productNameTag, String brandTag, String priceTag, String imageTag, String linkTag, String ratingTag){

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));
        List<WebElement> images = driver.findElements(By.xpath(imageTag));
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
        List<WebElement> ratings = driver.findElements(By.xpath(ratingTag));

        List<Integer> sizes = Arrays.asList(productNames.size(), brands.size(), prices.size(), images.size(), links.size(), ratings.size());
        int minSize = Collections.min(sizes);

        List<String> imgSrcs = new ArrayList<>();
        for(WebElement image : images){
            imgSrcs.add(image.getAttribute("src"));
        }

        List<String> linkHrefs = new ArrayList<>();
        for(WebElement link : links){
            linkHrefs.add(link.getAttribute("href"));
        }

        //rating 가지고 오기 위함(자식 요소의 텍스트를 제외하고 특정요소의 텍스트만 가지고 오기 위해서 JavaScriptExecutor 사용)
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for(int i=0; i< minSize; i++){
            RawData rawData = new RawData();

            rawData.setProductName(productNames.get(i).getText());
            rawData.setBrand(brands.get(i).getText());
            rawData.setPrice(prices.get(i).getText());
            rawData.setImage(imgSrcs.get(i));
            rawData.setProductLink(linkHrefs.get(i));

            rawData.setCategoryName(getCategoryName());

            //rating
            String ratingStr = (String) js.executeScript("return arguments[0].childNodes[arguments[0].childNodes.length-1].textContent.trim()", ratings.get(i));
            Double rating = 0.0;
            try {
                rating = Double.parseDouble(ratingStr);
            } catch (NumberFormatException | NullPointerException e) {
                // Ignore, the default value is already set to 0
            }
            rawData.setRating(rating);

            save(rawData);
        }
        waitForFiveSeconds();
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
            throw new NoMorePagesException("No more page to navigate");
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
            throw new NoMorePagesException("No more page to navigate");
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
