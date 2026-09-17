package com.gec.service;

/*
 * 主要用途: (通用节点)
 * 1. 处理树形节点的数据(解析, 封装)。
 * 2. 组织相关数据, 生成树形结构。
 * 3. 甚至可以实现查找功能。
 * 4. 对一个树形结构进行 增, 删, 改.
 *
 * [+]一级部门: 总经办
 *    [+]二级部门: 行政部
 *                人事部
 *    [+]三级部门
 *
 * [+]一级部门: 公司工会(委员会)
 *    [+]二级部门: 行政部
 * [+]一级部门: 董事会
 *
 */
import com.gec.domain.entity.Node;

import java.util.ArrayList;
import java.util.List;

public interface IBaseService {
    /*
    * 1.原本的 list 没有分层结构处理的。
    * 2.这里给它做一下分层处理, 转换为带分层结构的列表。
    * 一句话: 把线性结构 ==> 层次结构。
    */

    /* 1.请实现方法1. */
    default List<Node> convertNodeBO(
            List<? extends Node>list){
        /*定义一个顶层节点ID：0（虚拟节点）*/
        Integer topId = 0;
        /*定义第一层列表*/
        List oneList = new ArrayList<>();
        /*迭代原始列表*/
        for (Node node : list){
            /*获取每个节点的父*/
            Integer parId = node.getParentId();
            /**/
            if (parId.equals(topId)){
                Node nodeBO = copyObj(node);
                /**/
                findChildren(nodeBO,list);
                /**/
                oneList.add(nodeBO);
            }
        }
        return oneList;
    }


	/* 2.请实现方法2. */
    default void findChildren(
            Node D, List<? extends Node>list){
        /*获取D的ID作为parentID*/
        Integer parentId = D.getId();
        /*迭代原始列表*/
        for(Node node : list){
            Integer parId = node.getParentId();
            if(parId.equals(parentId)){
                Node nodeBO = copyObj(node);
                findChildren(nodeBO,list);
                D.addChildNode(nodeBO);
            }

        }
    }

    //这个方法是在实现类中实施。
    Node copyObj(Node node);
}
