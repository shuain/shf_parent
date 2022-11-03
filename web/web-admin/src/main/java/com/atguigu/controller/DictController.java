package com.atguigu.controller;

import com.atguigu.entity.Dict;
import com.atguigu.result.Result;
import com.atguigu.service.DictService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dict")
public class DictController {

    @DubboReference
    private DictService dictService;

    @GetMapping
    public String index(ModelMap model) {
        return "dict/index";
    }

    /*接收父ID，根据父ID查询所有子数据，做异步响应处理器*/
    @RequestMapping("/findZnodes")
    @ResponseBody
    public Result<List<Map<String, Object>>> findByParentId(@RequestParam(value = "id" ,defaultValue = "0") Long id){
        List<Map<String,Object>> znodes = dictService.findZnodes(id);
        Result<List<Map<String, Object>>> result = Result.ok(znodes);
        return result;
    }
    /**
     * 根据上级id获取子节点数据列表
     * @param parentId
     * @return
     */
    @GetMapping(value = "findListByParentId/{parentId}")
    @ResponseBody
    public Result<List<Dict>> findListByParentId(@PathVariable Long parentId) {
        List<Dict> list = dictService.findListByParentId(parentId);
        return Result.ok(list);
    }

    /**
     * 根据编码获取子节点数据列表
     * @param dictCode
     * @return
     */
    @GetMapping(value = "findListByDictCode/{dictCode}")
    @ResponseBody
    public Result<List<Dict>> findListByDictCode(@PathVariable String dictCode) {
        List<Dict> list = dictService.findListByDictCode(dictCode);
        return Result.ok(list);
    }

}
