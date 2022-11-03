package com.atguigu.controller;

import com.atguigu.base.BaseController;
import com.atguigu.entity.Admin;
import com.atguigu.entity.HouseBroker;
import com.atguigu.service.AdminService;
import com.atguigu.service.HouseBrokerService;
import com.atguigu.service.RoleService;
import com.atguigu.util.QiniuUtil;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
    @DubboReference
    private AdminService adminService;
    @DubboReference
    private HouseBrokerService houseBrokerService;
    @DubboReference
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
    分页带条件查询用户信息的处理器
    BindingAwareModelMap put() addAttribute()
     */
    @RequestMapping
    public String index(ModelMap map, HttpServletRequest request){
        // 接收用户发过来的查询条件信息和分页信息
        // username name phone createTimeBegin createTimeEnd   pageNum pageSize
        Map<String, Object> filters = getFilters(request);
        // 调用服务层方法,获取PageInfo对象
        PageInfo<Admin> pageInfo = adminService.findPage(filters);
        // 将pageInfo对象放入请求域
        map.addAttribute("page",pageInfo);
        // 将用户填写的筛选条件也放入请求域
        map.addAttribute("filters",filters);
        // 渲染视图 admin/index
        return "admin/index";
    }

    /* 渲染用户新增视图*/
    @RequestMapping("/create")
    public String create(){
        return "admin/create";
    }

    /*保存员工信息进入数据库的处理器
     * 存储完毕后要渲染一个成功页
     * */
    @PostMapping("/save")
    public String save(Admin admin){
        // 接收表单提交的信息 封装成一个Admin对象
        //admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        // 调用服务层方法,将Admin存入数据库
        // 暂时将admin 的头像设置为一个固定链接,
        admin.setHeadUrl("http://47.93.148.192:8080/group1/M00/03/F0/rBHu8mHqbpSAU0jVAAAgiJmKg0o148.jpg");
        //将明文密码转换为密文
        String encodePassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodePassword);
        adminService.insert(admin);
        // 返回successPage视图
        return "common/successPage";
    }

    /*渲染 用户信息回显视图 */
    @RequestMapping("/edit/{id}")
    public String edit(ModelMap map,@PathVariable("id") Long id){
        // 接收要修改的用户的id
        // 根据id查找完整的用户信息
        Admin admin = adminService.getById(id);
        // 将用户信息放入请求域
        map.put("admin",admin);
        // 返回用户信息编辑视图
        return "admin/edit";
    }

    /*
     * 接收表单信息,将用户新的用户信息更新进入数据库
     * 渲染一个成功提示页
     * */
    @RequestMapping("/update")
    public String update(Admin admin){
        // 调用服务层方法将信息更新进入数据库
        adminService.update(admin);
        // 返回成功提示页视图
        return "common/successPage";
    }


    /*
     * 接收占位符方式参数 用户id
     * 根据id删除用户
     * 重新渲染用户信息展示页
     *
     * */
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id){
        // 调用服务层方法删除
        adminService.delete(id);
        // 重定向到 /admin
        return "redirect:/admin";
    }


    /*渲染文件上传的视图*/
    @RequestMapping("/uploadShow/{id}")
    public String uploadShow(
            @PathVariable("id")Long id,
            Model model
    ){
        model.addAttribute("id",id);
        return "admin/upload";
    }

    /*头像上传处理器
     * 上传成功后渲染成功提示视图
     * */
    @RequestMapping("/upload/{id}")
    public String upload(
            @PathVariable("id") Long id,
            @RequestParam("file")MultipartFile multipartFile
    ) throws Exception{
        String filename = UUID.randomUUID().toString();
        QiniuUtil.upload2Qiniu(multipartFile.getBytes(),filename);
        // 拼接图片的url
        String imageUrl ="http://rkfvpb3bm.hn-bkt.clouddn.com/"+filename;
        // 将图片头像更行到Admin中
        Admin admin =new Admin();
        admin.setId(id);
        admin.setHeadUrl(imageUrl);
        // 调用服务层方法,更新数据
        adminService.update(admin);

        // 同步更新房产经纪人的头像
        HouseBroker houseBroker =new HouseBroker();
        houseBroker.setBrokerId(id);
        houseBroker.setBrokerHeadUrl(imageUrl);
        houseBrokerService.updateBrokerHeadImgUrl(houseBroker);
        return "common/successPage";
    }

    @RequestMapping("/assignShow/{adminId}")
    public String assignShow(@PathVariable("adminId") Long adminId,ModelMap map){
        Map<String,Object> roleMap = roleService.findRoleIdByAdminId(adminId);
        map.addAllAttributes(roleMap);
        map.addAttribute("adminId",adminId);
        return "admin/assignShow";
    }

    @RequestMapping("/assignRole")
    public String assignRole(Long adminId, Long[] roleIds){
        roleService.saveUserRoleRealtionShip(adminId,roleIds);
        return "common/successPage";
    }

    /**
     * 获取当前登录信息
     * @return
     */
    @RequestMapping("/getInfo")
    @ResponseBody
    public Object getInfo() {
       Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getPrincipal();
    }
}