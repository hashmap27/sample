package com.hashmap27.sample.repository.readwrite;

import com.hashmap27.sample.config.datasource.UseReadWriteDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@UseReadWriteDataSource
@Repository
public interface RwCommonMapper {

    Integer rwTest();
}
