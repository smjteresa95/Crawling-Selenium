package com.example.data_collection.domain.searchentity.datasaver;


import com.example.data_collection.domain.searchentity.SearchElevenStRawData;
import com.example.data_collection.domain.searchentity.SearchElevenStRawDataRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SearchElevenStDataSaver implements SearchDataSaver<SearchElevenStRawData> {

    SearchElevenStRawDataRepository searchElevenStRawDataRepository;
    @Override
    public void save(SearchElevenStRawData data) {
        searchElevenStRawDataRepository.save(data);
    }
}
