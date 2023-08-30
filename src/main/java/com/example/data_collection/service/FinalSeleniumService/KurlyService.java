package com.example.data_collection.service.FinalSeleniumService;

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
@Order(3)
public class KurlyService extends CrawlingService{

    int currentPage;

    String siteUrl = "https://www.kurly.com/categories/";

    String allCode = "911";

    String soupCode = "911001";

    String mealkitCode = "911006";

    String sideCode = "911002";

    //디테일 페이지로 넘어가기 전, 리스트 있는 부분에서 이미지 링크를 가지고 와야한다.
    //상품이미지
    String imageTag ="//*[@id=\"container\"]/div/div[2]/div[2]/a[contains(@class, 'css-9o2zup')]/div[1]/div/span/img";

    String linkTag = "//*[@id=\"container\\\"]/div/div[2]/div[2]/a[contains(@class, 'css-9o2zup')]";

    //1. 디테일 페이지에서의 상품 정보 가지고 오는 태그
    //웹사이트에서 띄워주는 상품명 - Use By.xpath
    String salesNameTag = "//*[@id=\"product-atf\"]/section/div[2]/div/h1";

    //일반가 - Use By.xpath
    String actualPriceTag = "//*[@id=\"product-atf\"]/section/span";

    //할인가 - 할인이 적용 되지 않는 상품의 경우 일반가와 동일
    String discountPriceTag = "//*[@id=\"product-atf\"]/section/h2/span[2]";

    //할인률 - Use By.xpath
    String discountRateTag = "//*[@id=\"product-atf\"]/section/h2/span[1]";

   //마켓컬리는 평점이 없음.



    //2. 상품상세정보탭에서 정보 가지고 오는 태그

    //상세 정보 탭
    //상세정보 탭 들어갈 필요 없음
    String detailTabTag = "//*[@id=\"top\"]/div[3]/div[2]/nav/ul/li[2]/a/span";

    //영양성분 이미지
    String nutritionFactImgTag = "//*[@id=\"detail\"]/div[2]/img[@alt='자세히보기 이미지']";


    //3. next button tag
    public String getNextPageButtonXpath(int currentPage){
        return "//*[@id='container']/div[2]/div[2]/div[3]/a["+(currentPage+2)+"]";
    }
    String nextGroupTag = "//*[@id=\"container\"]/div[2]/div[2]/div[3]/a[13]/img";


    @Autowired
    public KurlyService(WebDriver driver, WebDriverWait wait){
        super(driver, wait);
    }

    public void startCrawling(){
        List<String> categoryCodes = Arrays.asList(allCode);

        for(String code : categoryCodes) {
            driver.get(siteUrl + code);

            currentPage = 1;

            while(true){
                crawlDetailPage();

                try {
                    try {
                            moveToNextPageOrGroup(nextGroupTag);
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
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
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





}
