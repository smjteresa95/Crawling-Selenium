package com.example.data_collection.domain.entity.datasaver;

import com.example.data_collection.domain.entity.KurlyRawData;
import com.example.data_collection.domain.entity.KurlyRawDataRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KurlyDataSaver implements DataSaver<KurlyRawData> {
    private final KurlyRawDataRepository kurlyRepository;

    @Override
    public void save(KurlyRawData data) {
        kurlyRepository.save(data);
    }
}
