package com.match.service.impl;

import com.match.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "llm.provider", havingValue = "cloud")
public class CloudLLMServiceImpl implements LLMService {

    @Value("${llm.cloud.api-key}")
    private String apiKey;

    @Value("${llm.cloud.url}")
    private String cloudUrl;

    @Override
    public String generate(String prompt) {
        log.info("使用云端LLM生成");
        log.info("API URL：{}", cloudUrl);
        log.info("Prompt长度：{}", prompt.length());
        
        try {
            return generateMockData(prompt);
        } catch (Exception e) {
            log.error("云端LLM调用失败", e);
            throw new RuntimeException("大模型服务不可用：" + e.getMessage());
        }
    }

    private String generateMockData(String prompt) {
        log.warn("大模型API调用失败，返回空数据");
        return "{}";
    }
}
