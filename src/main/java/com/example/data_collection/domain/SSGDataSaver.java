package com.example.data_collection.domain;

import com.example.data_collection.domain.entity.RawData;
import com.example.data_collection.domain.entity.RawDataRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class SSGDataSaver implements DataSaver<RawData> {

    private final RawDataRepository rawDataRepository;

    @Override
    public void save(RawData data) {
        rawDataRepository.save(data);
    }
}
