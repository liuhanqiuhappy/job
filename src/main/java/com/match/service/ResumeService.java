package com.match.service;

import com.match.entity.Resume;

import java.util.List;

public interface ResumeService {

    Resume getById(Long id);

    void updateParsedJson(Long id, String json);

    Resume getLatestByUserId(Long userId);

    List<Resume> findAll();
}