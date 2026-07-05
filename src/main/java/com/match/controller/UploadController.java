package com.match.controller;

import com.match.common.Result;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.mapper.JobMapper;
import com.match.mapper.ResumeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private JobMapper jobMapper;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final String[] ALLOWED_EXTENSIONS = {".doc", ".docx", ".pdf"};

    @PostMapping("/upload")
    public Result<Long> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            HttpSession session
    ) {
        try {
            // 用户身份验证：从Session获取userId
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                log.warn("上传失败：用户未登录");
                return Result.error("请先登录");
            }
            log.info("用户ID {} 开始上传文件，类型：{}", userId, type);

            // 文件校验：检查文件是否为空
            if (file == null || file.isEmpty()) {
                log.warn("上传失败：未选择文件");
                return Result.error("请选择文件");
            }

            // 文件校验：检查文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("上传失败：文件大小超过10MB，实际大小：{}", file.getSize());
                return Result.error("文件大小不能超过10MB");
            }

            // 文件校验：检查文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                log.warn("上传失败：文件名为空");
                return Result.error("文件名不能为空");
            }

            // 文件类型校验：统一转换为小写后比较，避免大小写敏感问题
            String extension = getFileExtension(originalFilename);
            if (!isValidExtension(extension)) {
                log.warn("上传失败：文件格式不支持，文件名：{}，后缀：{}", originalFilename, extension);
                return Result.error("只支持 .doc、.docx、.pdf 格式的文件");
            }
            log.info("文件校验通过，文件名：{}，大小：{}，类型：{}", originalFilename, file.getSize(), extension);

            // 生成保存路径：使用System.getProperty("user.dir")获取项目根目录
            // 路径格式：{项目根目录} + File.separator + "uploads" + File.separator + {日期子目录}
            String projectRoot = System.getProperty("user.dir");
            String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            // 使用Paths.get构建跨平台兼容的绝对路径
            Path uploadDirPath = Paths.get(projectRoot, "uploads", dateDir);
            log.info("上传目录路径：{}", uploadDirPath.toAbsolutePath());

            // 使用Files.createDirectories确保目标目录存在
            // 目录创建失败时返回明确的错误信息
            try {
                if (!Files.exists(uploadDirPath)) {
                    Files.createDirectories(uploadDirPath);
                    log.info("创建上传目录成功：{}", uploadDirPath.toAbsolutePath());
                }
            } catch (IOException e) {
                log.error("目录创建失败：{}", uploadDirPath, e);
                return Result.error("目录创建失败：" + e.getMessage());
            }

            // 生成唯一文件名：UUID + 原始后缀
            String newFilename = UUID.randomUUID().toString() + extension;
            Path targetPath = uploadDirPath.resolve(newFilename);
            log.info("文件将保存到：{}", targetPath.toAbsolutePath());

            // 保存文件到本地
            file.transferTo(targetPath.toFile());
            log.info("文件保存成功：{}", targetPath.toAbsolutePath());

            // 根据类型执行数据库操作，使用targetPath.toString()确保路径格式一致
            String filePath = targetPath.toString();
            Long recordId = null;
            if ("resume".equals(type)) {
                Resume resume = new Resume();
                resume.setUserId(userId);
                resume.setFilePath(filePath);
                resume.setCreateTime(new Date());
                int rows = resumeMapper.insert(resume);
                log.info("简历记录插入成功，受影响行数：{}，记录ID：{}", rows, resume.getId());
                if (rows <= 0) {
                    log.error("简历记录插入失败，受影响行数：{}", rows);
                    return Result.error("数据库插入失败");
                }
                recordId = resume.getId();
            } else if ("job".equals(type)) {
                Job job = new Job();
                job.setUserId(userId);
                job.setFilePath(filePath);
                job.setCreateTime(new Date());
                int rows = jobMapper.insert(job);
                log.info("职位记录插入成功，受影响行数：{}，记录ID：{}", rows, job.getId());
                if (rows <= 0) {
                    log.error("职位记录插入失败，受影响行数：{}", rows);
                    return Result.error("数据库插入失败");
                }
                recordId = job.getId();
            } else {
                log.warn("上传失败：无效的文件类型，类型：{}", type);
                return Result.error("无效的文件类型");
            }

            log.info("用户ID {} 文件上传完成，类型：{}，文件名：{}", userId, type, newFilename);
            return Result.success(recordId);

        } catch (IOException e) {
            // 文件保存相关异常，记录完整堆栈信息
            log.error("文件上传失败：IOException", e);
            return Result.error("文件保存失败：" + e.getMessage());
        } catch (Exception e) {
            // 其他未知异常，记录完整堆栈信息
            log.error("文件上传失败：Exception", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名，统一转换为小写
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 校验文件扩展名是否在允许列表中
     */
    private boolean isValidExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
