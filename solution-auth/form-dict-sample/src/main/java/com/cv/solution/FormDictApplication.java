package com.cv.solution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: xutu
 * @since: 2025/9/26 13:22
 * <p>
 * 在主启动类或者配置类上加
 *
 */
@SpringBootApplication
@MapperScan( {
        "com.cv.solution.formdict.dict.mapper",
        "com.cv.solution.formdict.form.mapper"
})
public class FormDictApplication {
    public static void main(String[] args) {
        SpringApplication.run(FormDictApplication.class, args);
    }
}
