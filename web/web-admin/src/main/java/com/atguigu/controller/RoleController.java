package com.atguigu.controller;

import com.atguigu.base.BaseController;
import com.atguigu.entity.Role;
import com.atguigu.service.PermissionService;
import com.atguigu.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @DubboReference
    private RoleService roleService;

    @DubboReference
    private PermissionService permissionService;

    /*视图名称通过静态常量的方式表示*/
    private static final String PAGE_INDEX="role/index";
    /*
     * 查询全部Role信息,渲染展示角色信息的视图
     * */
    /*@RequestMapping()
    public String index(ModelMap modelMap){
        List<Role> list = roleService.findAll();
        modelMap.addAttribute("list",list);
        return PAGE_INDEX;
    }*/
    @PreAuthorize("hasAuthority('role.show')")
    @RequestMapping()
    public String index(ModelMap modelMap, HttpServletRequest request){
        // 通过Request对象获取 Map<String,Object> filters 用于存放 查询的条件和 页码数 页大小
        Map<String, Object> filters = getFilters(request);
        // 调用服务层方法,查询分页信息
        PageInfo<Role> page = roleService.findPage(filters);
        modelMap.addAttribute("page",page);
        // 将搜索的条件放入请求域,供渲染时回显搜索条件
        modelMap.addAttribute("filters",filters);
        return PAGE_INDEX;
    }

    /*渲染角色信息输入的页面*/
    @PreAuthorize("hasAuthority('role.create')")
    @RequestMapping("/create")
    public String create(){
        return "role/create";
    }

    /*接收客户端提交过来的新增的Role的信息,将信息保存进入数据库
     * 渲染一个成功页面响应给客户端
     * */
    @PreAuthorize("hasAuthority('role.create')")
    @PostMapping("/save")
    public String save(Role role){
        // 接收请求中参数,将参数转成 一个Role对象
        // 调用服务层方法,将Role对象存入数据库
        roleService.insert(role);
        return "common/successPage";
    }

    /*
     * 接收用户的修改请求
     * 获得要修改的角色id
     * 根据id查询对应的role对象
     * role对象需要放入请求域
     * 渲染一个展示role信息的视图
     * */
    @PreAuthorize("hasAuthority('role.edit')")
    @GetMapping("/edit/{id}")
    public String edit(ModelMap modelMap,@PathVariable("id") Long id){
        // 调用服务层方法根据id查询role
        Role role=roleService.getById(id);
        // 将数据放入请求域
        modelMap.addAttribute("role",role);
        // 渲染展示员工信息的视图
        return "role/edit";
    }

    /*
     * 接收页面输入的要更新的role信息
     * 将role更新进入数据库
     * 渲染成功页
     * */
    @PreAuthorize("hasAuthority('role.edit')")
    @RequestMapping("/update")
    public String update(Role role){
        roleService.update(role);
        return "common/successPage";
    }

    /*
     * 删除角色处理器
     * 接收id
     * 删除完毕后,重定向 /role
     * */
    @PreAuthorize("hasAuthority('role.delete')")
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id){
        // 调用服务层方法删除角色
        roleService.delete(id);
        return "redirect:/role";
    }

    @PreAuthorize("hasAuthority('role.assgin')")
    @RequestMapping("/assignShow/{roleId}")
    public String assignShow(@PathVariable("roleId") Long roleId,ModelMap modelMap){
        List<Map<String,Object>> zNodes = permissionService.findPermissionByRoleId(roleId);
        modelMap.addAttribute("zNodes", zNodes);
        modelMap.addAttribute("roleId",roleId);
        return "role/assignShow";
    }
    @PreAuthorize("hasAuthority('role.assgin')")
    @RequestMapping("/assignPermission")
    public String assignPermission(Long roleId,Long[] permissionIds){
        permissionService.saveRolePermissionRealtionShip(roleId, permissionIds);
        return "common/successPage";
    }

}
