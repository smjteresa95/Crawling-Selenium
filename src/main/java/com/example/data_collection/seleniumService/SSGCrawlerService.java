package com.example.data_collection.seleniumService;

import com.example.data_collection.config.HtmlTagConfigFactory;
import com.example.data_collection.domain.BaseRawData;
import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import com.example.data_collection.exception.NoMorePagesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SSGCrawlerService extends AbstractCrawler<RawData, Long>{

    //밀키트
    private String mealKitCode = "6000139913";

    //냉장/냉동식품
    private String frozenCode = "6000139879";

    //도시락/델리
    private String deliCode = "6000139914";

    private static final int CURRENT_PAGE = 1;

    private final RawDataRepository rawDataRepository;

    private static final String SITE_NAME = "ssg";


    @Autowired
    public SSGCrawlerService(RawDataRepository rawDataRepository, HtmlTagConfigFactory htmlTag, WebDriverService webDriverService) throws IllegalAccessException {
        super(htmlTag, webDriverService, SITE_NAME, CURRENT_PAGE);
        this.rawDataRepository = rawDataRepository;
    }


    @Override
    protected RawData createRawDataInstance() {
        return new RawData();
    }

    @Override
    protected RawDataRepository getRawDataRepository(){
        return rawDataRepository;
    }

    @Override
    public void crawlAllProductsByCategory(){

        List<String> categoryCodes = Arrays.asList(mealKitCode, frozenCode, deliCode);

        for (String code : categoryCodes) {

            driver.get(tag.getSiteUrl() + code);

            currentPage = 1;

            while(true) {
                try {
                    crawlProductsAndSave();
                } catch (NoMorePagesException e){
                    break;
                }
            }
        }
    }



}