package com.gec.domain.entity;

public interface Node {
    default Integer getId(){ return null; }
    default Integer getParentId(){ return null; }
    default void addChildNode(Node node){}
    default Node copyNode(Node node){ return null; }
    default void clearEmptyChild(){ }

    //{2}新增两组方法
    default Integer getLevel(){ return null; }
    default void setLevel(Integer level){}

    default boolean getHasSub(){ return false; }
    default void setHasSub(boolean flag){}
}
