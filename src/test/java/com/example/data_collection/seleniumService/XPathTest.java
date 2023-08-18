package com.example.data_collection.seleniumService;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.asynchttpclient.util.Assertions.assertNotNull;

public class XPathTest {

    private WebDriver driver;

    String siteUrl = "https://www.wingeat.com/side-nav?pathname=/saving";

    String xPath = "//*[@id=\"app-template\"]/div[1]/div/div[3]/ul/li[2]/div/div[1]/span";

    @BeforeEach
    public void setUp(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\msong\\Desktop\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        driver = new ChromeDriver();
    }

//    @Test
//    public void testVaildAttriXPath(){
//        driver.get(siteUrl);
//        WebElement attriElement = null;
//        try{
//            attriElement = driver.findElement(By.xpath(xPath));
//
//        } catch (Exception e){
//
//        }
//
//        assertNotNull(attriElement, "Element not found with the provided XPath");
//
//        String attri = attriElement.getAttribute("href");
//        System.out.println("Extracted attribute: " + attri);
//    }

    @Test
    public void testValidTextXPath(){
        driver.get(siteUrl);
        WebElement textElement = null;
        try{
            textElement = driver.findElement(By.xpath(xPath));
        } catch(Exception e){

        }

        assertNotNull(textElement, "Element not found with the provided XPath");
        String text = textElement.getText();
        System.out.println("Extracted text: " + text);
    }


    @Test
    public void testElevenDiscountRateXPath(){

        String xPath = "//*/li[contains(@id, 'thisClick_')]/div/div[3]/div[1]/span[2]/span[@class='sale']";

        driver.get(siteUrl);
        WebElement discountRate = null;
        String discountText = "";
        try{
            discountRate = driver.findElement(By.xpath(xPath));
            discountText = discountRate.getText();

            discountText = discountText.replace("최저가", "").replace("%", "").trim();

        } catch (Exception e){

        }

        System.out.println(discountText);


    }

    @AfterEach
    public void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

}
