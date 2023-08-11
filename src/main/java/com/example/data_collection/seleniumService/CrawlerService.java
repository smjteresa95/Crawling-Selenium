package com.example.data_collection.seleniumService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class CrawlerService {


    private WebDriverService webDriver;

    private final List<Crawler> crawlerList;

    @Autowired
    public CrawlerService(List<Crawler> crawlerList, WebDriverService webDriver){
        this.crawlerList = crawlerList;
        this.webDriver = webDriver;
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
