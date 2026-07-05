package com.match.dto;

import lombok.Data;

@Data
public class RecommendJobVO {

    private Long userId;

    private String username;

    private MatchResult matchResult;

    private Long jobId;

    private String filePath;
}