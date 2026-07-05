package com.match.service.impl;

import com.alibaba.fastjson.JSON;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ParseServiceImpl implements ParseService {

    @Autowired
    private LLMService llmService;

    private static final String RESUME_PROMPT_TEMPLATE = "从以下简历文本中提取：姓名、学历（字符串）、技能列表（数组）、工作年限（数字）、期望城市（字符串）。返回合法JSON格式，不要有其他文字。\n\n文本：{text}";

    private static final String JOB_PROMPT_TEMPLATE = "从以下职位描述中提取：职位名称、学历要求（字符串）、技能要求（数组）、工作年限要求（数字）、工作城市（字符串）。返回合法JSON格式，不要有其他文字。\n\n文本：{text}";

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
        return callLLMAndParse(prompt);
    }

    @Override
    public Map<String, Object> parseJob(String text) {
        log.info("开始解析职位");
        String prompt = JOB_PROMPT_TEMPLATE.replace("{text}", text);
        return callLLMAndParse(prompt);
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
            
            String cleanResponse = cleanJsonResponse(response);
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

    private String cleanJsonResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "{}";
        }
        
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        
        if (start == -1 || end == -1 || start > end) {
            log.warn("无法提取JSON内容，原始响应：{}", response);
            return "{}";
        }
        
        return response.substring(start, end + 1);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}