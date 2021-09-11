package com.hashmap27.sample.repository.readonly;

import com.hashmap27.sample.config.datasource.UseReadOnlyDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@UseReadOnlyDataSource
@Repository
public interface RoCommonMapper {

    Integer roTest();
}
