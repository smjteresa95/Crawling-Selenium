package com.example.data_collection.domain.entity;

import com.example.data_collection.domain.entity.OasisRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OasisRawDataRepository extends JpaRepository<OasisRawData, Long> {
}
