package com.hashmap27.sample.service;

import com.hashmap27.sample.repository.readonly.RoCommonMapper;
import com.hashmap27.sample.repository.readwrite.RwCommonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomeServiceImpl implements HomeService {

    private final RoCommonMapper roCommonMapper;
    private final RwCommonMapper rwCommonMapper;

    /**
     * mybatis readOnly Test
     * @return
     */
    @Override
    public Integer getReadOnly() {
        return this.roCommonMapper.roTest();
    }

    /**
     * mybatis readWrite Test
     * @return
     */
    @Override
    public Integer getReadWrite() {
        return this.rwCommonMapper.rwTest();
    }
}
