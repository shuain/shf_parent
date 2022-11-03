package com.atguigu.service;

import com.atguigu.base.BaseDao;
import com.atguigu.base.BaseServiceImpl;
import com.atguigu.dao.PermissionDao;
import com.atguigu.dao.RolePermissionDao;
import com.atguigu.entity.Permission;
import com.atguigu.entity.RolePermission;
import com.atguigu.help.PermissionHelper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
@Transactional
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Override
    protected BaseDao<Permission> getEntityDao() {
        return permissionDao;
    }

    @Override
    public List<Map<String, Object>> findPermissionByRoleId(Long roleId) {
        //查找所有权限
        List<Permission> permissions = permissionDao.findAll();
        //获取角色已分配的权限数据
        List<Long> permissionIdList = rolePermissionDao.findPermissionIdListByRoleId(roleId);
        
        List<Map<String,Object>> zNodes = new ArrayList<>();
        for (Permission permission : permissions) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",permission.getId());
            map.put("pId", permission.getParentId());
            map.put("name", permission.getName());
            if(permissionIdList.contains(permission.getId())){
                map.put("checked", true);
            }
            zNodes.add(map);
        }
        return zNodes;
    }

   /* @Override
    public void saveRolePermissionRealtionShip(Long roleId, Long[] permissionIds) {
        rolePermissionDao.deleteByRoleId(roleId);

        for (Long permissionId : permissionIds) {
            //循环存储permissionIds中的权限信息
            if(StringUtils.isEmpty(permissionId)){
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionDao.insert(rolePermission);
            }

        }
    }*/
    @Override
    public void saveRolePermissionRealtionShip(Long roleId, Long[] permissionIds) {
        rolePermissionDao.deleteByRoleId(roleId);
        if(null != permissionIds && permissionIds.length>0){
            for(Long permissionId : permissionIds) {
                if(StringUtils.isEmpty(permissionId)) continue;
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionDao.insert(rolePermission);
            }
        }

    }

    @Override
    public List<Permission> findMenuPermissionByAdminId(Long adminId) {
        List<Permission> permissionList = null;
        //如果是超级管理员,查询所有权限
        if (adminId.longValue() == 1){
            permissionList = permissionDao.findAll();
        }else {
            permissionList = permissionDao.findListByAdminId(adminId);
        }
        List<Permission> result = PermissionHelper.bulid(permissionList);
        return result;
    }

    @Override
    public List<Permission> findAllMenu() {
        //全部权限列表
        List<Permission> permissionList = permissionDao.findAll();
        if(CollectionUtils.isEmpty(permissionList)) return null;

        //构建树形数据,总共三级
        //把权限数据构建成树形结构数据
        List<Permission> result = PermissionHelper.bulid(permissionList);
        return result;
    }

    @Override
    public List<String> findCodeListByAdminId(Long adminId) {
        //超级管理员admin账号id为：1
        if(adminId.longValue() == 1) {
            return permissionDao.findAllCodeList();
        }
        return permissionDao.findCodeListByAdminId(adminId);
    }
}
