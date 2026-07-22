package com.docsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Docsy - 文档预览与编辑服务
 * 支持 Office/PDF/图片/音视频在线预览，Office 文件编辑，三方应用 iframe 集成
 */
@SpringBootApplication
@EnableScheduling
public class DocsyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocsyApplication.class, args);
    }
}
