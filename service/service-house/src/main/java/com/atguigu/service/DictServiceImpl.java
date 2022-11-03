package com.atguigu.service;

import com.atguigu.dao.DictDao;
import com.atguigu.entity.Dict;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
@Transactional
public class DictServiceImpl implements DictService {
    @Autowired
    private DictDao dictDao;

    @Override
    public List<Map<String, Object>> findZnodes(Long id){
        //通过父id查询当前数据下面的所有子数据
        List<Dict> dictList = dictDao.findListByParentId(id);

        //准备封装数据的集合
        List<Map<String, Object>> znodes = new ArrayList<>();
        for (Dict dict : dictList) {
            Map<String, Object> zNode = new HashMap<>();
            zNode.put("id",dict.getId());
            zNode.put("name",dict.getName());
            //查询节点下是否有其他节点,判断是否够为父节点
            Integer count = dictDao.countIsParent(dict.getId());
            zNode.put("isParent",count > 0 ? true : false);
            znodes.add(zNode);
        }
        return znodes;
    }

    @Override
    public List<Dict> findListByParentId(Long parentId) {
        return dictDao.findListByParentId(parentId);
    }

    @Override
    public List<Dict> findListByDictCode(String dictCode) {
        Dict dict = dictDao.getByDictCode(dictCode);
        if(null == dict) return null;
        return this.findListByParentId(dict.getId());
    }

    @Override
    public String getNameById(Long id) {
        return dictDao.getNameById(id);
    }

}
