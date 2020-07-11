package com.amylover.common.utils.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能描述：TODO//JWT token载荷
 *
 * @Title: PlayLoad
 * @Author: zhangbin
 * @Date: 2020/6/25
 */
public class PlayLoad<T> implements Serializable {
    private String id;
    private T data;
    private Date expireTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
