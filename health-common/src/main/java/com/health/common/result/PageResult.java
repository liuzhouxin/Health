package com.health.common.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private Long total;
    private List<T> records;
    private Integer pageNo;
    private Integer pageSize;
    private Integer totalPages;

    public PageResult() {}

    public PageResult(Long total, List<T> records, Integer pageNo, Integer pageSize) {
        this.total = total;
        this.records = records;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}