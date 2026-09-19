package com.gec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@MapperScan(
    basePackages={"com.gec.dao"}
)
public class MallAPP {
    public static void main(String[] args) {
        /* 1.启动　SpringBoot 主程序. */
        SpringApplication.run(MallAPP.class, args);
        System.out.println("+----------------------------------+");
        System.out.println("Spring Boot is RUNNING..");
        System.out.println("TIME:"+ (new Date()).toLocaleString());
        System.out.println("+----------------------------------+");
    }
}
    