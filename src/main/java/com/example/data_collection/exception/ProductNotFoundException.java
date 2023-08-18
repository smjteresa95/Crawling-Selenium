package com.example.data_collection.exception;

public class ProductNotFoundException extends Exception{

    public ProductNotFoundException(){
        super("검색어와 일치하는 상품이 없습니다.");
    }

    public ProductNotFoundException(String message){
        super(message);
    }
}
