package com.example.data_collection.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {
    Optional<RawData> findBySalesName(String salesName);
}
