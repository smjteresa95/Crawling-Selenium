package com.example.data_collection.service.seleniumservice;

import com.example.data_collection.config.HtmlConfig;
import com.example.data_collection.config.HtmlConfigFactory;
import com.example.data_collection.domain.dto.SsgDataRequestDto;
import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import com.example.data_collection.util.CategoryCodes;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Order(1)
public class SsgCrawlerService extends BaseCrawler {


    int currentPage = 1;

    private final RawDataRepository repository;

    private final HtmlConfig tag;
    private static final String SITE_NAME = "ssg";

    CategoryCodes categoryCodes;

    @Autowired
    public SsgCrawlerService(WebDriver driver,
                             HtmlConfigFactory tag,
                             RawDataRepository repository,
                             CategoryCodes categoryCodes) throws IllegalAccessException {
        super(driver);
        this.tag = tag.getTagForSite(SITE_NAME);
        this.repository = repository;
        this.categoryCodes = categoryCodes;

    }

    @Override
    public void startCrawling(){

        Map<String, String> ssgCodeMap = categoryCodes.getSSG();
        List<String> categoryCodes = new ArrayList<>(ssgCodeMap.values());

        for(String code : categoryCodes) {
            String baseUrl = tag.getSiteUrl() + code;
            driver.get(baseUrl);

            while(true){
                System.out.println("현재 카테고리: " + code);
                List<String> hrefLinks = getProductDetailLinks();

                if(!hrefLinks.isEmpty()){
                    crawlDetailPage(hrefLinks, code);
                    currentPage++;

                    String url = baseUrl + "&page=" + currentPage;
                    driver.navigate().to(url);
                } else {
                    System.out.println("해당 카테고리에서 넘어갈 페이지가 없습니다.");
                    break;
                }
            }

        }
    }
    //소분류 카테고리 얻어내는 메서드
    public String getSubCategoryByCode(String code){
        Map<String, String> ssg = categoryCodes.getSSG();
        for(Map.Entry<String, String> entry : ssg.entrySet()){
            if(entry.getValue().equals(code)){
                return entry.getKey();
            }
        }
        return null;
    }

    //대분류 카테고리 얻어내는 메서드
    public String getLargeCategoryByCode(String code){
        Map<String, List<String>> ssg = categoryCodes.getSSG_CODES_MAP();
        for(Map.Entry<String, List<String>> entry : ssg.entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return null;
    }


    public List<String> getProductDetailLinks(){

        List<WebElement> rows = driver.findElements(By.xpath(tag.getItemList()));

        //상세페이지 link들을 가지고 와서 리스트에 저장.
        List<String> linkHrefs = new ArrayList<>();

        for(WebElement row: rows) {

            List<WebElement> links = row.findElements(By.xpath(tag.getLink()));

            for (WebElement link : links) {
                linkHrefs.add(link.getAttribute("href"));
            }
        }
        System.out.println("한 페이지에 존재하는 상품 갯수: " + linkHrefs.size());
        return linkHrefs;
    }

    //제품 디테일 페이지로 이동
    public void crawlDetailPage(List<String> linkHrefs, String code){

        SsgDataRequestDto dto;


        //for문 돌리면서 하나를 실행시키고

        for(String href : linkHrefs){
            dto = new SsgDataRequestDto();

            //대분류 카테고리명 저장
            dto.setCategoryName(getLargeCategoryByCode(code));

            //소분류 카테고리명 저장
            dto.setSubcategoryName(getSubCategoryByCode(code));

            //dto에 링크를 저장
            dto.setProductLink(href);
            System.out.println(href);

            driver.navigate().to(href);

            //디테일 페이지에 있는 상품정보 가지고 오기
            try {
                getItemInfo(dto);
            }catch (Exception e){
                System.err.println("Unexpected error: " + e.getMessage());
            }

            //상품필수정보 표에 있는 값 가지고 오기
            try {
                saveItemInfoFromTable(dto);
            } catch(Exception e){
                System.err.println("Unexpected error: " + e.getMessage());
            }

            //DB에 해당 salesName 이 존재하는 지 체크한다.
            Optional<RawData> existingEntity = repository.findBySalesName(dto.getSalesName());


            //nutri_image 와 nutri_facts 둘 다 존재 하지 않으면 DB에 저장 하지 않는다.
            if(dto.getNutriFacts() == null && dto.getNutriImage() == null){
                return;
            } else {
                if(existingEntity.isEmpty()) {
                    try {
                        //DB에 상품정보 저장
                        repository.save(dto.toEntity());
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                } else {
                    System.out.println("sales name " + dto.getSalesName() + " is already in the database.");
                }
            }

            //뒤로 돌아오기
            driver.navigate().back();

        }
    }


    //제품 상세 탭의 제품상세테이블에서 항목의 유무를 확인 후 있는 경우 값을 가지고 오기.
    public Map<String, String> getItemInfoFromTable(){

        //테이블의 행들을 가지고 오기
        //테이블에 값이 존재 하는데 가지고 오지 못하는 경우가 생긴다.
        List<WebElement> rows = driver.findElements(By.xpath(tag.getRowPath()));

        //title과 cell을 매핑 할 Map 객체 초기화
        Map<String, String> tableData = new HashMap<>();

        //테이블 안에서 가지고 와야 할 항목들의 리스트
        List<String> titles = Arrays.asList(
                "품명 및 모델명",
                "포장 단위별 내용물의 용량 (중량), 수량, 크기",
                "주원료/함량(원료 원산지)",
                "영양성분"
        );

        for(WebElement row : rows){
            //변수를 밖에 먼저 정의해줬더니 서버가 종료되는 건 막을 수 있었다.
            String title;
            String value;

            //다행히 테이블에 있는 데이터는 전부 Xpath로 가지고 올 수 있다.
            try {
                title = row.findElement(By.xpath(tag.getTitlePath())).getText();


                //찾고자 하는 항목이 존재 하면 값도 가지고 오기
                if(titles.contains(title)) {

                    value = row.findElement(By.xpath(tag.getValuePath())).getText();

                    tableData.put(title, value);
                }
            } catch(NoSuchElementException e){
                System.out.println("Hey, No value nor title found: " + row.getText());
            }

        }

        return tableData;
    }

    public void saveItemInfoFromTable(SsgDataRequestDto dto){

        Map<String, String> tableData = getItemInfoFromTable();

        for(Map.Entry<String, String> entry: tableData.entrySet()){
            if(Objects.equals(entry.getKey(), "품명 및 모델명")){
                String product_name = entry.getValue();

                if (product_name.contains("상세설명참조")
                        || product_name.contains("[상세설명참조]")
                        || product_name.contains("상세정보참고")) {
                    continue;
                }
                dto.setProductName(product_name);
                System.out.println(product_name);

            } else if (Objects.equals(entry.getKey(), "포장 단위별 내용물의 용량 (중량), 수량, 크기")){
                dto.setQuantity(entry.getValue());
                System.out.println(entry.getValue());

            } else if(Objects.equals(entry.getKey(),"주원료/함량(원료 원산지)")){
                dto.setIngredients(entry.getValue());
                System.out.println(entry.getValue());

            } else if(Objects.equals(entry.getKey(), "영양성분")){
                //제대로 된 영양성분의 값을 가지고 왔을 때만 저장
                if(hasNutriFacts(entry.getValue())){
                    dto.setNutriFacts(entry.getValue());
                }
                System.out.println(entry.getValue());
            }
        }
    }

    //영양성분에 "상세", "없음", "참조" 라는 단어가 포함되어 있으면 저장하지 않게 한다.
    public boolean hasNutriFacts(String nutriFacts){

        List<String> filter_words = Arrays.asList("상세", "없음", "참조" );
        for(String word:filter_words){
            if(nutriFacts.contains(word)) {
                return false;
            }
        }
        return true;
    }


    //제품 디테일 페이지에서 상품정보 가지고 오기
    public void getItemInfo(SsgDataRequestDto dto){

        //제품 판매 사이트 저장
        dto.setSite(SITE_NAME);

        String salesName = getDataByXpath(tag.getSalesName()).getText();
        dto.setSalesName(salesName);
        System.out.println(salesName);

        try {
            WebElement actualPrice = getDataByXpath(tag.getActualPrice());
            if (!actualPrice.getText().isEmpty()) {
                //쉼표 제외하고 int형으로 변경
                dto.setActualPrice(Integer.parseInt(actualPrice.getText().replaceAll("[^0-9]", "")));
                System.out.println(Integer.parseInt(actualPrice.getText().replaceAll("[^0-9]", "")));
            } else {
                dto.setActualPrice(0);
            }
        } catch (Exception e){
            System.out.println("일반가 없음");
        }

        //쉼표와 원 제외하고 int형으로 변경
        try{
            WebElement discountPrice = getDataByXpath(tag.getDiscountPrice());
            String discountPriceStr = discountPrice.getText().replaceAll("[^0-9]", "");
            if(!discountPriceStr.isEmpty()) {
                dto.setDiscountPrice(Integer.parseInt(discountPriceStr));
                System.out.println(Integer.parseInt(discountPriceStr));
            } else{
                dto.setDiscountPrice(0);
            }
        } catch (Exception e){
            System.out.println("할인가 없음");
        }

        try{
            WebElement discountRate = getDataByXpath(tag.getDiscountRate());
            dto.setDiscountRate(Integer.parseInt(discountRate.getText().replace("%","")));
            System.out.println(Integer.parseInt(discountRate.getText().replace("%","")));
        } catch(Exception e) {
            dto.setDiscountRate(0);
            System.out.println("할인률 없음");
        }


        try{
            WebElement rating = getDataByXpath(tag.getRating());
            String ratingStr = rating.getText();
            if(!ratingStr.isEmpty()){
                dto.setRating(Double.parseDouble(ratingStr));
                System.out.println(Double.parseDouble(ratingStr));
            } else{
                dto.setRating(0.0);
            }
        } catch (Exception e){
            System.out.println("평점 없음");
        }

        //이미지 가지고 오기
        WebElement image = getDataByXpath(tag.getImage());
        if(image != null){
            String imageStr = image.getAttribute("src");
            dto.setImage(imageStr);
            System.out.println(imageStr);
        } else {
            System.out.println("상품이미지를 찾을 수 없습니다.");
        }

        //성분이미지 찾기
        driver.switchTo().frame(tag.getIframe());
        WebElement nutritionFacts = getDataByXpath(tag.getNutriImage());
        if (nutritionFacts != null) {
            String nutritionFactsImg = nutritionFacts.getAttribute("src");
            dto.setNutriImage(nutritionFactsImg);
            System.out.println(nutritionFactsImg);
        } else {
            System.out.println("성분이미지를 찾을 수 없습니다.");
        }
        driver.switchTo().defaultContent();
    }


}


