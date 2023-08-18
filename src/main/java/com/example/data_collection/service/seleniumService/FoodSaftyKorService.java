package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.WebDriverConfig;
import com.example.data_collection.exception.NoMorePagesException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.List;

@Service
@Order(3)
public class FoodSaftyKorService implements Crawler{

    WebDriver driver;
    WebDriverWait wait;

    private int totalPagePerGroup = 10;

    public String getNextPageButtonTag(int currentPage){
        return "//*[@id=\"listForm\"]/div[@id=\"ajaxPaging\"]/a[contains(@href, 'fnSearch(" + (currentPage + 1) + ")')]";
    }
    private String nextGroupButtonTag = "//*[@id=\"ajaxPaging\"]/a[@class=\"btn_next\"]";



    private String foodSaftyKorSiteUrl = "https://various.foodsafetykorea.go.kr/nutrient/detail/search/list.do";
    private String processedFoodTag = "//*[@id=\"listForm\"]/div[2]/div[1]/label[4]/i";


    //단백질
    private String proteinTagInList1 = "//*[@id=\"nutri1List\"]/li[3]/a";
    //지방
    private String fatTagInList2 = "//*[@id=\"nutri2List\"]/li[4]/a";
    //탄수화물
    private String carbohydrateTagInList3 = "//*[@id=\"nutri3List\"]/li[6]/a";


    //상용제품 선택하기 위한 태그
    private String labelTag = "//*[@id=\"searchDbClassCd\"]";
    private String commercialProductTag = "//*[@id=\"searchDbClassCd\"]/option[3]";


    //식품군 선택하기 위한 태그
    private String searchFoodGroupButtonTag = "//*[@id=\"listForm\"]/div[3]/ul[1]/li[3]/a/span";
    private String firstClassif = "//*[@id=\"depth1List\"]/li[2]/a[contains(text(), '과자류, 빵류 또는 떡류')]";
    private String secondClassif = "//*[@id=\"depth2List\"]/li[2]/a[contains(text(), '과자')]";
    private String applyButtonTag = "//*[@id=\"listForm\"]/div[2]/a";


    //상품 디테일로 넘어가기 위한 태그
    private String productDetailTag = "//*[@id=\"nextDataBody\"]/tr[1]/td[2]/a[contains(@onClick, 'fnObj.fnDetail')]";


    //검색 버튼
    private String searchButtonTag = "//*[@id=\"searchBtn\"]/span";

    @Autowired
    public FoodSaftyKorService(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }


    public WebElement waitUntilElementVisible(String xPath){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
    }


    public void waitForFiveSeconds(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startCrawlingAllProducts() {

        driver.get(foodSaftyKorSiteUrl);
        selectCategoriesThenSearch();

        int currentPage = 1;

        while (true) {

            //화면이 다 로딩될 때까지 기다리기
            waitUntilElementVisible(productDetailTag);

            try {
                //TODO
                //페이지 로딩 될 때까지 기다리게 하기. 지금은 20초 강제로 기다리게 하고 있음.

                Thread.sleep(20000);
                crawlCurrentPage();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            try {
                //페이지 차례대로 넘어가게 하는 메서드
                try {
                    if (currentPage % totalPagePerGroup == 0) {
                        moveToNextPageOrGroup(nextGroupButtonTag);
                        wait.until(
                                driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")
                        );
                        currentPage++;
                    } else {
                        moveToNextPageOrGroup(getNextPageButtonTag(currentPage));
                        wait.until(
                                driver ->((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete")
                        );
                        currentPage++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NoMorePagesException("Unexpected error: " + e.getMessage());
                }
            } catch (NoMorePagesException e) {
                break;
            }
        }
    }

    // 찾고자 하는 제품의 카테고리 선택
    public void selectCategoriesThenSearch(){

        //'가공식품' 클릭
        driver.findElement(By.xpath(processedFoodTag)).click();
        //'단백질' 클릭
        driver.findElement(By.xpath(proteinTagInList1)).click();
        //'지방' 클릭
        driver.findElement(By.xpath(fatTagInList2)).click();
        //'탄수화물' 클릭
        driver.findElement(By.xpath(carbohydrateTagInList3)).click();

        //레이블에서 '상용제품'클릭
        driver.findElement(By.xpath(labelTag)).click();
        driver.findElement(By.xpath(commercialProductTag)).click();

        //식품군 선택
        driver.findElement(By.xpath(searchFoodGroupButtonTag)).click();

        try {
            waitForFiveSeconds();
            //'iframe'의 id속성을 사용하여 포커스 이동
            driver.switchTo().frame("jqxIframe1");

            driver.findElement(By.xpath(firstClassif)).click();

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(secondClassif))).click();
            driver.findElement(By.xpath(applyButtonTag)).click();

        }catch(Exception e){
            e.printStackTrace();
        }

        //메인컨텐츠로 포커스 돌리기
        driver.switchTo().defaultContent();

        //검색버튼 클릭
        driver.findElement(By.xpath(searchButtonTag)).click();
        waitForFiveSeconds();

    }




    public void crawlCurrentPage(){

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='bbsList nextList']/tbody[@id='nextDataBody']/tr[10]")));
        List<WebElement> rows = driver.findElements(By.xpath("//*/table[@class=\"bbsList nextList\"]/tbody[@id=\"nextDataBody\"]/tr"));

        for(WebElement row : rows){
            System.out.println("-----------------------------");

            //제조사와 상품명 모두 불러오기
            WebElement aElement =  row.findElement(By.xpath(".//td[@class='tal']/a"));
            String aText = aElement.getText();

            //제조사
            WebElement makerElement = row.findElement(By.xpath(".//td[@class='tal']/a/strong"));
            String maker = makerElement.getText();
            System.out.println(maker);

            //상품명
            String name = aText.replace(maker, "").trim();
            System.out.println(name);

            //식품분류
            WebElement groupElement = row.findElement(By.xpath(".//td[4]"));
            String group = groupElement.getText();
            System.out.println(group);

            //식품상세분류
            WebElement subGroupElement = row.findElement(By.xpath(".//td[5]"));
            String subGroupFullText = subGroupElement.getText();
            String[] subGroup = subGroupFullText.split("\n");

            //식품상세분류명
            String subGroupName = subGroup[0].trim();
            System.out.println(subGroupName);

            //식품상세분류코드
            String subGroupCode = subGroup[1].trim();
            System.out.println(subGroupCode);

            //1회 제공량
            WebElement servingSizeElement = row.findElement(By.xpath(".//td[7]"));
            String servingSize = servingSizeElement.getText();
            System.out.println(servingSize);

            //영양성분 3가지
            WebElement nutrition1Element = row.findElement(By.xpath(".//td[8]"));
            String nutrition1 = nutrition1Element.getText();
            System.out.println(nutrition1);

            WebElement nutrition2Element = row.findElement(By.xpath(".//td[9]"));
            String nutrition2 = nutrition2Element.getText();
            System.out.println(nutrition2);

            WebElement nutrition3Element = row.findElement(By.xpath(".//td[10]"));
            String nutrition3 = nutrition3Element.getText();
            System.out.println(nutrition3);


        }
    }

    public void getNutritionDetail(){

        WebElement servingElement = driver.findElement(By.xpath("//*/div[@id=\"container\"]/div[@id=\"content\"]/div[@data-skin-food=\"table\"]/dl[2]"));

        //총내용량
        WebElement totalServingElement = servingElement.findElement(By.xpath(".//dd[1]"));
        String totalServing = totalServingElement.getText();
        System.out.println(totalServing);

        //1회제공량
        WebElement servingSizeElement = servingElement.findElement(By.xpath(".//dd[2]"));
        String servingSize = servingSizeElement.getText();
        System.out.println(servingSize);

        //영양정보 컬럼
        WebElement nutritionElement = driver.findElement(By.xpath("//*/div[@id=\"content\"]/div[@data-skin-food=\"simple\"]/div[@data-skin-food=\"list\"]/div[@data-skin-food=\"row\"]"));

        //칼로리
        WebElement caloriesElement = nutritionElement.findElement(By.xpath(".//div[1]/span[3]"));
        String calories = caloriesElement.getText();
        System.out.println(calories);

        //단백질
        WebElement proteinElement = nutritionElement.findElement(By.xpath(".//div[2]/span[3]"));
        String protein = proteinElement.getText();
        System.out.println(protein);

        //지방
        WebElement fatElement = nutritionElement.findElement(By.xpath(".//div[3]/span[3]"));
        String fat = fatElement.getText();
        System.out.println(fat);
    }


    public void moveToNextPageOrGroup(String nextButtonXPath){

        try {

            WebElement nextButtonElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextButtonXPath)));

            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", nextButtonElement);

            wait.until(
                    driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

        } catch (NoMorePagesException e){
            throw new NoMorePagesException("No more page to navigate");
        }
    }


}
