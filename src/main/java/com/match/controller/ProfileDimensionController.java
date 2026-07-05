package com.match.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.match.common.Result;
import com.match.dto.DimensionResult;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.service.JobService;
import com.match.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/profile/dimensions")
public class ProfileDimensionController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobService jobService;

    @GetMapping("/resume/{resumeId}")
    public Result<DimensionResult> getResumeDimensions(@PathVariable Long resumeId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("用户未登录");
            return Result.error("未登录");
        }
        log.info("获取简历维度评分，用户ID：{}，简历ID：{}", userId, resumeId);

        Resume resume = resumeService.getById(resumeId);
        if (resume == null) {
            return Result.error("简历不存在");
        }

        String parsedJson = resume.getParsedJson();
        if (parsedJson == null || parsedJson.isEmpty()) {
            return Result.error("简历未解析");
        }

        DimensionResult result = parseResumeToDimensions(resumeId, parsedJson);
        return Result.success(result);
    }

    @GetMapping("/job/{jobId}")
    public Result<DimensionResult> getJobDimensions(@PathVariable Long jobId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("企业用户未登录");
            return Result.error("未登录");
        }
        log.info("获取职位维度评分，用户ID：{}，职位ID：{}", userId, jobId);

        Job job = jobService.getById(jobId);
        if (job == null) {
            return Result.error("职位不存在");
        }

        String parsedJson = job.getParsedJson();
        if (parsedJson == null || parsedJson.isEmpty()) {
            return Result.error("职位未解析");
        }

        DimensionResult result = parseJobToDimensions(jobId, parsedJson);
        return Result.success(result);
    }

    private DimensionResult parseResumeToDimensions(Long id, String parsedJson) {
        DimensionResult result = new DimensionResult();
        result.setId(id);
        result.setStability(70.0);
        result.setCommunication(80.0);

        try {
            JSONObject json = JSON.parseObject(parsedJson);

            String name = json.getString("name");
            result.setName(name != null ? name : "未知姓名");

            List<String> skills = new ArrayList<>();
            if (json.containsKey("skills")) {
                Object skillsObj = json.get("skills");
                if (skillsObj instanceof List) {
                    for (Object skill : (List<?>) skillsObj) {
                        skills.add(String.valueOf(skill));
                    }
                }
            }
            result.setSkillTags(skills);

            int skillCount = skills.size();
            result.setTechnicalDepth(calculateTechnicalDepth(skillCount));

            int experience = 0;
            if (json.containsKey("experience")) {
                experience = json.getIntValue("experience");
            }
            result.setProjectExp(calculateProjectExp(experience));

            String education = json.getString("education");
            result.setEducationBg(calculateEducationBg(education));

        } catch (Exception e) {
            log.error("解析简历JSON失败", e);
            result.setName("解析失败");
            result.setSkillTags(new ArrayList<>());
            result.setTechnicalDepth(0.0);
            result.setProjectExp(0.0);
            result.setEducationBg(0.0);
        }

        return result;
    }

    private DimensionResult parseJobToDimensions(Long id, String parsedJson) {
        DimensionResult result = new DimensionResult();
        result.setId(id);
        result.setStability(70.0);
        result.setCommunication(80.0);

        try {
            JSONObject json = JSON.parseObject(parsedJson);

            String positionName = json.getString("positionName");
            result.setName(positionName != null ? positionName : "未知职位");

            List<String> skillRequirements = new ArrayList<>();
            if (json.containsKey("skillRequirements")) {
                Object skillsObj = json.get("skillRequirements");
                if (skillsObj instanceof List) {
                    for (Object skill : (List<?>) skillsObj) {
                        skillRequirements.add(String.valueOf(skill));
                    }
                }
            }
            result.setSkillTags(skillRequirements);

            int skillCount = skillRequirements.size();
            result.setTechnicalDepth(calculateTechnicalDepth(skillCount));

            int experienceRequirement = 0;
            if (json.containsKey("experienceRequirement")) {
                experienceRequirement = json.getIntValue("experienceRequirement");
            }
            result.setProjectExp(calculateProjectExp(experienceRequirement));

            String educationRequirement = json.getString("educationRequirement");
            result.setEducationBg(calculateEducationBg(educationRequirement));

        } catch (Exception e) {
            log.error("解析职位JSON失败", e);
            result.setName("解析失败");
            result.setSkillTags(new ArrayList<>());
            result.setTechnicalDepth(0.0);
            result.setProjectExp(0.0);
            result.setEducationBg(0.0);
        }

        return result;
    }

    private Double calculateTechnicalDepth(int skillCount) {
        if (skillCount <= 0) return 0.0;
        if (skillCount == 1) return 20.0;
        if (skillCount == 2) return 40.0;
        if (skillCount == 3) return 60.0;
        if (skillCount == 4) return 80.0;
        return 100.0;
    }

    private Double calculateProjectExp(int years) {
        if (years <= 0) return 0.0;
        if (years == 1) return 20.0;
        if (years == 2) return 40.0;
        if (years == 3) return 60.0;
        if (years <= 5) return 80.0;
        return 100.0;
    }

    private Double calculateEducationBg(String education) {
        if (education == null || education.isEmpty()) return 0.0;
        String edu = education.toLowerCase();
        if (edu.contains("博士") || edu.contains("phd")) return 100.0;
        if (edu.contains("硕士") || edu.contains("研究生")) return 90.0;
        if (edu.contains("本科") || edu.contains("学士")) return 70.0;
        if (edu.contains("专科") || edu.contains("大专")) return 40.0;
        if (edu.contains("高中") || edu.contains("中专")) return 20.0;
        return 30.0;
    }
}