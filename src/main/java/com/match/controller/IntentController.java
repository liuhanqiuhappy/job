package com.match.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.match.common.Result;
import com.match.dto.IntentVO;
import com.match.entity.Intent;
import com.match.entity.Job;
import com.match.entity.Resume;
import com.match.entity.User;
import com.match.mapper.JobMapper;
import com.match.mapper.ResumeMapper;
import com.match.mapper.UserMapper;
import com.match.service.IntentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intent")
public class IntentController {

    private static final Logger logger = LoggerFactory.getLogger(IntentController.class);

    @Autowired
    private IntentService intentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private JobMapper jobMapper;

    @PostMapping("/send")
    public Result<String> sendIntent(
            @RequestBody Map<String, Object> params,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        Integer role = (Integer) session.getAttribute("role");
        if (userId == null || role == null) {
            return Result.error("未登录");
        }

        Long toUserId = ((Number) params.get("toUserId")).longValue();
        Long resumeId = params.get("resumeId") != null ? ((Number) params.get("resumeId")).longValue() : null;
        Long jobId = params.get("jobId") != null ? ((Number) params.get("jobId")).longValue() : null;
        String message = (String) params.get("message");

        logger.info("发送意向请求: userId={}, role={}, toUserId={}, resumeId={}, jobId={}",
                userId, role, toUserId, resumeId, jobId);

        if (intentService.hasActiveIntent(userId, toUserId, resumeId, jobId)) {
            return Result.error("已发送过意向，请等待对方回应");
        }

        Intent intent = new Intent();
        intent.setFromUserId(userId);
        intent.setToUserId(toUserId);
        intent.setResumeId(resumeId);
        intent.setJobId(jobId);
        intent.setType(role);
        intent.setMessage(message);

        Date expireTime = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
        intent.setExpireTime(expireTime);

        intentService.sendIntent(intent);
        return Result.success("意向发送成功");
    }

    @PostMapping("/accept/{intentId}")
    public Result<String> acceptIntent(
            @PathVariable Long intentId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        Intent intent = intentService.getById(intentId);
        if (intent == null) {
            return Result.error("意向不存在");
        }

        if (!userId.equals(intent.getToUserId())) {
            return Result.error("无权操作");
        }

        intentService.acceptIntent(intentId);
        return Result.success("已接受意向，联系方式已解锁");
    }

    @PostMapping("/reject/{intentId}")
    public Result<String> rejectIntent(
            @PathVariable Long intentId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        Intent intent = intentService.getById(intentId);
        if (intent == null) {
            return Result.error("意向不存在");
        }

        if (!userId.equals(intent.getToUserId())) {
            return Result.error("无权操作");
        }

        intentService.rejectIntent(intentId);
        return Result.success("已拒绝");
    }

    @GetMapping("/my-sent")
    public Result<List<IntentVO>> getMySentIntents(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        List<Intent> intents = intentService.getSentIntents(userId);
        List<IntentVO> result = convertToVOList(intents, userId);
        return Result.success(result);
    }

    @GetMapping("/my-received")
    public Result<List<IntentVO>> getMyReceivedIntents(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        List<Intent> intents = intentService.getReceivedIntents(userId);
        List<IntentVO> result = convertToVOList(intents, userId);
        return Result.success(result);
    }

    @GetMapping("/contact/{intentId}")
    public Result<Map<String, String>> getContactInfo(
            @PathVariable Long intentId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        Intent intent = intentService.getById(intentId);
        if (intent == null) {
            return Result.error("意向不存在");
        }

        if (!userId.equals(intent.getFromUserId()) && !userId.equals(intent.getToUserId())) {
            return Result.error("意向未达成，无法查看联系方式");
        }

        if (!intent.getStatus().equals(1)) {
            return Result.error("意向未达成，无法查看联系方式");
        }

        Long targetUserId = userId.equals(intent.getFromUserId()) ? intent.getToUserId() : intent.getFromUserId();
        User targetUser = userMapper.selectById(targetUserId);

        Map<String, String> contact = new HashMap<>();
        contact.put("phone", targetUser.getPhone());
        contact.put("email", targetUser.getEmail());
        contact.put("username", targetUser.getUsername());

        return Result.success(contact);
    }

    private List<IntentVO> convertToVOList(List<Intent> intents, Long currentUserId) {
        List<IntentVO> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Intent intent : intents) {
            IntentVO vo = new IntentVO();
            vo.setId(intent.getId());
            vo.setFromUserId(intent.getFromUserId());
            vo.setToUserId(intent.getToUserId());
            vo.setType(intent.getType());
            vo.setTypeDesc(intent.getType() == 0 ? "意向书" : "面试邀约");
            vo.setStatus(intent.getStatus());
            vo.setStatusDesc(getStatusDesc(intent.getStatus()));
            vo.setMessage(intent.getMessage());

            User fromUser = userMapper.selectById(intent.getFromUserId());
            if (fromUser != null) {
                vo.setFromUsername(fromUser.getUsername());
                vo.setFromUserRole(fromUser.getRole() == 0 ? "个人" : "企业");
            }

            User toUser = userMapper.selectById(intent.getToUserId());
            if (toUser != null) {
                vo.setToUsername(toUser.getUsername());
            }

            if (intent.getResumeId() != null) {
                Resume resume = resumeMapper.selectById(intent.getResumeId());
                if (resume != null) {
                    User resumeUser = userMapper.selectById(resume.getUserId());
                    vo.setResumeTitle((resumeUser != null ? resumeUser.getUsername() : "") + "的简历");
                }
            }

            if (intent.getJobId() != null) {
                Job job = jobMapper.selectById(intent.getJobId());
                if (job != null) {
                    User jobUser = userMapper.selectById(job.getUserId());
                    vo.setJobTitle((jobUser != null ? jobUser.getUsername() : "") + "的职位");
                }
            }

            if (intent.getCreateTime() != null) {
                vo.setCreateTime(sdf.format(intent.getCreateTime()));
            }
            if (intent.getExpireTime() != null) {
                vo.setExpireTime(sdf.format(intent.getExpireTime()));
            }

            if (intent.getStatus().equals(1)) {
                Long targetUserId = currentUserId.equals(intent.getFromUserId()) ? intent.getToUserId() : intent.getFromUserId();
                User targetUser = userMapper.selectById(targetUserId);
                if (targetUser != null) {
                    vo.setContactInfo("已解锁");
                }
            }

            result.add(vo);
        }

        return result;
    }

    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待回应";
            case 1: return "已接受";
            case 2: return "已拒绝";
            case 3: return "已过期";
            default: return "未知";
        }
    }
}