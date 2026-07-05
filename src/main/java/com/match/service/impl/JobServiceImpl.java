package com.match.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.match.entity.Job;
import com.match.mapper.JobMapper;
import com.match.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public Job getById(Long id) {
        log.info("查询职位，ID：{}", id);
        return jobMapper.selectById(id);
    }

    @Override
    public void updateParsedJson(Long id, String json) {
        log.info("更新职位解析结果，ID：{}", id);
        Job job = new Job();
        job.setId(id);
        job.setParsedJson(json);
        jobMapper.updateById(job);
        log.info("职位解析结果更新成功");
    }

    @Override
    public Job getLatestByUserId(Long userId) {
        log.info("查询用户最新职位，用户ID：{}", userId);
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT 1");
        return jobMapper.selectOne(queryWrapper);
    }
}