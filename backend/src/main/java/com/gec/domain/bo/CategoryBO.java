package com.gec.domain.bo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gec.domain.entity.BaseEntity;
import com.gec.domain.entity.Category;
import com.gec.domain.entity.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryBO extends BaseEntity implements Node {
    private Integer id;
    private String categoryName;  //category_name
    private Integer parentId;     //parent_id
    private String pIds;          //
    private Integer showStatus;
    private Integer sort;

    private Integer level;    /* 【 BO属性 】 */
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }

    private boolean hasSub = false;
    public boolean getHasSub(){
        if( children==null ) return false;
        return children.size() > 0;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Node> children = new ArrayList<>();

    public CategoryBO(){}
    public CategoryBO(Category ca){
        this.id = ca.getId();
        this.categoryName = ca.getCategoryName();
        this.parentId = ca.getParentId();
        this.pIds =  ca.getPIds();
        this.showStatus =  ca.getShowStatus();
        this.sort = ca.getSort();
        this.level = ca.getLevel();
    }

    public void addChildNode(Node node){
        children.add( node );
    }
}
