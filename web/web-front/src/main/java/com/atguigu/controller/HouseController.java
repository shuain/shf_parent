package com.atguigu.controller;

import com.atguigu.entity.*;
import com.atguigu.result.Result;
import com.atguigu.service.*;
import com.atguigu.util.QiniuUtil;
import com.atguigu.vo.HouseQueryVo;
import com.atguigu.vo.HouseVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/house")
public class HouseController {

    @DubboReference
    private HouseService houseService;

    @DubboReference
    private HouseImageService houseImageService;

    @DubboReference
    private CommunityService communityService;

    @DubboReference
    private DictService dictService;

    @DubboReference
    private HouseBrokerService houseBrokerService;

    @DubboReference
    private UserFollowService userFollowService;

    @RequestMapping("/list/{pageNum}/{pageSize}")
    public Result<PageInfo<HouseVo>> findListPage(@PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize,
                                                  @RequestBody HouseQueryVo houseQueryVo){
        PageInfo<HouseVo> pageInfo = houseService.findListPage(pageNum, pageSize, houseQueryVo);
        return Result.ok(pageInfo);
    }


    @RequestMapping("/upload/{houseId}/{type}")
    @ResponseBody
    public Result upload(
            @PathVariable("houseId") Long  houseId,
            @PathVariable("type") Integer  type,
            @RequestParam("file") MultipartFile[] multipartFiles,
            Model model
    ) throws IOException {
        for (int i = 0; i < multipartFiles.length; i++) {

            MultipartFile multipartFile =multipartFiles[i];
            String filename= UUID.randomUUID().toString();
            QiniuUtil.upload2Qiniu(multipartFile.getBytes(),filename);
            String url ="http://rga8k5ezp.hn-bkt.clouddn.com/"+filename;

            HouseImage houseImage  =new HouseImage();
            houseImage.setHouseId(houseId);
            houseImage.setImageName(filename);
            houseImage.setType(type);
            houseImage.setImageUrl(url);


            houseImageService.insert(houseImage);
            if (i==0  && type==1){
                // 更新房源默认图片
                House house =new House();
                house.setId(houseId);
                house.setDefaultImageUrl(url);
                houseService.update(house);
            }


        }
        return Result.ok();
    }

    /*前台获取房源详情的处理器*/
    @RequestMapping("/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id, HttpServletRequest request){
        House house = houseService.getById(id);
        Community community = communityService.getById(house.getCommunityId());
        List<HouseBroker> houseBrokerList = houseBrokerService.findListByHouseId(id);
        List<HouseImage> houseImage1List = houseImageService.findList(id, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("house",house);
        map.put("community",community);
        map.put("houseBrokerList",houseBrokerList);
        map.put("houseImage1List",houseImage1List);

        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("USER");
        Boolean isFollow = false;
        if(null != userInfo) {
            Long userId = userInfo.getId();
            isFollow = userFollowService.isFollowed(userId, id);
        }
        map.put("isFollow",isFollow);
        return Result.ok(map);
    }
}
