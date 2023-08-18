package com.example.data_collection.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiDataRepository extends JpaRepository<ApiData, Long> {

    @Query("SELECT a.productName FROM ApiData a")
    List<String> findAllProductNames();
}
