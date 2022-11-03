package com.atguigu.base;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

    /**
     * 保存一个实体类信息进入数据库的方法
    * */
    Integer insert(T t);

    void delete(Serializable id);

    Integer update(T t);

    T getById(Serializable id);
    /**
     * 根据条件分页查询表格数据的API
     * 用于配合分页查询
     * */
    Page<T> findPage(Map<String, Object> filters);


}
