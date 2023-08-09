package com.example.data_collection.seleniumService;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Getter
public class WebDriverService {
    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    public WebDriverService(WebDriver driver, WebDriverWait wait){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @PreDestroy
    public void quitDriver(){
        if(driver != null){
            driver.quit();
        }
    }
}
