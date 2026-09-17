package com.gec.domain.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gec.domain.entity.Dept;
import com.gec.domain.entity.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeptBO implements Node {
    private Integer id;
    private String deptName;     //dept_name
    private String deptDesc;     //dept_desc
    private Integer parentId;    //parent_id
    private String pIds;         //p_ids

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Node> children = new ArrayList<>();

    private Integer level;  //附加属性..
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }

    public DeptBO() {}
    public DeptBO(Dept d) {
        this.id = d.getId();
        this.deptName = d.getDeptName();
        this.parentId = d.getParentId();
        this.pIds = d.getPIds();
        this.deptDesc = d.getDeptDesc();
    }
    public void addChildNode(Node node){
        children.add( node );
    }
}


