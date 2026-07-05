package com.match.dto;

import lombok.Data;

@Data
public class RecommendCandidateVO {

    private Long userId;

    private String username;

    private MatchResult matchResult;

    private Long resumeId;

    private String filePath;
}