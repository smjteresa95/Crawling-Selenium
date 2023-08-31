package com.example.data_collection.service.seleniumservice;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public abstract class BaseCrawler implements Crawler {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Autowired
    protected BaseCrawler(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofMinutes(5));
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
