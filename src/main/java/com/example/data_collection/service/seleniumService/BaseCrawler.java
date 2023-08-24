package com.example.data_collection.service.seleniumService;

import com.example.data_collection.config.HtmlTagConfig;
import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.entity.BaseRawData;
import com.example.data_collection.domain.entity.datasaver.DataSaver;
import com.example.data_collection.exception.NoMorePagesException;
import com.example.data_collection.service.WebDriverService;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

abstract class BaseCrawler<T extends BaseRawData, ID extends Serializable> implements Crawler {

    HtmlTagConfigFactory htmlTagFactory;
    WebDriverService webDriverService;
    WebDriver driver;
    WebDriverWait wait;
    HtmlTagConfig tag;

    String siteName;
    int currentPage;

    private int totalPagePerGroup = 10;

    @Setter
    DataSaver<T> dataSaver;


    BaseCrawler(HtmlTagConfigFactory htmlTag, WebDriverService webDriverService, String siteName, int currentPage) throws IllegalAccessException {

        this.siteName = siteName;
        this.currentPage = currentPage;

        this.htmlTagFactory = htmlTag;
        this.tag = htmlTag.getHtmlTagForSite(siteName, currentPage);

        this.webDriverService = webDriverService;
        this.driver = webDriverService.getDriver();
        this.wait = webDriverService.getWait();
    }


    abstract T createRawDataInstance();

    abstract JpaRepository<T, ID> getRawDataRepository();


    public void reloadHtmlConfig(){
        try{
            this.tag = htmlTagFactory.getHtmlTagForSite(siteName, currentPage);

        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }


    public void waitForFiveSeconds(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getCategoryName(){
        WebElement category = driver.findElement(By.xpath(tag.getCategoryNameTag()));
        return category.getText();
    }


    //파라미터에 따라서, 다음페이지로 넘어갈 수도, 다음 그룹으로 넘어갈 수도 있다.
    public void moveToNextPageOrGroup(String nextButtonXPath){
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextButtonXPath)));

            WebElement pageButton = driver.findElement(By.xpath(nextButtonXPath));

            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", pageButton);

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );

            currentPage++;
            reloadHtmlConfig();

        } catch (TimeoutException e){
            throw new NoMorePagesException("No more page to navigate");
        }
    }

    //다음 페이지로 넘어감
    public void goToNextGroup() {
        String nextGroupButtonXPath = tag.getNextGroupButtonXPath();
        moveToNextPageOrGroup(nextGroupButtonXPath);
    }

    //다음 페이지 그룹으로 넘어감
    public void goToNextPageWithinGroup(){
        String nextPageButtonXPath = tag.getNextPageButtonXPath();
        moveToNextPageOrGroup(nextPageButtonXPath);
    }

//    abstract Double getDiscountRate(int index, List<WebElement> discountRates);

    //평점 전처리 하는 메서드
    abstract double getRating(int index, List<WebElement> ratings, JavascriptExecutor js);

    //마켓 컬리의 경우 BrandTag를 따로 찾아 값을 가지고 올 수 있는 것은 아니고 product name에서 추출해야 한다.
    abstract String getBrand(int index, List<WebElement> brands);

    //금액 String -> int 전처리 하는 메서드
    //19,000(String) -> 19000(int)
    public int getPrice(int index, List<WebElement> prices){
        if(index < prices.size() && prices.get(index) != null) {
            String priceStr = prices.get(index).getText().replace(",", "");
            return Integer.parseInt(priceStr);

        } else {
            return 0;
        }
    }





    //현재 제품 리스트 페이지 크롤링 해오기
    public void crawlCurrentPage(
            String productNameTag, String brandTag, String priceTag, String imageTag,
            String linkTag, String ratingTag, String imageAttr, String linkAttr){

        JpaRepository<T, ID> rawDataRepository = getRawDataRepository();

        List<WebElement> productNames = driver.findElements(By.xpath(productNameTag));
        List<WebElement> brands = driver.findElements(By.xpath(brandTag));
        List<WebElement> prices = driver.findElements(By.xpath(priceTag));
        List<WebElement> images = driver.findElements(By.xpath(imageTag));
        List<WebElement> links = driver.findElements(By.xpath(linkTag));
        List<WebElement> ratings = driver.findElements(By.xpath(ratingTag));
//        List<WebElement> discountRates= driver.findElements(By.xpath(discountRateTag));

        int size = productNames.size();

        List<String> imgSrcs = new ArrayList<>();
        for(WebElement image : images){
            imgSrcs.add(image.getAttribute(imageAttr));
        }

        List<String> linkHrefs = new ArrayList<>();
        for(WebElement link : links){
            linkHrefs.add(link.getAttribute(linkAttr));
        }

        //rating 가지고 오기 위함(자식 요소의 텍스트를 제외하고 특정요소의 텍스트만 가지고 오기 위해서 JavaScriptExecutor 사용)
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for(int i=0; i< size; i++){
            T rawData = createRawDataInstance();

            //상품명 저장
            rawData.setProductName(i < productNames.size() ? productNames.get(i).getText() : "");

            //마켓컬리의 경우, Brands 가 null이다. 이 경우에는 위에서 정의한 getBrand를 이용해서 값을 저장하고,
            //그렇지 않으면 brands에 있는 값을 저장한다.
            if(brands.isEmpty()){
                rawData.setBrand(getBrand(i, brands));
            } else{
                rawData.setBrand(i < brands.size() ? brands.get(i).getText() : "");
            }

            //이미지 저장
            rawData.setImage(i < imgSrcs.size() ? imgSrcs.get(i) : "");

            //제품링크 저장
            rawData.setProductLink(i < linkHrefs.size() ? linkHrefs.get(i) : "");

            // price 처리 후 저장
            rawData.setPrice(i < prices.size() ? getPrice(i, prices) : 0);

            //discount rate 처리 후 저장
//            rawData.setDiscountRate(i < ratings.size() ? getDiscountRate(i, discountRates) : 0);

            //rating 처리 후 저장
            if(ratings.isEmpty()){
                rawData.setRating(null);
            } else {
                rawData.setRating(i < ratings.size() ? getRating(i, ratings, js) : 0);
            }

            //카테고리 명 저장
            rawData.setCategoryName(getCategoryName());

            rawDataRepository.save(rawData);
            System.out.println(rawData.toString());

        }
        waitForFiveSeconds();
    }

    public void crawlProductsAndSave() {
        try {
            try {
                crawlCurrentPage(tag.getProductNameTag(), tag.getBrandTag(), tag.getPriceTag(), tag.getImageTag(), tag.getLinkTag(), tag.getRatingTag(), tag.getImageAttribute(), tag.getLinkAttribute());

            } catch (Exception e){
                throw new NoMorePagesException("Unexpected error: " + e.getMessage());
            }

            // If the current page is a multiple of 10, move to the next group
            if (currentPage % totalPagePerGroup == 0) {
                goToNextGroup(); // Click on the 'next' button to move to the next group
                waitForFiveSeconds();
            }
            // For all other pages, move to the next page within the group
            else {
                goToNextPageWithinGroup(); // Click on the 'currentPage + 1' button to move to the next page within the group
                waitForFiveSeconds();
            }

        } catch (Exception e){
            throw new NoMorePagesException("Unexpected error: " + e.getMessage());
        }
    }



}
