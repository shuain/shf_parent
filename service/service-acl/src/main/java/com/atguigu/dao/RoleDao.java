package com.atguigu.dao;

import com.atguigu.base.BaseDao;
import com.atguigu.entity.Role;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface RoleDao extends BaseDao<Role> {
    List<Role> findAll();

    //List<Long> findRoleIdByAdminId(Long adminId);
}
