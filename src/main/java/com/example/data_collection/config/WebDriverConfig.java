package com.example.data_collection.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Configuration
public class WebDriverConfig {

    @Bean
    public WebDriver webDriver(){
        Path path = Paths.get("./chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toAbsolutePath().toString());
        return new ChromeDriver(getOptions());
    }

    @Bean
    public WebDriverWait webDriverWait(WebDriver driver){
        return new WebDriverWait(driver, Duration.ofMinutes(5));
    }

    public ChromeOptions getOptions(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=ko_KR");
        //Chrome > 개발자 모드 > console > navigator.userAgent
        options.addArguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
        );
        return options;
    }
}
