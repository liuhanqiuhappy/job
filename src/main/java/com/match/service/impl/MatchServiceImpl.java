package com.match.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.match.dto.MatchResult;
import com.match.dto.RecommendCandidateVO;
import com.match.dto.RecommendJobVO;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.entity.User;
import com.match.mapper.UserMapper;
import com.match.service.JobService;
import com.match.service.MatchService;
import com.match.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public MatchResult calculateMatch(Long resumeId, Long jobId) {
        log.info("开始计算匹配，简历ID：{}，职位ID：{}", resumeId, jobId);

        Resume resume = resumeService.getById(resumeId);
        Job job = jobService.getById(jobId);

        if (resume == null) {
            log.error("简历不存在，ID：{}", resumeId);
            return null;
        }
        if (job == null) {
            log.error("职位不存在，ID：{}", jobId);
            return null;
        }

        Map<String, Object> resumeData = parseJson(resume.getParsedJson());
        Map<String, Object> jobData = parseJson(job.getParsedJson());

        String resumeEdu = getResumeEducation(resumeData);
        String jobEdu = getJobEducation(jobData);
        String resumeCity = getResumeCity(resumeData);
        String jobCity = getJobCity(jobData);
        List<String> resumeSkills = getResumeSkills(resumeData);
        List<String> jobSkills = getJobSkills(jobData);

        double educationScore = calculateEducationScore(resumeEdu, jobEdu);
        double cityScore = calculateCityScore(resumeCity, jobCity);
        double jaccardScore = calculateJaccardScore(resumeSkills, jobSkills);
        double cosineScore = calculateCosineScore(resumeSkills, jobSkills);

        double totalScore = educationScore + cityScore + jaccardScore + cosineScore;

        MatchResult result = new MatchResult();
        result.setTotalScore(totalScore);
        result.setEducationScore(educationScore);
        result.setCityScore(cityScore);
        result.setJaccardScore(jaccardScore);
        result.setCosineScore(cosineScore);
        result.setEducationMatch(buildEducationMatchDetail(resumeEdu, jobEdu, educationScore));
        result.setCityMatch(buildCityMatchDetail(resumeCity, jobCity, cityScore));
        result.setCommonSkills(findCommonSkills(resumeSkills, jobSkills));
        result.setMissingSkills(findMissingSkills(resumeSkills, jobSkills));
        result.setResumeId(resumeId.doubleValue());
        result.setJobId(jobId.doubleValue());

        log.info("匹配计算完成，总分：{}", totalScore);
        return result;
    }

    @Override
    public List<RecommendJobVO> recommendJobs(Long userId) {
        log.info("为用户推荐职位，用户ID：{}", userId);

        Resume resume = resumeService.getLatestByUserId(userId);
        if (resume == null) {
            log.warn("用户未上传简历，用户ID：{}", userId);
            return new ArrayList<>();
        }

        List<Job> allJobs = jobService.findAll();
        if (allJobs.isEmpty()) {
            log.warn("暂无职位数据");
            return new ArrayList<>();
        }

        List<RecommendJobVO> recommendations = new ArrayList<>();

        for (Job job : allJobs) {
            MatchResult matchResult = calculateMatch(resume.getId(), job.getId());
            if (matchResult != null) {
                User user = userMapper.selectById(job.getUserId());
                RecommendJobVO vo = new RecommendJobVO();
                vo.setUserId(job.getUserId());
                vo.setUsername(user != null ? user.getUsername() : "未知");
                vo.setMatchResult(matchResult);
                vo.setJobId(job.getId());
                vo.setFilePath(job.getFilePath());
                recommendations.add(vo);
            }
        }

        recommendations.sort((a, b) -> Double.compare(b.getMatchResult().getTotalScore(), a.getMatchResult().getTotalScore()));

        if (recommendations.size() > 10) {
            recommendations = recommendations.subList(0, 10);
        }

        log.info("推荐职位完成，共推荐 {} 个职位", recommendations.size());
        return recommendations;
    }

    @Override
    public List<RecommendCandidateVO> recommendCandidates(Long userId) {
        log.info("为企业推荐候选人，用户ID：{}", userId);

        Job job = jobService.getLatestByUserId(userId);
        if (job == null) {
            log.warn("企业未发布职位，用户ID：{}", userId);
            return new ArrayList<>();
        }

        List<Resume> allResumes = resumeService.findAll();
        if (allResumes.isEmpty()) {
            log.warn("暂无简历数据");
            return new ArrayList<>();
        }

        List<RecommendCandidateVO> recommendations = new ArrayList<>();

        for (Resume resume : allResumes) {
            MatchResult matchResult = calculateMatch(resume.getId(), job.getId());
            if (matchResult != null) {
                User user = userMapper.selectById(resume.getUserId());
                RecommendCandidateVO vo = new RecommendCandidateVO();
                vo.setUserId(resume.getUserId());
                vo.setUsername(user != null ? user.getUsername() : "未知");
                vo.setMatchResult(matchResult);
                vo.setResumeId(resume.getId());
                vo.setFilePath(resume.getFilePath());
                recommendations.add(vo);
            }
        }

        recommendations.sort((a, b) -> Double.compare(b.getMatchResult().getTotalScore(), a.getMatchResult().getTotalScore()));

        if (recommendations.size() > 10) {
            recommendations = recommendations.subList(0, 10);
        }

        log.info("推荐候选人完成，共推荐 {} 个候选人", recommendations.size());
        return recommendations;
    }

    private Map<String, Object> parseJson(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return new HashMap<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            Map<String, Object> result = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                result.put(key, jsonObject.get(key));
            }
            return result;
        } catch (Exception e) {
            log.error("JSON解析失败：{}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String getResumeEducation(Map<String, Object> data) {
        Object edu = data.get("education");
        return edu != null ? edu.toString() : "";
    }

    private String getJobEducation(Map<String, Object> data) {
        Object edu = data.get("eduReq");
        if (edu == null) {
            edu = data.get("educationRequirement");
        }
        return edu != null ? edu.toString() : "";
    }

    private String getResumeCity(Map<String, Object> data) {
        Object city = data.get("city");
        return city != null ? city.toString() : "";
    }

    private String getJobCity(Map<String, Object> data) {
        Object city = data.get("city");
        if (city == null) {
            city = data.get("workCity");
        }
        return city != null ? city.toString() : "";
    }

    private List<String> getResumeSkills(Map<String, Object> data) {
        Object skillsObj = data.get("skills");
        List<String> skills = new ArrayList<>();
        if (skillsObj instanceof JSONArray) {
            for (Object item : (JSONArray) skillsObj) {
                skills.add(item.toString().toLowerCase());
            }
        } else if (skillsObj instanceof List) {
            for (Object item : (List<?>) skillsObj) {
                skills.add(item.toString().toLowerCase());
            }
        } else if (skillsObj != null) {
            skills.add(skillsObj.toString().toLowerCase());
        }
        return skills;
    }

    private List<String> getJobSkills(Map<String, Object> data) {
        Object skillsObj = data.get("skillReq");
        if (skillsObj == null) {
            skillsObj = data.get("skillRequirements");
        }
        List<String> skills = new ArrayList<>();
        if (skillsObj instanceof JSONArray) {
            for (Object item : (JSONArray) skillsObj) {
                skills.add(item.toString().toLowerCase());
            }
        } else if (skillsObj instanceof List) {
            for (Object item : (List<?>) skillsObj) {
                skills.add(item.toString().toLowerCase());
            }
        } else if (skillsObj != null) {
            skills.add(skillsObj.toString().toLowerCase());
        }
        return skills;
    }

    private double calculateEducationScore(String resumeEdu, String jobEdu) {
        if (jobEdu == null || jobEdu.isEmpty() || "暂未提取到该信息".equals(jobEdu)) {
            return 20.0;
        }

        Map<String, Integer> eduLevel = new HashMap<>();
        eduLevel.put("博士", 5);
        eduLevel.put("硕士", 4);
        eduLevel.put("本科", 3);
        eduLevel.put("专科", 2);
        eduLevel.put("高中", 1);
        eduLevel.put("中专", 1);

        String normalizedJobEdu = normalizeEducation(jobEdu);
        String normalizedResumeEdu = normalizeEducation(resumeEdu);

        int resumeLevel = eduLevel.getOrDefault(normalizedResumeEdu, 0);
        int jobLevel = eduLevel.getOrDefault(normalizedJobEdu, 0);

        if (resumeLevel >= jobLevel) {
            return 20.0;
        }
        if (resumeLevel == jobLevel - 1) {
            return 10.0;
        }
        return 0.0;
    }

    private String normalizeEducation(String edu) {
        if (edu == null || edu.isEmpty()) {
            return "";
        }
        String lower = edu.toLowerCase();
        if (lower.contains("博士")) {
            return "博士";
        } else if (lower.contains("硕士") || lower.contains("研究生")) {
            return "硕士";
        } else if (lower.contains("本科") || lower.contains("学士")) {
            return "本科";
        } else if (lower.contains("专科") || lower.contains("大专")) {
            return "专科";
        } else if (lower.contains("高中")) {
            return "高中";
        } else if (lower.contains("中专")) {
            return "中专";
        }
        return edu;
    }

    private double calculateCityScore(String resumeCity, String jobCity) {
        if (jobCity == null || jobCity.isEmpty() || "暂未提取到该信息".equals(jobCity)) {
            return 20.0;
        }
        if (resumeCity == null || resumeCity.isEmpty() || "暂未提取到该信息".equals(resumeCity)) {
            return 0.0;
        }
        if (resumeCity.equals(jobCity)) {
            return 20.0;
        }
        if (resumeCity.length() >= 2 && jobCity.length() >= 2) {
            if (resumeCity.substring(0, 2).equals(jobCity.substring(0, 2))) {
                return 10.0;
            }
        }
        return 0.0;
    }

    private double calculateJaccardScore(List<String> resumeSkills, List<String> jobSkills) {
        if (resumeSkills == null || jobSkills == null) {
            return 0.0;
        }
        if (resumeSkills.isEmpty() && jobSkills.isEmpty()) {
            return 0.0;
        }
        Set<String> set1 = new HashSet<>(resumeSkills);
        Set<String> set2 = new HashSet<>(jobSkills);
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        if (union.isEmpty()) {
            return 0.0;
        }
        return (intersection.size() / (double) union.size()) * 30.0;
    }

    private double calculateCosineScore(List<String> resumeSkills, List<String> jobSkills) {
        if (resumeSkills == null || jobSkills == null) {
            return 0.0;
        }
        if (resumeSkills.isEmpty() || jobSkills.isEmpty()) {
            return 0.0;
        }

        Map<String, Integer> resumeFreq = new HashMap<>();
        Map<String, Integer> jobFreq = new HashMap<>();

        for (String skill : resumeSkills) {
            resumeFreq.put(skill.toLowerCase(), resumeFreq.getOrDefault(skill.toLowerCase(), 0) + 1);
        }
        for (String skill : jobSkills) {
            jobFreq.put(skill.toLowerCase(), jobFreq.getOrDefault(skill.toLowerCase(), 0) + 1);
        }

        Set<String> allSkills = new HashSet<>(resumeFreq.keySet());
        allSkills.addAll(jobFreq.keySet());

        double dotProduct = 0.0;
        double resumeNorm = 0.0;
        double jobNorm = 0.0;

        for (String skill : allSkills) {
            int rf = resumeFreq.getOrDefault(skill, 0);
            int jf = jobFreq.getOrDefault(skill, 0);
            dotProduct += rf * jf;
            resumeNorm += rf * rf;
            jobNorm += jf * jf;
        }

        if (resumeNorm == 0 || jobNorm == 0) {
            return 0.0;
        }

        return (dotProduct / (Math.sqrt(resumeNorm) * Math.sqrt(jobNorm))) * 30.0;
    }

    private String buildEducationMatchDetail(String resumeEdu, String jobEdu, double score) {
        if (jobEdu == null || jobEdu.isEmpty() || "暂未提取到该信息".equals(jobEdu)) {
            return "职位无学历要求，得分20/20";
        }
        String resume = resumeEdu != null ? resumeEdu : "未填写";
        return String.format("职位要求%s，求职者%s，得分%.0f/20", jobEdu, resume, score);
    }

    private String buildCityMatchDetail(String resumeCity, String jobCity, double score) {
        if (jobCity == null || jobCity.isEmpty() || "暂未提取到该信息".equals(jobCity)) {
            return "职位无城市要求，得分20/20";
        }
        String resume = resumeCity != null ? resumeCity : "未填写";
        if (score == 20) {
            return String.format("城市匹配，求职者期望%s，职位工作%s，得分20/20", resume, jobCity);
        } else if (score == 10) {
            return String.format("同省匹配，求职者期望%s，职位工作%s，得分10/20", resume, jobCity);
        } else {
            return String.format("城市不匹配，求职者期望%s，职位工作%s，得分0/20", resume, jobCity);
        }
    }

    private List<String> findCommonSkills(List<String> resumeSkills, List<String> jobSkills) {
        if (resumeSkills == null || jobSkills == null) {
            return new ArrayList<>();
        }
        Set<String> set1 = new HashSet<>(resumeSkills);
        Set<String> set2 = new HashSet<>(jobSkills);
        set1.retainAll(set2);
        return new ArrayList<>(set1);
    }

    private List<String> findMissingSkills(List<String> resumeSkills, List<String> jobSkills) {
        if (resumeSkills == null || jobSkills == null) {
            return new ArrayList<>();
        }
        Set<String> resumeSet = new HashSet<>(resumeSkills);
        Set<String> jobSet = new HashSet<>(jobSkills);
        jobSet.removeAll(resumeSet);
        return new ArrayList<>(jobSet);
    }
}