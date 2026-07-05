package com.match.controller;

import com.alibaba.fastjson.JSON;
import com.match.common.Result;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.service.JobService;
import com.match.service.ParseService;
import com.match.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/parse")
public class ParseController {

    @Autowired
    private ParseService parseService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobService jobService;

    @PostMapping("/{type}/{id}")
    public Result<String> parse(
            @PathVariable("type") String type,
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                log.warn("解析失败：用户未登录");
                return Result.error("请先登录");
            }
            log.info("用户ID {} 开始解析，类型：{}，ID：{}", userId, type, id);

            String filePath;
            if ("resume".equals(type)) {
                Resume resume = resumeService.getById(id);
                if (resume == null) {
                    return Result.error("简历不存在");
                }
                if (!userId.equals(resume.getUserId())) {
                    return Result.error("无权访问该简历");
                }
                filePath = resume.getFilePath();
            } else if ("job".equals(type)) {
                Job job = jobService.getById(id);
                if (job == null) {
                    return Result.error("职位不存在");
                }
                if (!userId.equals(job.getUserId())) {
                    return Result.error("无权访问该职位");
                }
                filePath = job.getFilePath();
            } else {
                return Result.error("无效的解析类型");
            }

            log.info("文件路径：{}", filePath);

            String text = parseService.extractTextFromFile(filePath);
            log.info("文本提取完成，长度：{}", text.length());

            Map<String, Object> resultMap;
            if ("resume".equals(type)) {
                resultMap = parseService.parseResume(text);
            } else {
                resultMap = parseService.parseJob(text);
            }

            String jsonString = JSON.toJSONString(resultMap);
            log.info("解析结果JSON：{}", jsonString);

            if ("resume".equals(type)) {
                resumeService.updateParsedJson(id, jsonString);
            } else {
                jobService.updateParsedJson(id, jsonString);
            }

            return Result.success("解析成功");
        } catch (Exception e) {
            log.error("解析失败", e);
            return Result.error("解析失败：" + e.getMessage());
        }
    }
}