package com.gec.controller;

import com.gec.components.FileTemplate;
import com.gec.domain.entity.Dept;
import com.gec.domain.entity.Node;
import com.gec.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/Dept")
public class DeptController
    extends BaseController {
    //1.自动装配-DeptService
    @Autowired
    private IDeptService deptService;

    /* 1.部门列表. */
    @GetMapping(value = "/list")
    public R list(){
        List<Node> list =null;
        try{
            /*调用service的获取树形列表*/
            list = deptService.listDept();
            /*下发接口数据*/
            return R.ok(list);
        }catch(Exception e){
            e.printStackTrace();
            return R.err(e);
        }
    }
	/* 2.添加部门. */
    @PostMapping(value="/addDept")
    public R addDept(@RequestBody Dept dept){
        deptService.addDept(dept);
        return R.ok();
    }
    /* 3.更新部门. */
    @PutMapping(value="/updateDept")
    public R updateDept(@RequestBody Dept dept){
        deptService.updateDept(dept);
        return R.ok();
    }
    /* 4.获取部门. */
    @GetMapping(value="/getDept/{id}")
    public R getDept (@PathVariable("id") Integer id){
        Dept dept = deptService.getById(id);
        return R.ok(dept);
    }
    /* 5.删除部门-DELETE请求. */
    @DeleteMapping(value="/deleteDept/{id}")
    public R deleteDept(@PathVariable("id") Integer id){
        deptService.deleteDept(id);
        return R.ok();
    }
    @ExceptionHandler
    public void exceptionHandler(
            Exception e, HttpServletResponse resp){
        e.printStackTrace();
        R.err( e ).write( resp );
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
