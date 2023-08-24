package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.HtmlTagConfig;
import com.example.data_collection.service.WebDriverService;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestCrawlingService {

    String siteUrl = "https://www.oasis.co.kr/product/list?categoryId=246";
    String linkTag = "//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[contains(@class, 'swiper-slide pId')]/div/div[2]/div[1]/div[3]/div[1]/span/a[@class='listTit']";

    //1. 디테일 페이지에서의 상품 정보 가지고 오는 태그
    //웹사이트에서 띄워주는 상품명 - Use By.cssSelector
    String salesNameTag = "div.textSubject > h1";

    //일반가 - Use By.cssSelector
    String actualPriceTag = "dd[class*='dr']";

    //할인가 - 할인이 적용 되지 않는 상품의 경우 일반가와 동일
    String discountPriceTag = "//*[@id=\"sec_product_view\"]/div[2]/div/div[2]/div[2]/dl/div[1]/dd[2]/span/b";

    //할인률 - Use By.cssSelector
    String discountRateTag = "strong[class*='dr']";

    //평점 - Use By.class
    String ratingTag = "detailStar";

    //상품이미지
    String imageTag ="//*[@id=\"sec_product_view\"]/div[2]/div/div[1]/div[1]/img";


    //2. 상품상세정보탭에서 정보 가지고 오는 태그

    //상세 정보 탭
    String detailTabTag = "//*[@id=\"pViewTab\"]/ul/li[2]/a[@class='tabBtn btn03']";

    //기준으로 잡을 상품명
    String itemNameTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody/tr[2]/td[3]";

    //용량/수량
    String servQtyTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody/tr[6]/td[3]";

    //영양성분 이미지
    String nutritionFactImgTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[@class='conts_productInfo_notice img']/div/img";

    WebDriverService webDriverService;

    WebDriver driver;

    WebDriverWait wait;

    @Autowired
    public TestCrawlingService(WebDriverService webDriverService){
        this.webDriverService = webDriverService;
        driver = webDriverService.getDriver();
        wait = webDriverService.getWait();
    }

    @PostConstruct
    public void startCrawling(){
        crawlDetailPage();
    }


    //제품 디테일 페이지로 이동
    public void crawlDetailPage(){

        driver.get(siteUrl);

        //상세페이지 link들을 가지고 와서 리스트에 저장.
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
        List<String> linkHrefs = new ArrayList<>();

        for(WebElement link : links){
            linkHrefs.add(link.getAttribute("href"));
        }

        //for문 돌리면서 하나를 실행시키고

        for(String href : linkHrefs){
            driver.navigate().to(href);

            //제품 상세 탭에 들어가기
            openDetailTab();

            //디테일 페이지에 있는 상품정보 가지고 오기
            getItemInfo();

            getItemInfoFromTable();

            //뒤로 돌아오기
            driver.navigate().back();

            System.out.println("--------------------");
        }
    }

    //제품 상세 탭에 들어가기
    public void openDetailTab(){
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(detailTabTag)));
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", element);
        } catch (NoSuchElementException e){
            System.out.println("상품상세정보 탭을 찾을 수 없습니다.");
        }
    }

    //제품 상세 탭의 제품상세테이블에서 항목의 유무를 확인 후 있는 경우 값을 가지고 오기.
    public void getItemInfoFromTable(){
        String tablePath = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody";

        String rowPath = tablePath + "/tr";

        //상품명, 용량/수량/크기, 영양성분 등
        String titleCellPath = "./td[1]/font/strong";

        //title cell의 값
        String valuePath ="./td[3]";


        //테이블의 행들을 가지고 오기
        //테이블에 값이 존재 하는데 가지고 오지 못하는 경우가 생긴다.
        List<WebElement> rows = driver.findElements(By.xpath(rowPath));

        //title과 cell을 매핑 할 Map 객체 초기화
        Map<String, String> results = new HashMap<>();

        //테이블 안에서 가지고 와야 할 항목들의 리스트
        List<String> titles = Arrays.asList("상품명", "용량/수량/크기", "원재료 및 함량", "영양성분");

        for(WebElement row : rows){
            //변수를 밖에 먼저 정의해줬더니 서버가 종료되는 건 막을 수 있었다.
            String title;
            String value;

            try {

                //다행히 테이블에 있는 데이터는 전부 Xpath로 가지고 올 수 있다.
                try {
                    title = row.findElement(By.xpath(titleCellPath)).getText();
//                    System.out.println(title);

                    //찾고자 하는 항목이 존재 하면 값도 가지고 오기
                    if(titles.contains(title)) {

                    value = row.findElement(By.xpath(valuePath)).getText();
//                    System.out.println(value);

                    results.put(title, value);
                    }
                } catch(NoSuchElementException e){
                    System.out.println("Hey, No value nor title found: " + row.getText());
                }

            } catch (Exception e){
                throw new NoSuchElementException("찾는 요소가 없습니다.");
            }
        }

        for(String title : results.keySet()){
            System.out.println(title + ": " + results.get(title));
        }
    }

    //제품 디테일 페이지에서 상품정보 가지고 오기
    public void getItemInfo(){
        String salesName = getDataByCss(salesNameTag).getText();
        System.out.println(salesName);

        String actualPrice =  getDataByCss(actualPriceTag).getText();
        System.out.println(actualPrice);

        String discountPrice = getDataByXpath(discountPriceTag).getText();
        System.out.println(discountPrice);

        String discountRate = getDataByCss(discountRateTag).getText();
        System.out.println(discountRate);

        String rating = getDataByClass(ratingTag).getText();
        System.out.println(rating);

        //이미지 가지고 오기
        WebElement image = getDataByXpath(imageTag);
        if(image != null){
            String imageStr = image.getAttribute("src");
            System.out.println(imageStr);
        } else {
            System.out.println("상품이미지를 찾을 수 없습니다.");
        }

        WebElement nutritionFacts = getDataByXpath(nutritionFactImgTag);
        if (nutritionFacts != null) {
            String nutritionFactsImg = nutritionFacts.getAttribute("src");
            System.out.println(nutritionFactsImg);
        } else {
            System.out.println("성분이미지를 찾을 수 없습니다.");
        }
    }

    public WebElement getDataByXpath(String tag){
        try {
            return driver.findElement(By.xpath(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다:" + tag);
            return null;
        }
    }

    public WebElement getDataByCss(String tag){
        try {
            return driver.findElement(By.cssSelector(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다:" + tag);
            return null;
        }
    }

    public WebElement getDataByClass(String tag){
        try {
            return driver.findElement(By.className(tag));
        } catch(NoSuchElementException e){
            System.out.println("요소를 찾지 못했습니다: " + tag);
            return null;
        }
    }

}
