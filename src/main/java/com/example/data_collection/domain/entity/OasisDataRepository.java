package com.example.data_collection.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OasisDataRepository extends JpaRepository<OasisDataEntity, Long> {
    Optional<OasisDataEntity> findBySalesName(String salesName);
}
