package com.gec.controller;

import com.gec.components.FileTemplate;
import com.gec.dao.OptionMapper;
import com.gec.domain.vo.OptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Role")
public class RoleController extends BaseController {
    /* 1.自动装配 OptionMapper 接口. */
    @Autowired
    private OptionMapper optionMapper;

    /* 2.方法一 */
    /* (请在此处填入代码) */
    @GetMapping("/roleOptons")
    public R roleOptions(){
        List<OptionVO> ops = optionMapper.roleOptions();
        return R.ok(ops);
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
