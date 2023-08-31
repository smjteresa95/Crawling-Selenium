package com.example.data_collection.service.seleniumservice;

import com.example.data_collection.config.HtmlConfig;
import com.example.data_collection.config.HtmlConfigFactory;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KurlyCrawlerService extends BaseCrawler {

    int currentPage = 1;

    String soupCode = "911001";

    String mealkitCode = "911006";

    String sideCode = "911002";

    HtmlConfig tag;

    @Autowired
    public KurlyCrawlerService(WebDriver driver, HtmlConfigFactory htmlFactory) throws IllegalAccessException {
        super(driver);
        this.tag = htmlFactory.getTagForSite("kurly");
    }

    public void startCrawling(){
        List<String> categoryCodes = Arrays.asList(soupCode);

        for(String code : categoryCodes) {
            driver.get(tag.getSiteUrl() + code);

            currentPage = 1;

            while(true){
//                crawlDetailPage();

                try {
                    try {
                        moveToNextPageOrGroup(tag.getNextPageButton());
                        Thread.sleep(2000);

                    } catch (NoMorePagesException e) {
                        System.out.println("넘어갈 페이지가 없습니다.");
                        break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ex){
                    ex.printStackTrace();
                }
            }


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

        //상세페이지 link들을 가지고 와서 리스트에 저장.
        List<WebElement> links = driver.findElements(By.xpath(tag.getLink()));
        List<String> linkHrefs = new ArrayList<>();

        for(WebElement link : links){
            linkHrefs.add(link.getAttribute("href"));
        }

        //for문 돌리면서 하나를 실행시키고

        for(String href : linkHrefs){
            driver.navigate().to(href);

            //디테일 페이지에 있는 상품정보 가지고 오기
            getItemInfo();

//            getItemInfoFromTable();

            //뒤로 돌아오기
            driver.navigate().back();

            System.out.println("--------------------");
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
    public void getItemInfo() {

        String salesName = getDataByXpath(tag.getSalesName()).getText();
        System.out.println(salesName);

        try {
            WebElement actualPrice = getDataByXpath(tag.getActualPrice());
            if (actualPrice != null) {
                System.out.println(actualPrice.getText());
            } else {
                System.out.println("일반가 없음");
            }
        }catch(Exception e){
            throw new NullPointerException();
        }

        try {
            WebElement discountPrice = getDataByXpath(tag.getDiscountPrice());
            if (discountPrice != null) {
                System.out.println(discountPrice.getText());
            } else {
                System.out.println("할인가 없음");
            }
        } catch (Exception e){
            throw new NullPointerException();
        }

        try {
            WebElement discountRate = getDataByXpath(tag.getDiscountRate());
            if (discountRate != null) {
                System.out.println(discountRate.getText());
            } else {
                System.out.println("할인률 없음");
            }
        } catch (Exception e){
            throw new NullPointerException();
        }

        //이미지 가지고 오기
        WebElement image = getDataByXpath(tag.getImage());
        if (image != null) {
            String imageStr = image.getAttribute("src");
            System.out.println(imageStr);
        } else {
            System.out.println("상품이미지를 찾을 수 없습니다.");
        }

        WebElement nutritionFacts = getDataByXpath(tag.getNutriImage());
        if (nutritionFacts != null) {
            String nutritionFactsImg = nutritionFacts.getAttribute("src");
            System.out.println(nutritionFactsImg);
        } else {
            System.out.println("성분이미지를 찾을 수 없습니다.");
        }
    }







}