package com.atguigu.controller;

import com.atguigu.entity.House;
import com.atguigu.entity.HouseImage;
import com.atguigu.result.Result;
import com.atguigu.service.HouseImageService;
import com.atguigu.service.HouseService;
import com.atguigu.util.QiniuUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/houseImage")
public class HouseImageController {

    @DubboReference
    private HouseService houseService;

    @DubboReference
    private HouseImageService houseImageService;

    private final static String LIST_ACTION = "redirect:/house/";
    private final static String PAGE_UPLOED_SHOW = "house/upload";

    @RequestMapping("/uploadShow/{houseId}/{type}")
    public String uploadShow(ModelMap model,@PathVariable Long houseId, @PathVariable Long type) {
        model.addAttribute("houseId",houseId);
        model.addAttribute("type",type);
        return PAGE_UPLOED_SHOW;
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
            String url ="http://rkfvpb3bm.hn-bkt.clouddn.com/"+filename;

            HouseImage houseImage  =new HouseImage();
            houseImage.setHouseId(houseId);
            houseImage.setImageName(filename);
            houseImage.setType(type);
            houseImage.setImageUrl(url);
            houseImageService.insert(houseImage);
            // 默认本次上传的第一个房源屠天设置为house表格中的默认图片
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

    /**
     * 删除
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/delete/{houseId}/{id}")
    public String delete(ModelMap model, @PathVariable Long houseId, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        HouseImage houseImage = houseImageService.getById(id);
        houseImageService.delete(id);
        QiniuUtil.deleteFileFromQiniu(houseImage.getImageName());
        return LIST_ACTION + houseId;
    }

}
