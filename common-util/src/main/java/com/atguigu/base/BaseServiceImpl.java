package com.atguigu.base;

import com.atguigu.util.CastUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

@Transactional
public abstract class BaseServiceImpl<T> implements BaseService<T>{
    //定义一个抽象方法，获取子类中的dao
    protected abstract BaseDao<T> getEntityDao();

    @Override
    public Integer insert(T t) {
        //插入的如果是角色就要用Roledao，继承当前类的service如果是roleserviceimpl
        // 插入的如果是用户就要用admaindao，继承当前类的service如果是roleserviceimpl


        return getEntityDao().insert(t);
    }

    @Override
    public void delete(Serializable id) {
        getEntityDao().delete(id);
    }

    @Override
    public Integer update(T t) {
        return getEntityDao().update(t);
    }

    @Override
    public T getById(Serializable id) {
        return getEntityDao().getById(id);
    }

    @Override
    public PageInfo<T> findPage(Map<String, Object> filters) {
        //当前页数
        int pageNum = CastUtil.castInt(filters.get("pageNum"), 1);
        //每页显示的记录条数
        int pageSize = CastUtil.castInt(filters.get("pageSize"), 10);
        //开启分页
        PageHelper.startPage(pageNum, pageSize);
        //进行查询

        return new PageInfo<T>(getEntityDao().findPage(filters), 10);
    }
}
