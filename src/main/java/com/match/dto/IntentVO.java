package com.match.dto;

import lombok.Data;

@Data
public class IntentVO {
    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private String fromUserRole;
    private Long toUserId;
    private String toUsername;
    private Integer type;
    private String typeDesc;
    private Integer status;
    private String statusDesc;
    private String message;
    private String contactInfo;
    private String resumeTitle;
    private String jobTitle;
    private String createTime;
    private String expireTime;
}