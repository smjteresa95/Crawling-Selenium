package com.example.data_collection.domain;

import com.example.data_collection.domain.entity.ElevenStRawData;
import com.example.data_collection.domain.entity.ElevenStRawDataRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ElevenStDataSaver implements DataSaver<ElevenStRawData> {

    private final ElevenStRawDataRepository elevenStRawDataRepository;

    @Override
    public void save(ElevenStRawData data) {
        elevenStRawDataRepository.save(data);
    }
}
