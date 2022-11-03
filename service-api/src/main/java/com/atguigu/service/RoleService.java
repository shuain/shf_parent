package com.atguigu.service;

import com.atguigu.base.BaseService;
import com.atguigu.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleService extends BaseService<Role> {
    List<Role> findAll();
    Map<String, Object> findRoleIdByAdminId(Long adminId);

    void saveUserRoleRealtionShip(Long adminId, Long[] roleIds);
}
