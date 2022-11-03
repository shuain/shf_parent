package com.atguigu.dao;

import com.atguigu.entity.Dict;

import java.util.List;

public interface DictDao  {

    List<Dict> findListByParentId(Long id);
    Integer countIsParent(long id);

    Dict getByDictCode(String dictCode);
    String getNameById(Long id);

}
