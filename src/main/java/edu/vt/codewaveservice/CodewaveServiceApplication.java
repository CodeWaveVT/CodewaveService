package edu.vt.codewaveservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodewaveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodewaveServiceApplication.class, args);
    }

}
