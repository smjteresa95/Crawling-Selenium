package com.example.data_collection.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class WebDriverConfig {

    @Bean
    public WebDriver webDriver(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\msong\\Desktop\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        return new ChromeDriver(getOptions());
    }

    @Bean
    public WebDriverWait webDriverWait(WebDriver driver){
        return new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public ChromeOptions getOptions(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=ko_KR");
        options.addArguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
        return options;
    }
}
