package com.example.data_collection.domain;

import com.example.data_collection.domain.entity.SSGRawData;
import com.example.data_collection.domain.entity.SSGRawDataRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class SSGDataSaver implements DataSaver<SSGRawData> {

    private final SSGRawDataRepository SSGRawDataRepository;

    @Override
    public void save(SSGRawData data) {
        SSGRawDataRepository.save(data);
    }
}
