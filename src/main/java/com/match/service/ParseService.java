package com.match.service;

import java.util.Map;

public interface ParseService {

    String extractTextFromFile(String filePath);

    Map<String, Object> parseResume(String text);

    Map<String, Object> parseJob(String text);
}