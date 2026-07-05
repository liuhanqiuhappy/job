package com.match.service;

import com.match.entity.Job;

import java.util.List;

public interface JobService {

    Job getById(Long id);

    void updateParsedJson(Long id, String json);

    Job getLatestByUserId(Long userId);

    List<Job> findAll();
}