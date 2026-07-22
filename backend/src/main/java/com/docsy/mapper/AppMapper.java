package com.docsy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docsy.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMapper extends BaseMapper<App> {
}
