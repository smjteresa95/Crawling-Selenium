package com.example.data_collection.domain.entity.datasaver;

import com.example.data_collection.domain.entity.SSGRawData;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class SSGDataSaver implements DataSaver<SSGRawData> {

    private final com.example.data_collection.domain.entity.SSGRawDataRepository SSGRawDataRepository;

    @Override
    public void save(SSGRawData data) {
        SSGRawDataRepository.save(data);
    }
}
