package com.match.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.match.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职位数据访问接口
 * 已通过MyBatisPlusConfig中的@MapperScan("com.match.mapper")扫描
 * 添加@Mapper注解作为显式标记
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
}
