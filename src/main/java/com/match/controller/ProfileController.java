package com.match.controller;

import com.match.common.Result;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.service.JobService;
import com.match.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobService jobService;

    @GetMapping("/resume")
    public Result<Resume> getResumeProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("用户未登录");
            return Result.error("未登录");
        }
        log.info("获取用户简历档案，用户ID：{}", userId);
        Resume resume = resumeService.getLatestByUserId(userId);
        return Result.success(resume);
    }

    @GetMapping("/job")
    public Result<Job> getJobProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("企业用户未登录");
            return Result.error("未登录");
        }
        log.info("获取企业职位档案，用户ID：{}", userId);
        Job job = jobService.getLatestByUserId(userId);
        return Result.success(job);
    }
}