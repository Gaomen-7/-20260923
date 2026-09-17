package com.gec.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration                 //1.设置为配置类
@EnableTransactionManagement   //2.启用事务管理。。
public class MybatisConfig {
    //{1}分页插件。
    @Bean
    public MybatisPlusInterceptor mybatisIntercepter(){
        System.out.println("+--------------------[TRACE]--------------------+");
        System.out.println("{自动配置}创建了 MybatisPlus 拦截器..");
        System.out.println("+-----------------------------------------------+");
        MybatisPlusInterceptor it = new MybatisPlusInterceptor();
        //2. 设置分页拦截器。
        it.addInnerInterceptor(
            new PaginationInnerInterceptor(DbType.MYSQL)
        );
        return it;
    }
    //{2}元数据处理器。[全局配置器]
    @Bean
    public GlobalConfig globalConfig(){
        System.out.println("+--------------------[TRACE]--------------------+");
        System.out.println("{自动配置}创建了全局配置对象..");
        System.out.println("+-----------------------------------------------+");
        //{1}创建全局配置对象。
        GlobalConfig config = new GlobalConfig();
        //{2}设置元数据处理器
        MyMetaObjectHandler handler = new MyMetaObjectHandler();
        config.setMetaObjectHandler( handler );
        return config;
    }
}
