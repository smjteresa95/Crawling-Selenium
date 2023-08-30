package com.example.data_collection.domain.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OasisDataRepository extends JpaRepository<OasisDataEntity, Long> {
}
