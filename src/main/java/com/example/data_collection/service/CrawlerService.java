package com.example.data_collection.service;

import com.example.data_collection.service.FinalSeleniumService.Crawling;
import com.example.data_collection.service.seleniumsearchservice.SearchCrawler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class CrawlerService {

    WebDriver driver;
    List<Crawling> crawlingList;

    //FinalSeleniumService 패키지 안의 @PostConstruct 붙은 메서드들 차례대로 실행
    @Autowired
    public CrawlerService(List<Crawling> crawlingList){
        this.crawlingList = crawlingList;
    }

    @PostConstruct
    public void crawlProductBySite() throws InterruptedException {

        for(Crawling crawling : crawlingList){
            crawling.startCrawling();
        }
    }


    //Selenium 으로 검색해서 나온 값 DB에 저장하게 끔 하는 메서서드들 실행.
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
        if(driver != null){
            driver.quit();
        }
    }

}
