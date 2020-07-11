package com.amylover.common.exception;

import com.amylover.common.enums.base.ExceptionEnums;

/**
 * 功能描述：TODO//
 *
 * @Title: BusinessException
 * @Author: zhangbin
 * @Date: 2020/6/24
 */
public class BusinessException extends RuntimeException {

    private ExceptionEnums enums;

    public BusinessException(ExceptionEnums enums) {
        super(enums.msg());
        this.enums = enums;
    }

    public BusinessException(String message) {
        super(message);
    }

    public ExceptionEnums getEnums() {
        return enums;
    }

    public void setEnums(ExceptionEnums enums) {
        this.enums = enums;
    }
}
