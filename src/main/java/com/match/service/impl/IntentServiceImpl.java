package com.match.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.match.entity.Intent;
import com.match.mapper.IntentMapper;
import com.match.service.IntentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntentServiceImpl implements IntentService {

    private static final Logger logger = LoggerFactory.getLogger(IntentServiceImpl.class);

    @Autowired
    private IntentMapper intentMapper;

    @Override
    public void sendIntent(Intent intent) {
        logger.info("发送意向: fromUserId={}, toUserId={}, type={}, resumeId={}, jobId={}", 
                intent.getFromUserId(), intent.getToUserId(), intent.getType(),
                intent.getResumeId(), intent.getJobId());
        try {
            if (intent.getStatus() == null) {
                intent.setStatus(0);
            }
            intentMapper.insert(intent);
            logger.info("意向发送成功: id={}", intent.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据库插入意向异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void acceptIntent(Long intentId) {
        logger.info("接受意向: intentId={}", intentId);
        Intent intent = intentMapper.selectById(intentId);
        if (intent != null) {
            intent.setStatus(1);
            intentMapper.updateById(intent);
            logger.info("意向已接受: intentId={}", intentId);
        }
    }

    @Override
    public void rejectIntent(Long intentId) {
        logger.info("拒绝意向: intentId={}", intentId);
        Intent intent = intentMapper.selectById(intentId);
        if (intent != null) {
            intent.setStatus(2);
            intentMapper.updateById(intent);
            logger.info("意向已拒绝: intentId={}", intentId);
        }
    }

    @Override
    public void expireIntent(Long intentId) {
        logger.info("过期意向: intentId={}", intentId);
        Intent intent = intentMapper.selectById(intentId);
        if (intent != null) {
            intent.setStatus(3);
            intentMapper.updateById(intent);
            logger.info("意向已过期: intentId={}", intentId);
        }
    }

    @Override
    public List<Intent> getSentIntents(Long userId) {
        logger.info("获取用户发出的意向列表: userId={}", userId);
        QueryWrapper<Intent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return intentMapper.selectList(queryWrapper);
    }

    @Override
    public List<Intent> getReceivedIntents(Long userId) {
        logger.info("获取用户收到的意向列表: userId={}", userId);
        QueryWrapper<Intent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return intentMapper.selectList(queryWrapper);
    }

    @Override
    public Intent getById(Long id) {
        return intentMapper.selectById(id);
    }

    @Override
    public boolean hasActiveIntent(Long fromUserId, Long toUserId, Long resumeId, Long jobId) {
        logger.info("检查是否已有待回应意向: fromUserId={}, toUserId={}, resumeId={}, jobId={}",
                fromUserId, toUserId, resumeId, jobId);
        try {
            QueryWrapper<Intent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("from_user_id", fromUserId);
            queryWrapper.eq("to_user_id", toUserId);
            if (resumeId != null) {
                queryWrapper.eq("resume_id", resumeId);
            }
            if (jobId != null) {
                queryWrapper.eq("job_id", jobId);
            }
            queryWrapper.eq("status", 0);
            Long count = intentMapper.selectCount(queryWrapper);
            boolean exists = count > 0;
            logger.info("检查结果: {}", exists);
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("检查重复意向异常: {}", e.getMessage(), e);
            return false;
        }
    }
}