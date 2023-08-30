package com.example.data_collection.util;

import com.example.data_collection.domain.Category;
import com.example.data_collection.domain.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DatabaseLoader implements CommandLineRunner{

    private final CategoryRepository categoryRepository;

    @Autowired
    public DatabaseLoader(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public void initDatabase(){
        initCategoryDatabase();
    }

    private void initCategoryDatabase() {

        List<String> subCategories = List.of(
                "과자류, 빵류 또는 떡류",
                "빙과류", "알가공품류",
                "코코아가공품류 또는 초콜릿류",
                "수산가공식품류",
                "식육가공품 및 포장육",
                "유가공품",
                "음료류",
                "장기보존식품",
                "즉석식품류",
                "특수용도식품"
                );

        for(String categoryName : subCategories){
            Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryName);
            if(existingCategory.isEmpty()){
                categoryRepository.save(Category.builder().categoryName(categoryName).build());
            }
        }

    }

    @Override
    public void run(String... args) throws Exception {
        initDatabase();
    }
}
