package com.gec.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Node;
import com.gec.domain.entity.Dept;

import java.util.List;

public interface IDeptService extends IService<Dept> {

    List<Node> listDept();

    void deleteDept(Integer id);

    void addDept(Dept dept);

    void updateDept(Dept dept);

    Dept getDept(Integer id);

}



