package com.example.data_collection.seleniumService;

import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;



@Service
public class CrawlerService {


    private WebDriverService webDriver;

    private final List<Crawler> crawlerList;

    @Autowired
    public CrawlerService(List<Crawler> crawlerList){
        this.crawlerList = crawlerList;
    }

    @PostConstruct
    public void crawlProductBySite(){

        for(Crawler crawler : crawlerList){
            crawler.crawlAllProductsByCategory();
        }
    }

    @PreDestroy
    public void quitDriver(){
        if(webDriver.getDriver() != null){
            webDriver.getDriver().quit();
        }
    }

}
