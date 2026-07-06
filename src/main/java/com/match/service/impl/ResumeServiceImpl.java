package com.match.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.match.entity.Resume;
import com.match.mapper.ResumeMapper;
import com.match.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    @Override
    public Resume getById(Long id) {
        log.info("查询简历，ID：{}", id);
        return resumeMapper.selectById(id);
    }

    @Override
    public void updateParsedJson(Long id, String json) {
        log.info("更新简历解析结果，ID：{}", id);
        Resume resume = new Resume();
        resume.setId(id);
        resume.setParsedJson(json);
        resumeMapper.updateById(resume);
        log.info("简历解析结果更新成功");
    }

    @Override
    public Resume getLatestByUserId(Long userId) {
        log.info("查询用户最新简历，用户ID：{}", userId);
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT 1");
        return resumeMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Resume> findAll() {
        log.info("查询所有简历");
        return resumeMapper.selectList(null);
    }
}