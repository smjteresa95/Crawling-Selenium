package com.example.data_collection.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicDataRepository extends JpaRepository<PublicData, Long> {

    @Query("SELECT a.productName FROM PublicData a")
    List<String> findAllProductNames();
}
