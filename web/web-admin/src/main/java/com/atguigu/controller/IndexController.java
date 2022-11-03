package com.atguigu.controller;

import com.atguigu.entity.Admin;
import com.atguigu.entity.Permission;
import com.atguigu.service.AdminService;
import com.atguigu.service.PermissionService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {
    @DubboReference
    private AdminService adminService;
    @DubboReference
    private PermissionService permissionService;

    /*渲染首页控制器*/
    @GetMapping("/")
    public String index(ModelMap modelMap){
        //后续替换为当前登录用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        Admin admin = adminService.getByUsername(username);

        List<Permission> permissionList = permissionService.findMenuPermissionByAdminId(admin.getId());
        modelMap.addAttribute("admin",admin);
        modelMap.addAttribute("permissionList",permissionList);
        return "frame/index";
    }

    /*渲染main页面的控制器*/
    @RequestMapping("/main")
    public String main(){
        return "frame/main";
    }
    /**
     * 添加了SpringSecurity之后去首页的方法
     * @return
     */
    @RequestMapping("/login")
    public String login(){
        return "frame/login";
    }

    @GetMapping("/auth")
    public String auth() {
        return "frame/auth";
    }
}
