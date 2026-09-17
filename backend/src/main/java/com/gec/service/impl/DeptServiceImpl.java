package com.gec.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.DeptMapper;
import com.gec.domain.bo.DeptBO;
import com.gec.domain.entity.Dept;
import com.gec.domain.entity.Node;
import com.gec.service.IBaseService;
import com.gec.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl
    extends ServiceImpl<DeptMapper, Dept>
    implements IDeptService, IBaseService {
    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<Node> listDept() {
        /*--创建条件设置器--*/
        QueryWrapper<Dept>WR=new QueryWrapper<>();
        /*调用mybatis-plus内置方法，查询部门列表*/
        List<Dept>depts=deptMapper.selectList(WR);
        /*把原始数据变成树状层次结构数据*/
        List<Node>listBO=convertNodeBO(depts);
        /*返回listBO*/
        return listBO;
    }

    //在这里编写这个方法主要是为了适配不同类型。
    @Override
    public Node copyObj(Node N) {
        /*--请填入代码2--*/
        Dept dept=(Dept) N;
        /*把一个数据库的原生格式，扩展为增强类型*/
        DeptBO deptBO=new DeptBO(dept);
        return deptBO;
    }

    @Override
    public void deleteDept(Integer deptId) {
        /*--先拿到部门下的关联用户数，部门下有关联用户不能删除--*/
        int userCount = deptMapper.getUserCount(deptId);
        if(userCount!=0){
            throw new RuntimeException("此部门存在有关联用户，无法删除，");
        }
        /*判断部门下的子部门数有多少，部门下有子部门不能删除*/
        int deptCnt = deptMapper.getSubDeptCount(deptId);
        if (deptCnt!=0){
            throw new RuntimeException("此部门存在子级部门，无法删除。");
        }
        /*从数据表中删除部门信息*/
        boolean ret = removeById(deptId);
        if (!ret){
            throw new RuntimeException("删除部门出错");
        }
    }

    /*
    *  ID           父ID   PIDS(上代所有 ID)
    *  21   人事部    2     0,1,2
    *  200  下级部门  21    0,1,2,21  [新的PIDS]
    */
    private Dept setParentIds(Dept newDept){
       /*获取父部门*/
        Integer parID = newDept.getParentId();
        /*获取父部门完整信息*/
        Dept parDEPT = getById(parID);
        String pids = parDEPT.getPIds();
        /*拼接PIDS字符串*/
        newDept.setPIds(pids +","+ parID);
        return newDept;
    }

    @Override
    public void addDept(Dept dept) {
        /*--请填入代码5--*/
        Dept _dept = setParentIds(dept);
        boolean ret = saveOrUpdate(_dept);
        if (!ret){
            throw new RuntimeException("添加部门失败");
        }
    }

    @Override
    public void updateDept(Dept dept) {
        /*--请填入代码6--*/
        Dept _dept = setParentIds(dept);
        boolean ret = saveOrUpdate(_dept);
        if (!ret){
            throw new RuntimeException("更新部门失败");
        }
    }

    @Override
    public Dept getDept(Integer id) { return null; }
}
