package com.example.data_collection.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class XpathButtonTest {

    private WebDriver driver;

    String siteUrl = "https://www.oasis.co.kr/product/list?categoryId=25";
    int currentPage;

    //*[@id="container"]/div/div[2]/div[3]/a[3]
    public String getNextPageButtonXpath(int currentPage){
        return String.format("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@href = 'javascript:page(%d);']", currentPage+1);
    }

    @BeforeEach
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\msong\\Bootcamp\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(siteUrl);
    }

    @Test
    @DisplayName("다음 페이지 버튼 클릭 후 올바른 페이지로 이동하는지 확인")
    public void testIfNextButtonWorks(){

        WebElement buttonElement;
        currentPage = 1;

        for(int i=0; i<2; i++) {
            try {
                buttonElement = driver.findElement(By.xpath(getNextPageButtonXpath(currentPage)));
                buttonElement.click();

                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath(getNextPageButtonXpath(currentPage + 1))
                        ));

                String currentUrl = driver.getCurrentUrl();

                assertTrue(currentUrl.contains(String.valueOf(currentPage + 1)),
                        "Expected to be on page " + (currentPage + 1) + " but was on " + currentUrl);
                currentPage++;

            } catch (Exception e) {
                throw new NoSuchElementException("Element not found with the provided XPath");
            }
        }

    }

    @AfterEach
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

}
