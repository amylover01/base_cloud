package com.amylover.common.resullt;

import java.util.List;

/**
 * 功能描述：TODO// 分页对象
 *
 * @Title: PageRs
 * @Author: zhangbin
 * @Date: 2020/6/24
 */
public class PageRs<T> {
    private long pages;
    private long total;
    private List<T> data;

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageRs(long pages, long total, List<T> data) {
        this.pages = pages;
        this.total = total;
        this.data = data;
    }
}
