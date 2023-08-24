package com.example.data_collection.domain.entity.datasaver;

import com.example.data_collection.domain.entity.OasisRawData;
import com.example.data_collection.domain.entity.OasisRawDataRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OasisDataSaver implements DataSaver<OasisRawData> {

    private OasisRawDataRepository rawDataRepository;
    @Override
    public void save(OasisRawData data) {
        rawDataRepository.save(data);
    }
}
