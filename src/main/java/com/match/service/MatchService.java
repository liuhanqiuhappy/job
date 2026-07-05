package com.match.service;

import com.match.dto.MatchResult;
import com.match.dto.RecommendCandidateVO;
import com.match.dto.RecommendJobVO;

import java.util.List;

public interface MatchService {

    MatchResult calculateMatch(Long resumeId, Long jobId);

    List<RecommendJobVO> recommendJobs(Long userId);

    List<RecommendCandidateVO> recommendCandidates(Long userId);
}