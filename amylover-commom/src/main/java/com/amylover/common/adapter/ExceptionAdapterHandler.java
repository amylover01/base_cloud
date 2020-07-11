package com.amylover.common.adapter;

import com.amylover.common.exception.BusinessException;
import com.amylover.common.resullt.Rs;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 功能描述：TODO// 统一异常拦截处理器
 *
 * @Title: ExceptionAdapterHandler
 * @Author: zhangbin
 * @Date: 2020/6/24
 */
@RestControllerAdvice
public class ExceptionAdapterHandler {

    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception bs) {
        bs.printStackTrace();
        if (bs instanceof BusinessException) {
            return Rs.fail(((BusinessException) bs).getEnums());
        }
        return Rs.fail(bs.getMessage());
    }
}
