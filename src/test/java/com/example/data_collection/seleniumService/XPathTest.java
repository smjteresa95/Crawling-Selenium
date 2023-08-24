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

public class XPathTest {

    private WebDriver driver;

//    String siteUrl = "https://www.kurly.com/categories/912001";
    String siteUrl = "https://www.oasis.co.kr/product/detail/77645-1088094?categoryId=246";

    String textXPath = "//*[@id=\"sec_product_view\"]/div[2]/div/div[2]/div[@class=\"infoBadges\"]/h1";

    String attrXPath = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[2]/div[@class='conts_productInfo_notice img']/img";
    String discountXPath = "//*[@id=\"container\"]/div[2]/div[2]/div[3]/a[13]/img[contains(@alt,'다음 페이지')]";


    @BeforeEach
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\msong\\Bootcamp\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(siteUrl);
    }

    @Test
    @DisplayName("상품 이미지/링크 잘 가지고 오는지 테스트")
    public void testValidAttrXPath(){
        WebElement attrElement;
        try{
            attrElement = driver.findElement(By.xpath(attrXPath));

        } catch (Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }

        String attr = attrElement.getAttribute("src");
        System.out.println("Extracted attribute: " + attr);
    }

    @Test
    @DisplayName("웹 사이트에서 원하는 텍스트를 잘 가지고 오는 지 테스트")
    public void testValidTextXPath(){
        WebElement textElement;
        try{
            //xpath로 찾는 경우
//            textElement = driver.findElement(By.xpath(textXPath));

            //클래스 이름으로 찾는 경우
//            textElement = driver.findElement(By.className("detailStar"));

            //css로 찾는 경우
            textElement = driver.findElement(By.cssSelector("div.textSubject > h1"));
        } catch(Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }
        String text = textElement.getText();
        System.out.println("Extracted text: " + text);
    }



    @Test
    public void testElevenDiscountRateXPath(){

        driver.get(siteUrl);
        WebElement discountRate = null;
        String discountText = "";
        try{
            discountRate = driver.findElement(By.xpath(discountXPath));
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
