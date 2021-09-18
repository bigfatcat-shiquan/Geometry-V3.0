package com.bigfatcat.geometry3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bigfatcat.geometry3.dao")
public class Geometry3Application {

    public static void main(String[] args) {
        SpringApplication.run(Geometry3Application.class, args);
    }

}
