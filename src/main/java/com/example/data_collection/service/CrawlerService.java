package com.example.data_collection.service;

import com.example.data_collection.service.seleniumService.Crawler;
import com.example.data_collection.service.seleniumsearchservice.SearchCrawler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class CrawlerService {


    WebDriverService webDriver;

    List<Crawler> crawlerList;

    @Autowired
    public CrawlerService(List<Crawler> crawlerList, WebDriverService webDriver){
        this.crawlerList = crawlerList;
        this.webDriver = webDriver;
    }

    @PostConstruct
    public void crawlProductBySite(){

        for(Crawler crawler : crawlerList){
            crawler.startCrawlingAllProducts();
        }
    }

//    private final List<SearchCrawler> crawlerList;
//
//    @Autowired
//    public CrawlerService(List<SearchCrawler> crawlerList, WebDriverService webDriver){
//        this.crawlerList = crawlerList;
//        this.webDriver = webDriver;
//    }
//
//    @PostConstruct
//    public void searchThenCrawlProducts(){
//        for(SearchCrawler crawler : crawlerList){
//            crawler.searchThenCrawlProductInfo();
//        }
//    }

    @PreDestroy
    public void quitDriver(){
        if(webDriver.getDriver() != null){
            webDriver.getDriver().quit();
        }
    }

}
