package com.match.dto;

import lombok.Data;

import java.util.List;

@Data
public class DimensionResult {

    private Long id;
    private String name;
    private Double technicalDepth;
    private Double projectExp;
    private Double educationBg;
    private Double stability;
    private Double communication;
    private List<String> skillTags;
}