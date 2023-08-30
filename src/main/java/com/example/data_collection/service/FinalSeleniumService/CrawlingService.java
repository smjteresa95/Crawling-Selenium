package com.example.data_collection.service.FinalSeleniumService;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public abstract class CrawlingService implements Crawling{

    WebDriver driver;
    WebDriverWait wait;


//    HtmlConfigFactory htmlFactory;
//    HtmlConfig tag;
//    String siteName;

//    public void reloadHtmlConfig(){
//        try{
//            this.tag = htmlFactory.getTagForSite(siteName, currentPage);
//
//        } catch (IllegalAccessException e){
//            e.printStackTrace();
//        }
//    }

    @Autowired
    CrawlingService(WebDriver driver, WebDriverWait wait){

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public WebElement getDataByXpath(String tag){
        try {
            return driver.findElement(By.xpath(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다:" + tag);
            return null;
        }
    }

    public WebElement getDataByCss(String tag){
        try {
            return driver.findElement(By.cssSelector(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다:" + tag);
            return null;
        }
    }

    public WebElement getDataByClass(String tag){
        try {
            return driver.findElement(By.className(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다: " + tag);
            return null;
        }
    }

}
