package com.example.data_collection.service.FinalSeleniumService;

import com.example.data_collection.domain.entity.OasisDataEntity;
import com.example.data_collection.domain.entity.OasisDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(4)
public class OasisService extends CrawlingService{

    int currentPage;

    String siteUrl = "https://www.oasis.co.kr/product/list?categoryId=";

    String freshCode = "520";

    String sideDishCode = "123";

    String saladCode = "50";


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

    //영양성분 이미지
    String nutritionFactImgTag = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[@class='conts_productInfo_notice img']/div/img";


    //3. 테이블 안의 정보 가지고 오는 탭
    String tablePath = "//*[@id=\"pViewTabCon\"]/div[2]/div[3]/div[1]/table/tbody/tr[2]/td/table/tbody";

    String rowPath = tablePath + "/tr";

    //상품명, 용량/수량/크기, 영양성분 등
    String titleCellPath = "./td[1]/font/strong";

    //title cell의 값
    String valuePath ="./td[3]";


    //4. next button tag
    public String getNextPageButtonXpath(int currentPage){
        return String.format("//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@href = 'javascript:page(%d);']", currentPage+1);
    }

    String nextGroupTag = "//*[@id=\"sec_product_list\"]/div[4]/div[3]/a[@class='pgBtn_next']";
    private final OasisDataRepository repository;

    @Autowired
    public OasisService(WebDriver driver, WebDriverWait wait, OasisDataRepository repository){
        super(driver, wait);
        this.repository = repository;
    }

    @Override
    public void startCrawling(){
        List<String> categoryCodes = Arrays.asList(sideDishCode, saladCode);

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
//            getItemInfo();

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


    //title과 cell을 매핑 할 Map 객체 초기화
    Map<String, String> results = new HashMap<>();

    //제품 상세 탭의 제품상세테이블에서 항목의 유무를 확인 후 있는 경우 값을 가지고 오기.
    public void getItemInfoFromTable(){

        //테이블의 행들을 가지고 오기
        //테이블에 값이 존재 하는데 가지고 오지 못하는 경우가 생긴다.
        List<WebElement> rows = driver.findElements(By.xpath(rowPath));

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
                    System.out.println("No value nor title found: " + row.getText());
                }

            } catch (Exception e){
                throw new NoSuchElementException("찾는 요소가 없습니다.");
            }
        }

        for(String title : results.keySet()){
            System.out.println(title + ": " + results.get(title));
        }
    }

    private Double getRating(String input) {
        Pattern pattern = Pattern.compile("([0-9]*\\.?[0-9]+)");  // regex to extract floating point numbers
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }

    //제품 디테일 페이지에서 상품정보 가지고 오기
//    public void getItemInfo(){
//
//
//        OasisDataEntity.OasisDataBuilder builder = OasisDataEntity.builder();
//
//        WebElement salesName = getDataByCss(salesNameTag);
//        if(salesName != null) {
//            builder.salesName(salesName.getText());
//        } else {
//            builder.salesName(null);
//        }
//
//
//        WebElement actualPrice = getDataByCss(actualPriceTag);
//        if(actualPrice != null) {
//            //쉼표 제외하고 int형으로 변경
//            builder.actualPrice(Integer.parseInt(actualPrice.getText().replace(",", "")));
//        } else {
//            builder.actualPrice(0);
//        }
//
//
//        //쉼표 제외하고 int형으로 변경
//        WebElement discountPrice = getDataByXpath(discountPriceTag);
//        if(discountPrice != null) {
//            builder.discountPrice(Integer.parseInt(discountPrice.getText().replace(",", "")));
//        } else{
//            builder.discountPrice(0);
//        }
//
//        //%를 제외한 숫자만 남기기
//        WebElement discountRate = getDataByCss(discountRateTag);
//        if(discountRate != null){
//            builder.discountRate(Integer.parseInt(discountRate.getText().replace("%","")));
//        } else {
//            builder.discountRate(0);
//        }
//
//        //평점 문자열 처리
//        WebElement rating = getDataByClass(ratingTag);
//        if(rating != null){
//            builder.rating(getRating(rating.getText()));
//        } else{
//            builder.rating(0.0);
//        }
//
//        System.out.println(rating);
//
//        //이미지 가지고 오기
//        WebElement image = getDataByXpath(imageTag);
//        if(image != null){
//            String imageStr = image.getAttribute("src");
//            builder.image(imageStr);
//        } else {
//            builder.nutriImage(null);
//        }
//
//        WebElement nutritionFacts = getDataByXpath(nutritionFactImgTag);
//        if (nutritionFacts != null) {
//            String nutritionFactsImg = nutritionFacts.getAttribute("src");
//            builder.nutriImage(nutritionFactsImg);
//        } else {
//            builder.nutriImage(null);
//        }
//
//        //상세정보테이블에 있는 데이터 저장
//
//        //스크래핑한 데이터를 entity에 매핑해야 한다.
//        Map<String, String> titleMapping = new HashMap<>();
//        titleMapping.put("상품명", "productName");
//        titleMapping.put("용량/수량/크기", "serving");
//        titleMapping.put("원재료 및 함량", "ingredient");
//        titleMapping.put("영양성분", "NutriFacts");
//
//        getItemInfoFromTable();
//
//        for (Map.Entry<String, String> entry : titleMapping.entrySet()) {
//            String title = entry.getKey();
//            String builderMethod = entry.getValue();
//
//            String value = results.get(title);
//            if (value != null) {
//                try {
//                    Method method = builder.getClass().getMethod(builderMethod, String.class);
//                    method.invoke(builder, value);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
////        for(String title : results.keySet()) {
////            String column = titleMapping.get(title);
////            if(column != null) {
////                switch(column) {
////                    case "productName":
////                        oasisData.setProductName(results.get(title));
////                        break;
////                    case "serving":
////                        oasisData.setServing(results.get(title));
////                        break;
////                    case "ingredients":
////                        oasisData.setIngredients(results.get(title));
////                        break;
////                    case "NutriFacts":
////                        oasisData.setNutriFacts(results.get(title));
////                        break;
////                }
////            }
////        }
//        OasisDataEntity data = (OasisDataEntity) builder.build();
//        repository.save(data);
//
//    }







}
