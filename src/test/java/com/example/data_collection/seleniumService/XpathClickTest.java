package com.example.data_collection.seleniumService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class XpathClickTest {
    WebDriver driver;
    WebDriverWait wait;

    String siteUrl = "https://www.ssg.com/disp/category.ssg?ctgId=6000093748";
    String detailTag = "//*[@id=\"pViewTab\"]/ul/li[2]/a[@class='tabBtn btn03']";

    //클릭 후 상품명이 존재하는 지 확인
    String assertTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody/tr[2]/td[3]";

    //이미지 태그
    String imgTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[@class='conts_productInfo_notice img']/div/img";

    @BeforeEach
    public void setUp(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\msong\\Desktop\\Bootcamp\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(siteUrl);
    }

    @Test
    public void navigateDetail(){

        String productListTag = "//*[@id=\"ty_thmb_view\"]/ul";

        String rowTag = productListTag + "/li";

        List<WebElement> rows = driver.findElements(By.xpath(rowTag));

        String linkTag = "./div[1]/div[2]/a";

        //상세페이지 link들을 가지고 와서 리스트에 저장.
        List<String> linkHrefs = new ArrayList<>();

        for(WebElement row: rows) {

            List<WebElement> links = row.findElements(By.xpath(linkTag));

            for (WebElement link : links) {
                linkHrefs.add(link.getAttribute("href"));
            }
        }

        //for문 돌리면서 하나를 실행시키고
        for(String href : linkHrefs){
            driver.navigate().to(href);

            //뒤로 돌아오기
            driver.navigate().back();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(rowTag)));

            System.out.println("--------------------");
        }
}

    @Test
    @DisplayName("Oasis: 제품상세페이지에서 상품상세정보 탭 클릭이 되는 지 확인")
    public void clickProductDetail(){
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(detailTag)));
        element.click();

        WebElement result = driver.findElement(By.xpath(assertTag));
        System.out.println(result.getText());
        assertThat(result.getText()).isEqualTo("백제원 한우사골 고기곰탕");
    }


    @Test
    @DisplayName("market Kurly: 다음페이지로 넘어가는 버튼이 클릭이 되는 지 확인")
    public void clickNextPage(){
        int currentPage = 1;
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(detailTag)));
        element.click();

        WebElement result = driver.findElement(By.xpath(assertTag));
        System.out.println(result.getText());
        assertThat(result.getText()).isEqualTo("백제원 한우사골 고기곰탕");
    }

    @Test
    @DisplayName("상품 이미지/링크 잘 가지고 오는지 테스트")
    public void testValidAttrXPath(){
        WebElement attrElement;
        try{
            attrElement = driver.findElement(By.xpath(imgTag));

        } catch (Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }

        String attr = attrElement.getAttribute("src");
        System.out.println("Extracted attribute: " + attr);
    }

    @AfterEach
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }
}
