package com.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchResult {

    private Double totalScore;

    private Double educationScore;

    private Double cityScore;

    private Double jaccardScore;

    private Double cosineScore;

    private String educationMatch;

    private String cityMatch;

    private List<String> commonSkills;

    private List<String> missingSkills;

    private Double resumeId;

    private Double jobId;
}