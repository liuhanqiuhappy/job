package com.match.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("job")
public class Job {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，显式映射数据库字段 user_id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 解析后的JSON内容（M3阶段暂不使用）
     */
    @TableField("parsed_json")
    private String parsedJson;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
