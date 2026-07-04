package com.match.common;

/**
 * 统一返回结果类
 *
 * @param <T> 数据类型
 */
public class Result<T> {

    /** 状态码 */
    private Integer code;

    /** 消息提示 */
    private String msg;

    /** 返回数据 */
    private T data;

    /**
     * 无参构造方法
     */
    public Result() {
    }

    /**
     * 全参构造方法
     *
     * @param code 状态码
     * @param msg  消息提示
     * @param data 返回数据
     */
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "成功", data);
    }

    /**
     * 失败返回
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}