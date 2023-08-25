package com.example.data_collection.seleniumService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class XPathTest {

    private WebDriver driver;

//    String siteUrl = "https://www.kurly.com/categories/912001";
    String siteUrl = "https://www.ssg.com/disp/category.ssg?ctgId=6000093748";


    @BeforeEach
    public void setUp(){
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\msong\\Desktop\\Bootcamp\\bitcamp\\Project KINNI\\data_collection\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(siteUrl);
    }

    @Test
    public void iframeTest(){

        WebElement attrElement;

        String attrXPath = "//*[@id=\"wrap_ifr\"]/div/div[3]/div/div/div[2]/img[contains(@alt, '품질표시이미지')]";

        //iframe name이나 id 쓰면 된다.
        driver.switchTo().frame("_ifr_html");

        try{
            attrElement = driver.findElement(By.xpath(attrXPath));

        } catch (Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }

        String attr = attrElement.getAttribute("src");
        System.out.println("Extracted attribute: " + attr);


    }
    @Test
    @DisplayName("상품 이미지/링크 잘 가지고 오는지 테스트")
    public void testValidAttrXPath(){

        String attrXPath = "//*[@id=\"ty_thmb_view\"]/ul/li[2]/div[1]/div[2]/a[@class='clickable']";

        WebElement attrElement;
        try{
            attrElement = driver.findElement(By.xpath(attrXPath));

        } catch (Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }

        String attr = attrElement.getAttribute("href");
        System.out.println("Extracted attribute: " + attr);
    }

    @Test
    @DisplayName("XPath로 웹 사이트에서 원하는 텍스트를 잘 가지고 오는 지 테스트")
    public void testValidTextXPath(){

        WebElement textElement;

        String textXPath = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[3]/dl/dd/div/a/div[1]/span[2]/em";

        try{
            //xpath로 찾는 경우
            textElement = driver.findElement(By.xpath(textXPath));

        } catch(Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }
        String text = textElement.getText();
        System.out.println("Extracted text: " + text);
    }

    @Test
    @DisplayName("ClassName 으로 웹 사이트에서 원하는 텍스트를 잘 가지고 오는 지 테스트")
    public void testValidTextClassName(){
        WebElement textElement;

        try{
            //클래스 이름으로 찾는 경우
            textElement = driver.findElement(By.className("ssg_price"));

        } catch(Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }
        String text = textElement.getText();
        System.out.println("Extracted text: " + text);
    }

    @Test
    @DisplayName("XPath로 웹 사이트에서 원하는 텍스트를 잘 가지고 오는 지 테스트")
    public void testValidTextCss(){
        WebElement textElement;
        String textCss = "em[class='ssg_price']";
        try{
            //css로 찾는 경우
            textElement = driver.findElement(By.cssSelector(textCss));
        } catch(Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }
        String text = textElement.getText();
        System.out.println("Extracted text: " + text);
    }



    @Test
    public void testElevenDiscountRateXPath(){

        String discountXPath = "";

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
