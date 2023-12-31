package com.example.data_collection.service.seleniumservice;

import com.example.data_collection.config.HtmlConfig;
import com.example.data_collection.config.HtmlConfigFactory;
import com.example.data_collection.domain.dto.RawDataRequestDto;
import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import com.example.data_collection.util.CategoryCodes;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Order(1)
public class OasisCrawlerService extends BaseCrawler {

    int currentPage = 1;
    private final HtmlConfig tag;
    RawDataRepository repository;
    private static final String SITE_NAME = "oasis";
    CategoryCodes categoryCodes;


    @Autowired
    public OasisCrawlerService(WebDriver driver,
                               RawDataRepository repository,
                               HtmlConfigFactory htmlFactory,
                               CategoryCodes categoryCodes
    ) throws IllegalAccessException {
        super(driver);
        this.repository = repository;
        this.tag = htmlFactory.getTagForSite(SITE_NAME);
        this.categoryCodes = categoryCodes;
    }



    @Override
    public void startCrawling() throws InterruptedException {

        Map<String, Integer> oasisCodeMap = categoryCodes.getOASIS();
        List<Integer> categoryCodes = new ArrayList<>(oasisCodeMap.values());

        for(Integer code : categoryCodes) {

            String baseUrl = tag.getSiteUrl()+ code;

            driver.get(baseUrl);

            while(true){

                Thread.sleep(5);
                List<String> hrefLinks = getProductDetailLinks();

                if(!hrefLinks.isEmpty()) {

                    crawlDetailPage(hrefLinks, code);
                    currentPage++;

                    String url = baseUrl + "&rows=20&page=" + currentPage;
                    driver.navigate().to(url);

                } else {
                    System.out.println("해당 카테고리에서 넘어갈 페이지가 없습니다.");
                    break;
                }

            }
        }
    }



    //소분류 카테고리 얻어내는 메서드
    public String getSubCategoryByCode(int code){
        Map<String, Integer> oasis = categoryCodes.getOASIS();
        for(Map.Entry<String, Integer> entry : oasis.entrySet()){
            if(entry.getValue().equals(code)){
                return entry.getKey();
            }
        }
        return null;
    }

    //대분류 카테고리 얻어내는 메서드
    public String getLargeCategoryByCode(int code){
        Map<String, List<Integer>> oasis = categoryCodes.getOASIS_CODES_MAP();
        for(Map.Entry<String, List<Integer>> entry : oasis.entrySet()){
            if(entry.getValue().contains(code)){
                return entry.getKey();
            }
        }
        return null;
    }



    //상세페이지 link들을 가지고 와서 리스트에 저장.
    public List<String> getProductDetailLinks(){

        List<WebElement> links = driver.findElements(By.xpath("//*[@id=\"sec_product_list\"]/div[4]/div[2]/div/ul/li[contains(@class, 'swiper-slide pId')]/div/div[2]/div[1]/div[3]/div[1]/span/a[@class='listTit']"));
        List<String> linkHrefs = new ArrayList<>();

        for(WebElement link : links){
            linkHrefs.add(link.getAttribute("href"));
        }

        return linkHrefs;

    }

    //제품 디테일 페이지로 이동
    public void crawlDetailPage(List<String> linkHrefs, int code){

        //상품 정보 저장할 dto 객체 초기화
        RawDataRequestDto dto;

        //for문 돌리면서 제품상세정보 하나에 들어간다.
        for (String href : linkHrefs) {
            dto = new RawDataRequestDto();
            //대분류 카테고리명 저장
            System.out.println("카테고리: " + getLargeCategoryByCode(code));
            dto.setCategoryName(getLargeCategoryByCode(code));

            //소분류 카테고리명 저장
            dto.setSubcategoryName(getSubCategoryByCode(code));

            driver.navigate().to(href);

            // 제품 링크 dto 객체에 저장
            dto.setProductLink(href);

            //제품 상세 탭에 들어가기
            try {
                openDetailTab();
            }catch(Exception e){
                throw new RuntimeException();
            }

            //디테일 페이지에 있는 상품정보 가지고 오기
            try {
                saveItemInfo(dto);
            }catch (Exception e){
                throw new NoSuchElementException("요소를 찾을 수 없습니다.");
            }

            //디테일 페이지 내부 상품상세정보 테이블에 있는 정보 가지고 오기
            try {
                saveItemInfoFromTable(dto);
            }catch (Exception e){
                throw new NoSuchElementException("요소를 찾을 수 없습니다.");
            }


            //DB에 해당 salesName 이 존재하는지 체크한다.
            Optional<RawData> existingEntity = repository.findBySalesName(dto.getSalesName());

            //nutri_image 와 nutri_facts 둘 다 존재 하지 않으면 DB에 저장 하지 않는다.
            if(dto.getNutriFacts() == null && dto.getNutriImage() == null){
                return;
            } else {
                // 중복되는 sale name을 가지지 않았다면
                if(existingEntity.isEmpty()) {
                    try {
                        //DB에 상품정보 저장
                        repository.save(dto.toEntity());
                    } catch (Exception e) {
                        System.err.println("Unexpected error: " + e.getMessage());
                    }
                } else {
                    System.out.println("상품명: " + dto.getSalesName() + " DB에 있는 상품과 중복됩니다.");
                }
            }

            //뒤로 돌아오기
            driver.navigate().back();
        }

    }

    //제품 상세 탭에 들어가기
    public void openDetailTab(){
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tag.getDetailTab())));
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", element);
        } catch (NoSuchElementException e){
            System.out.println("상품상세정보 탭을 찾을 수 없습니다.");
        }
    }

    public void saveItemInfo(RawDataRequestDto dto) throws IOException {

        //제품 판매 사이트 저장
        dto.setSite(SITE_NAME);

        try {
            WebElement salesName = getDataByCss(tag.getSalesName());
            String salesNameStr = salesName.getText();

            if (!salesNameStr.isEmpty()) {
                dto.setSalesName(salesNameStr);
                System.out.println("판매명: " + salesNameStr);
            } else {
                dto.setSalesName(null);
            }

        } catch (Exception e){
            dto.setSalesName(null);
        }


        try {
            WebElement actualPrice = getDataByCss(tag.getActualPrice());
            if (!actualPrice.getText().isEmpty()) {
                //쉼표 제외하고 int형으로 변경
                dto.setActualPrice(Integer.parseInt(actualPrice.getText().replaceAll("[^0-9]", "")));
                System.out.println("가격: " + Integer.parseInt(actualPrice.getText().replaceAll("[^0-9]", "")));
            } else {
                dto.setActualPrice(0);
            }
        } catch (Exception e){
            dto.setActualPrice(0);
        }


        //쉼표와 원 제외하고 int형으로 변경
        try{
            WebElement discountPrice = getDataByXpath(tag.getDiscountPrice());
            String discountPriceStr = discountPrice.getText().replaceAll("[^0-9]", "");
            if(!discountPriceStr.isEmpty()) {
                dto.setDiscountPrice(Integer.parseInt(discountPriceStr));
                System.out.println("할인가: " + Integer.parseInt(discountPriceStr));
            } else{
                dto.setDiscountPrice(0);
            }
        } catch (Exception e){
            System.out.println("할인가 없음");
        }


        try{
            WebElement discountRate = getDataByCss(tag.getDiscountRate());
            dto.setDiscountRate(Integer.parseInt(discountRate.getText().replace("%","")));
            System.out.println("할인률: " + Integer.parseInt(discountRate.getText().replace("%","")));
        } catch(Exception e) {
            dto.setDiscountRate(0);
            System.out.println("할인률 없음");
        }


        //평점 문자열 처리
        try {
            WebElement rating = getDataByClass(tag.getRating());
            dto.setRating(getRating(rating.getText()));
            System.out.println("평점: " + getRating(rating.getText()));
        } catch (Exception e){
            dto.setRating(0.0);
            System.out.println("평점 없음");
        }

        //이미지 가지고 오기
        WebElement image = getDataByXpath(tag.getImage());
        if (image != null) {
            String imageStr = image.getAttribute("src");
            dto.setImage(imageStr);
            System.out.println("이미지: " + imageStr);

        } else {
            dto.setImage(null);
            System.out.println("상품이미지를 찾을 수 없습니다.");
        }

        WebElement nutritionFacts = getDataByXpath(tag.getNutriImage());
        if (nutritionFacts != null) {
            String nutritionFactsImg = nutritionFacts.getAttribute("src");
            dto.setNutriImage(nutritionFactsImg);
            System.out.println("성분이미지: " + nutritionFactsImg);
        } else {
            dto.setNutriImage(null);
            System.out.println("성분이미지를 찾을 수 없습니다.");
        }

    }

    //테이블에 있는 정보 가지고 와서 Map에 담아 반환
    public Map<String, String> getItemInfoFromTable() {

        //가지고 올 데이터를 담을 Map
        Map<String, String> tableData = new HashMap<>();

        //테이블의 행들을 가지고 오기
        List<WebElement> rows = driver.findElements(By.xpath(tag.getRowPath()));

        //테이블 안에서 가지고 와야 할 항목들의 리스트
        List<String> titles = Arrays.asList("상품명", "용량/수량/크기", "용량/수량", "원재료 및 함량", "영양성분");

        for (WebElement row : rows) {
            //변수를 밖에 먼저 정의해줬더니 서버가 종료되는 건 막을 수 있었다.
            String title;
            String value;

                //다행히 테이블에 있는 데이터는 전부 Xpath로 가지고 올 수 있다.
                try {
                    title = row.findElement(By.xpath(tag.getTitlePath())).getText();

                    //찾고자 하는 항목이 존재 하면 값도 가지고 오기
                    if (titles.contains(title)) {
                        value = row.findElement(By.xpath(tag.getValuePath())).getText();
                        tableData.put(title, value);
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("해당 값이 존재하지 않습니다: ");
                }
        }
        return tableData;
    }

    public void saveItemInfoFromTable(RawDataRequestDto dto){

        Map<String, String> tableData = getItemInfoFromTable();

        for(Map.Entry<String, String> entry: tableData.entrySet()){
            if(Objects.equals(entry.getKey(), "상품명")){
                String product_name = entry.getValue();

                if (product_name.contains("상세설명참조")
                        || product_name.contains("[상세설명참조]")
                        || product_name.contains("상세정보참고")) {
                    continue;
                }
                dto.setProductName(product_name);
                System.out.println("상품명: "+ product_name);

            } else if (Objects.equals(entry.getKey(), "용량/수량/크기")){
                dto.setQuantity(entry.getValue());
                System.out.println("용량/수량/크기: " + entry.getValue());

            } else if (Objects.equals(entry.getKey(), "용량/수량")){
                dto.setQuantity(entry.getValue());
                System.out.println("용량/수량:" + entry.getValue());

            } else if(Objects.equals(entry.getKey(),"원재료 및 함량")){
                dto.setIngredients(entry.getValue());
                System.out.println("원재료 및 함량: " + entry.getValue());

            } else if(Objects.equals(entry.getKey(), "영양성분")){
                dto.setNutriFacts(entry.getValue());
                System.out.println("영양성분: " + entry.getValue());
            }
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

}
