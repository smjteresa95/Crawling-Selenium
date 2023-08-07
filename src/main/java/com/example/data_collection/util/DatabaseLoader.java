package com.example.data_collection.util;

import com.example.data_collection.domain.entity.Category;
import com.example.data_collection.domain.entity.CategoryRepository;
import jakarta.transaction.Transactional;
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

        List<String> categories = List.of("밀키트", "도시락", "간식/음료", "샐러드", "유제품", "냉장/냉동식품");

        for(String categoryName : categories){
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
