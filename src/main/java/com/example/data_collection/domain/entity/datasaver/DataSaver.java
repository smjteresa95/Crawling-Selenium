package com.example.data_collection.domain.entity.datasaver;

public interface DataSaver<T> {
    void save(T data);
}
