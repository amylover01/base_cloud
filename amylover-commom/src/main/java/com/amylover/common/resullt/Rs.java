package com.amylover.common.resullt;

import com.amylover.common.constant.RsCode;
import com.amylover.common.enums.base.ExceptionEnums;

import java.util.List;

/**
 * 功能描述：TODO// 统一返回结果集
 *
 * @Title: Rs
 * @Author: zhangbin
 * @Date: 2020/6/24
 */
public class Rs<T> {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public Rs(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Rs(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Rs(int code) {
        this.code = code;
    }

    /**
     * 功能描述： TODO// 分页返回
     *
     * @param pages 当前页
     * @param total 总条数
     * @param t     返回的数据
     * @Return: com.amylover.common.resullt.Rs<com.amylover.common.resullt.PageRs < T>>
     * @Author: zhangbin
     * @Data: 2020/6/24
     */
    public static <T> Rs<PageRs<T>> pageSuccess(long pages, long total, List<T> t) {
        return success(new PageRs<>(pages, total, t));
    }

    public static <T> Rs<T> success() {
        return new Rs<>(RsCode.HTTP_SUCCESS_CODE);
    }

    public static <T> Rs<T> success(T t) {
        return new Rs<>(RsCode.HTTP_SUCCESS_CODE, null, t);
    }

    public static <T> Rs<T> success(String msg, T t) {
        return new Rs<>(RsCode.HTTP_SUCCESS_CODE, msg, t);
    }

    public static <T> Rs<T> fail(String msg) {
        return new Rs<>(RsCode.HTTP_ERROR_CODE, msg);
    }

    public static <T> Rs<T> fail(ExceptionEnums ex) {
        return new Rs<>(ex.getCode(), ex.msg());
    }
}
