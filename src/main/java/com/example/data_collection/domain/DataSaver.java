package com.example.data_collection.domain;

public interface DataSaver<T> {
    void save(T data);
}
