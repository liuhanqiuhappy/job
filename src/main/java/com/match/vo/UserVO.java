package com.match.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String username;

    private Integer role;

    private String phone;

    private String city;
}