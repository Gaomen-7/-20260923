package com.gec.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

//@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject meta) {
        this.strictInsertFill( meta,
            "createDate", ()-> LocalDateTime.now(),
                LocalDateTime.class );
        this.strictInsertFill( meta,
            "updateDate", ()-> LocalDateTime.now(),
                LocalDateTime.class );
    }
    @Override
    public void updateFill(MetaObject meta) {
        this.strictUpdateFill( meta,
            "updateDate", ()-> LocalDateTime.now(),
                LocalDateTime.class);
    }
}
