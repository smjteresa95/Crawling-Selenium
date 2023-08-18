package com.example.data_collection.domain.searchentity.datasaver;

import com.example.data_collection.domain.searchentity.SearchSsgRawData;
import com.example.data_collection.domain.searchentity.SearchSsgRawDataRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SearchSsgDataSaver implements SearchDataSaver<SearchSsgRawData>{
    SearchSsgRawDataRepository searchSsgRawDataRepository;
    @Override
    public void save(SearchSsgRawData data) {
        searchSsgRawDataRepository.save(data);
    }
}
