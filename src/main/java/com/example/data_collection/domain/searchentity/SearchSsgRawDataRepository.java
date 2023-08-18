package com.example.data_collection.domain.searchentity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchSsgRawDataRepository extends JpaRepository<SearchSsgRawData, Long> {
}
