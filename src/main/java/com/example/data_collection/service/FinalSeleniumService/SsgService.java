package com.example.data_collection.service.FinalSeleniumService;

import com.example.data_collection.domain.entity.SsgDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Order(2)
public class SsgService extends CrawlingService{


    int currentPage;

    String siteUrl = "https://www.ssg.com/disp/category.ssg?ctgId=";

    String koreanSnackCode = "6000093748";

    String coffeeCode = "6000092905";



    //1. 디테일 페이지에서의 상품 정보 가지고 오는 태그
    //웹사이트에서 띄워주는 상품명 - Use By.Xpath
    String salesNameTag = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[2]/h2/span/span[@class='cdtl_info_tit_txt']";

    //일반가 - Use By.Xpath
    String actualPriceTag = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_old_price']/em[@class='ssg_price']";

    //할인가 - 할인이 적용 되지 않는 상품의 경우 일반가와 동일 - Use By.Xpath
    String discountPriceTag = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_new_price notranslate']/em[@class='ssg_price']";

    //할인률 - Use By.Xpath
    String discountRateTag = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[4]/div[2]/div/span[@class='cdtl_new_price notranslate']/em[@class='ssg_percent']";

    //평점 - Use By.Xpath
    String ratingTag = "//*[@id=\"content\"]/div[2]/div[1]/div[2]/div[3]/dl/dd/div/a/div[1]/span[2]/em";

    //상품이미지
    String imageTag ="//*[@id=\"mainImg\"]";


    //2. 상품상세정보탭에서 정보 가지고 오는 태그 iframe 안에 있어서 따로 처리를 해줘야 한다.

    //영양성분 이미지
    String nutritionFactImgTag = "//*[@id=\"wrap_ifr\"]/div/div[@Class='cdtl_tmpl_guide']/div/div/div[2]/img[contains(@alt, '품질표시이미지')]";


    //3. next button tag
    public String getNextPageButtonXpath(int currentPage){
        return "//div[@class='com_paginate notranslate']//a[contains(text(), '" + (currentPage + 1) + "')]";
    }

    String nextGroupTag = "//div[@class='com_paginate notranslate']//a[@class='btn_next on' and @title='다음']";


    private final SsgDataRepository repository;

    @Autowired
    public SsgService(WebDriver driver, WebDriverWait wait, SsgDataRepository repository){
        super(driver, wait);
        this.repository = repository;
    }

    @Override
    public void startCrawling(){
        List<String> categoryCodes = Arrays.asList(coffeeCode, koreanSnackCode);

        for(String code : categoryCodes) {
            driver.get(siteUrl + code);

            currentPage = 1;

            while(true){
                try {
                    crawlAllProducts();
                } catch (NoMorePagesException e){
                    System.out.println("넘어갈 페이지가 없습니다.");
                    break;
                }
            }

        }
}



    public void crawlAllProducts() {

        try {

            try {
                crawlDetailPage();
            } catch (Exception e) {
                throw new NoMorePagesException("Unexpected error: " + e.getMessage());
            }

            // If the current page is a multiple of 10, move to the next group
            if (currentPage % 10 == 0) {
                moveToNextPageOrGroup(nextGroupTag); // Click on the 'next' button to move to the next group
            }
            // For all other pages, move to the next page within the group
            else {
                moveToNextPageOrGroup(getNextPageButtonXpath(currentPage)); // Click on the 'currentPage + 1' button to move to the next page within the group
            }

        } catch (Exception e){
            throw new NoMorePagesException("Unexpected error: " + e.getMessage());
        }
    }

    //페이지 넘어가는 부분

    //파라미터에 따라서, 다음페이지로 넘어갈 수도, 다음 그룹으로 넘어갈 수도 있다.
    public void moveToNextPageOrGroup(String nextButtonXPath){
        try {
            WebElement pageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextButtonXPath)));
            pageButton.click();

            currentPage++;

        } catch (TimeoutException e){
            throw new NoMorePagesException("No more page to navigate");
        }
    }

    //제품 디테일 페이지로 이동
    public void crawlDetailPage(){

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

            //디테일 페이지에 있는 상품정보 가지고 오기
            getItemInfo();

            //상품필수정보 표에 있는 값 가지고 오기
            getItemInfoFromTable();

            //뒤로 돌아오기
            driver.navigate().back();

            System.out.println("--------------------");
        }
    }


    //제품 상세 탭의 제품상세테이블에서 항목의 유무를 확인 후 있는 경우 값을 가지고 오기.
    public void getItemInfoFromTable(){
        String tablePath = "//div[@class='cdtl_sec']/div[@class='cdtl_cont_info']//table/tbody";

        String rowPath = tablePath + "/tr";

        //상품명, 용량/수량/크기, 영양성분 등,
        String titleCellPath = "./th/div[@class='in']";

        //title cell의 값
        String valuePath ="./td/div[@class='in']";


        //테이블의 행들을 가지고 오기
        //테이블에 값이 존재 하는데 가지고 오지 못하는 경우가 생긴다.
        List<WebElement> rows = driver.findElements(By.xpath(rowPath));

        //title과 cell을 매핑 할 Map 객체 초기화
        Map<String, String> results = new HashMap<>();

        //테이블 안에서 가지고 와야 할 항목들의 리스트
        List<String> titles = Arrays.asList("품명 및 모델명", "포장 단위별 내용물의 용량 (중량), 수량, 크기", "주원료/함량(원료 원산지)", "영양성분");

        for(WebElement row : rows){
            //변수를 밖에 먼저 정의해줬더니 서버가 종료되는 건 막을 수 있었다.
            String title;
            String value;

            try {

                //다행히 테이블에 있는 데이터는 전부 Xpath로 가지고 올 수 있다.
                try {
                    title = row.findElement(By.xpath(titleCellPath)).getText();


                    //찾고자 하는 항목이 존재 하면 값도 가지고 오기
                    if(titles.contains(title)) {

                        value = row.findElement(By.xpath(valuePath)).getText();

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
        String salesName = getDataByXpath(salesNameTag).getText();
        System.out.println(salesName);

        WebElement actualPrice = getDataByXpath(actualPriceTag);
        if(actualPrice != null) {
            System.out.println(actualPrice.getText());
        } else {
            System.out.println("일반가 없음");
        }

        WebElement discountPrice = getDataByXpath(discountPriceTag);
        if(discountPrice != null) {
            System.out.println(discountPrice.getText());
        } else {
            System.out.println("할인가 없음");
        }

        WebElement discountRate = getDataByXpath(discountRateTag);
        if(discountRate != null) {
            System.out.println(discountRate.getText());
        } else {
            System.out.println("할인률 없음");
        }

        String rating = getDataByXpath(ratingTag).getText();
        System.out.println(rating);

        //이미지 가지고 오기
        WebElement image = getDataByXpath(imageTag);
        if(image != null){
            String imageStr = image.getAttribute("src");
            System.out.println(imageStr);
        } else {
            System.out.println("상품이미지를 찾을 수 없습니다.");
        }

        //성분이미지 찾기
        driver.switchTo().frame("_ifr_html");
        WebElement nutritionFacts = getDataByXpath(nutritionFactImgTag);
        if (nutritionFacts != null) {
            String nutritionFactsImg = nutritionFacts.getAttribute("src");
            System.out.println(nutritionFactsImg);
        } else {
            System.out.println("성분이미지를 찾을 수 없습니다.");
        }
        driver.switchTo().defaultContent();
    }



}
