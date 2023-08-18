package com.example.data_collection.domain.searchentity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchElevenStRawDataRepository extends JpaRepository<SearchElevenStRawData, Long> {

}
