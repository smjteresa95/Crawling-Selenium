package com.example.data_collection.seleniumService;


import org.aspectj.lang.annotation.Before;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SSGCrawlerServiceTest {

    @Autowired
    private SSGCrawlerService ssgCrawlerService;

    @Autowired
    private WebDriverService webDriverService;


}
