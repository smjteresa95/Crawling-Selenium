package com.example.data_collection.seleniumService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class XPathTest {

    private WebDriver driver;

    String siteUrl = "https://www.kurly.com/goods/1000146000";



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
    @DisplayName("상품 이미지 잘 가지고 오는지 테스트")
    public void testValidAttrXPath(){

        String attrXPath = "//*[@id=\"detail\"]/div[2]/img[@alt='자세히보기 이미지']";


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
    @DisplayName("상품 디테일 링크로 잘 이동하면서 왔다갔다 잘 하는지 테스트")
    public void testNavigateToLink(){

        String attrXPath = "//*[@id=\"container\"]/div/div[2]/div[2]/a[contains(@class, 'css-9o2zup')]";

        List<WebElement> attrElements;
        try{
            attrElements = driver.findElements(By.xpath(attrXPath));

        } catch (Exception e){
            throw new NoSuchElementException("Element not found with the provided XPath");
        }

        for(WebElement attrElement : attrElements) {
            String attr = attrElement.getAttribute("href");
            System.out.println("Extracted attribute: " + attr);

            driver.navigate().to(attr);

//            driver.navigate().back();
        }


    }

    @Test
    @DisplayName("XPath로 웹 사이트에서 원하는 텍스트를 잘 가지고 오는 지 테스트")
    public void testValidTextXPath(){

        WebElement textElement;

        String textXPath = "//*[@id=\"product-atf\"]/section/h2/span[1]";

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
