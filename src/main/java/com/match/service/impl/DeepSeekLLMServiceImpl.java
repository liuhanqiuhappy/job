package com.match.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.match.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "llm.provider", havingValue = "deepseek")
public class DeepSeekLLMServiceImpl implements LLMService {

    private final RestTemplate restTemplate;

    @Value("${llm.deepseek.api-key}")
    private String apiKey;

    @Value("${llm.deepseek.url}")
    private String apiUrl;

    @Value("${llm.deepseek.model}")
    private String model;

    public DeepSeekLLMServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(String prompt) {
        log.info("使用DeepSeek LLM生成，模型：{}", model);
        log.info("API URL：{}", apiUrl);
        log.info("Prompt长度：{}", prompt.length());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> messages = new HashMap<>();
            messages.put("role", "user");
            messages.put("content", prompt);

            List<Map<String, Object>> messagesList = new ArrayList<>();
            messagesList.add(messages);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messagesList);
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 1024);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.debug("DeepSeek请求体：{}", requestBody);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("DeepSeek响应状态码：{}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                log.debug("DeepSeek响应体：{}", responseBody);
                String content = parseResponse(responseBody);
                log.info("DeepSeek解析成功，返回内容长度：{}", content.length());
                return content;
            } else {
                log.error("DeepSeek API调用失败，状态码：{}，响应体：{}", response.getStatusCode(), response.getBody());
                log.warn("将使用模拟数据作为fallback");
                return generateMockData(prompt);
            }

        } catch (Exception e) {
            log.error("DeepSeek LLM调用失败", e);
            log.warn("将使用模拟数据作为fallback");
            return generateMockData(prompt);
        }
    }

    private String generateMockData(String prompt) {
        log.warn("大模型API调用失败，返回空数据");
        return "{}";
    }

    private String parseResponse(String responseBody) {
        try {
            JSONObject jsonResponse = JSONObject.parseObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("DeepSeek响应中choices数组为空");
            }
            
            JSONObject firstChoice = choices.getJSONObject(0);
            if (firstChoice == null) {
                throw new RuntimeException("DeepSeek响应中第一个choice为空");
            }
            
            JSONObject message = firstChoice.getJSONObject("message");
            if (message == null) {
                throw new RuntimeException("DeepSeek响应中message字段为空");
            }
            
            String content = message.getString("content");
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("DeepSeek响应中content字段为空");
            }
            
            return content.trim();
            
        } catch (RuntimeException e) {
            log.error("DeepSeek响应格式异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek响应解析失败", e);
            throw new RuntimeException("DeepSeek响应解析失败：" + e.getMessage());
        }
    }
}
