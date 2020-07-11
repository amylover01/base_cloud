package com.amylover.common.enums.exenums;

import com.amylover.common.constant.RsCode;
import com.amylover.common.enums.base.ExceptionEnums;

/**
 * 功能描述：TODO//公用异常枚举类
 *
 * @Title: SystemEnums
 * @Author: zhangbin
 * @Date: 2020/6/24
 */
public enum SystemEnums implements ExceptionEnums {
    SYSTEM_ERROR(RsCode.HTTP_ERROR_CODE, "系统异常");

    private int code;
    private String msg;

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String msg() {
        return this.msg;
    }

    SystemEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
