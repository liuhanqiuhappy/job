package com.match.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.match.service.LLMService;
import com.match.service.ParseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ParseServiceImpl implements ParseService {

    @Autowired
    private LLMService llmService;

    private static final String RESUME_PROMPT_TEMPLATE = "你是一个严格的信息提取工具。从以下简历文本中提取信息，只返回一个合法的JSON对象，不要包含任何其他文字、解释或markdown标记（如```json）。\n" +
            "JSON格式必须为：{\"name\":\"姓名\", \"education\":\"学历\", \"skills\":[\"技能1\",\"技能2\"], \"experience\":数字(工作年限), \"city\":\"期望城市\"}。\n\n" +
            "文本内容：\n{text}";

    private static final String JOB_PROMPT_TEMPLATE = "你是一个严格的信息提取工具。从以下职位描述中提取信息，只返回一个合法的JSON对象，不要包含任何其他文字、解释或markdown标记（如```json）。\n" +
            "JSON格式必须为：{\"title\":\"职位名\", \"eduReq\":\"学历要求\", \"skillReq\":[\"技能1\"], \"expReq\":数字, \"city\":\"城市\"}。\n\n" +
            "文本内容：\n{text}";

    @Override
    public String extractTextFromFile(String filePath) {
        log.info("开始提取文件文本：{}", filePath);
        
        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("文件路径为空");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在：" + filePath);
        }

        String extension = getFileExtension(filePath).toLowerCase();
        log.info("文件扩展名：{}", extension);

        try {
            switch (extension) {
                case ".pdf":
                    return extractTextFromPdf(file);
                case ".doc":
                    return extractTextFromDoc(file);
                case ".docx":
                    return extractTextFromDocx(file);
                default:
                    throw new RuntimeException("不支持的文件格式：" + extension);
            }
        } catch (IOException e) {
            log.error("文件读取失败：{}", filePath, e);
            throw new RuntimeException("文件读取失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> parseResume(String text) {
        log.info("开始解析简历");
        String prompt = RESUME_PROMPT_TEMPLATE.replace("{text}", text);
        Map<String, Object> result = callLLMAndParse(prompt);
        
        result.put("name", result.getOrDefault("name", "暂未提取到该信息"));
        result.put("education", result.getOrDefault("education", "暂未提取到该信息"));
        result.put("city", result.getOrDefault("city", "暂未提取到该信息"));
        
        Object experienceObj = result.get("experience");
        int experience = 0;
        if (experienceObj instanceof Number) {
            experience = ((Number) experienceObj).intValue();
        } else if (experienceObj instanceof String) {
            try {
                experience = Integer.parseInt(((String) experienceObj).replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                experience = 0;
            }
        }
        result.put("experience", experience);
        
        Object skillsObj = result.get("skills");
        List<String> skills = new ArrayList<>();
        if (skillsObj instanceof JSONArray) {
            for (Object item : (JSONArray) skillsObj) {
                skills.add(item.toString());
            }
        } else if (skillsObj instanceof List) {
            for (Object item : (List<?>) skillsObj) {
                skills.add(item.toString());
            }
        } else if (skillsObj != null) {
            skills.add(skillsObj.toString());
        }
        result.put("skills", skills);
        
        log.info("简历解析结果（增强容错后）：{}", result);
        return result;
    }

    @Override
    public Map<String, Object> parseJob(String text) {
        log.info("开始解析职位");
        String prompt = JOB_PROMPT_TEMPLATE.replace("{text}", text);
        Map<String, Object> result = callLLMAndParse(prompt);
        
        Map<String, Object> mappedResult = new HashMap<>();
        
        mappedResult.put("positionName", result.getOrDefault("title", result.getOrDefault("positionName", "暂未提取到该信息")));
        mappedResult.put("educationRequirement", result.getOrDefault("eduReq", result.getOrDefault("educationRequirement", "暂未提取到该信息")));
        mappedResult.put("workCity", result.getOrDefault("city", result.getOrDefault("workCity", "暂未提取到该信息")));
        
        Object expReqObj = result.get("expReq");
        if (expReqObj == null) {
            expReqObj = result.get("experienceRequirement");
        }
        int expReq = 0;
        if (expReqObj instanceof Number) {
            expReq = ((Number) expReqObj).intValue();
        } else if (expReqObj instanceof String) {
            try {
                expReq = Integer.parseInt(((String) expReqObj).replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                expReq = 0;
            }
        }
        mappedResult.put("experienceRequirement", expReq);
        
        Object skillReqObj = result.get("skillReq");
        if (skillReqObj == null) {
            skillReqObj = result.get("skillRequirements");
        }
        List<String> skillReq = new ArrayList<>();
        if (skillReqObj instanceof JSONArray) {
            for (Object item : (JSONArray) skillReqObj) {
                skillReq.add(item.toString());
            }
        } else if (skillReqObj instanceof List) {
            for (Object item : (List<?>) skillReqObj) {
                skillReq.add(item.toString());
            }
        } else if (skillReqObj != null) {
            skillReq.add(skillReqObj.toString());
        }
        mappedResult.put("skillRequirements", skillReq);
        
        log.info("职位解析结果（增强容错后）：{}", mappedResult);
        return mappedResult;
    }

    private String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("PDF文本提取完成，长度：{}", text.length());
            return text;
        }
    }

    private String extractTextFromDoc(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            String text = extractor.getText();
            log.info("DOC文本提取完成，长度：{}", text.length());
            return text;
        }
    }

    private String extractTextFromDocx(File file) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    textBuilder.append(run.getText(0));
                }
                textBuilder.append("\n");
            }
        }
        String text = textBuilder.toString();
        log.info("DOCX文本提取完成，长度：{}", text.length());
        return text;
    }

    private Map<String, Object> callLLMAndParse(String prompt) {
        try {
            String response = llmService.generate(prompt);
            log.info("LLM返回结果长度：{}", response != null ? response.length() : 0);
            log.info("【大模型原始返回】：{}", response);
            
            String cleanResponse = cleanJsonResponse(response);
            log.info("【清洗后的JSON】：{}", cleanResponse);
            
            JSONObject jsonObject = JSON.parseObject(cleanResponse);
            
            Map<String, Object> result = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                result.put(key, jsonObject.get(key));
            }
            
            log.info("解析结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("LLM调用或解析失败", e);
            throw new RuntimeException("大模型解析失败：" + e.getMessage());
        }
    }

    private String cleanJsonResponse(String raw) {
        if (raw == null || raw.isEmpty()) {
            log.warn("LLM返回为空");
            return "{}";
        }
        
        String cleaned = raw;
        
        cleaned = cleaned.replaceAll("(?i)```json\\s*", "");
        cleaned = cleaned.replaceAll("```\\s*", "");
        
        cleaned = cleaned.trim();
        
        if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
            return extractBalancedJson(cleaned);
        } else {
            int firstBrace = cleaned.indexOf("{");
            if (firstBrace == -1) {
                log.warn("无法找到JSON起始符号，原始响应：{}", raw);
                return "{}";
            }
            String candidate = cleaned.substring(firstBrace);
            if (candidate.startsWith("{") && candidate.endsWith("}")) {
                return extractBalancedJson(candidate);
            }
            return extractBalancedJson(candidate);
        }
    }

    private String extractBalancedJson(String input) {
        if (input == null || input.isEmpty() || !input.startsWith("{")) {
            return "{}";
        }
        
        int balance = 0;
        int endIndex = -1;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (c == '"') {
                i++;
                while (i < input.length() && (input.charAt(i) != '"' || (i > 0 && input.charAt(i - 1) == '\\'))) {
                    i++;
                }
                continue;
            }
            
            if (c == '{') {
                balance++;
            } else if (c == '}') {
                balance--;
                if (balance == 0) {
                    endIndex = i;
                    break;
                }
            }
        }
        
        if (endIndex != -1) {
            return input.substring(0, endIndex + 1);
        } else {
            log.warn("JSON括号不平衡，返回原始内容");
            return input;
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}