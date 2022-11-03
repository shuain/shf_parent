package com.atguigu.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.Map;

public interface BaseService<T> {
    Integer insert(T t);

    void delete(Serializable id);

    Integer update(T t);

    T getById(Serializable id);
    /**
     * 根据条件分页查询表格数据的API
     * 用于配合分页查询
     * */
    PageInfo<T> findPage(Map<String, Object> filters);
}
