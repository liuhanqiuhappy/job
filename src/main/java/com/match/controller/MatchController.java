package com.match.controller;

import com.match.common.Result;
import com.match.dto.MatchResult;
import com.match.dto.RecommendCandidateVO;
import com.match.dto.RecommendJobVO;
import com.match.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/calculate")
    public Result<MatchResult> calculateMatch(
            @RequestParam Long resumeId,
            @RequestParam Long jobId
    ) {
        MatchResult result = matchService.calculateMatch(resumeId, jobId);
        if (result == null) {
            return Result.error("匹配计算失败，简历或职位不存在");
        }
        return Result.success(result);
    }

    @GetMapping("/recommend/jobs")
    public Result<List<RecommendJobVO>> recommendJobs(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        List<RecommendJobVO> jobs = matchService.recommendJobs(userId);
        return Result.success(jobs);
    }

    @GetMapping("/recommend/candidates")
    public Result<List<RecommendCandidateVO>> recommendCandidates(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        List<RecommendCandidateVO> candidates = matchService.recommendCandidates(userId);
        return Result.success(candidates);
    }
}