package com.bootdo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableScheduling
@EnableAutoConfiguration(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
@EnableTransactionManagement
@ServletComponentScan
@MapperScan("com.bootdo.*.dao")
@SpringBootApplication
@EnableCaching
@Configuration // 申明这是一个配置
public class BootdoApplication {
	public static void main(String[] args) {
		SpringApplication.run(BootdoApplication.class, args);
		System.out.println(
				"                     .::::.\r\n" + 
				"                   .::::::::.\r\n" + 
				"                  :::::::::::\r\n" + 
				"              ..:::::::::::'\r\n" + 
				"            '::::::::::::'\r\n" + 
				"              .::::::::::\r\n" + 
				"         '::::::::::::::..\r\n" + 
				"              ..::::::::::::.\r\n" + 
				"            ``::::::::::::::::\r\n" + 
				"             ::::``:::::::::'        .:::.\r\n" + 
				"            ::::'   ':::::'       .::::::::.\r\n" + 
				"          .::::'      ::::     .:::::::'::::.\r\n" + 
				"         .:::'       :::::  .:::::::::' ':::::.\r\n" + 
				"        .::'        :::::.:::::::::'      ':::::.\r\n" + 
				"       .::'         ::::::::::::::'         ``::::.\r\n" + 
				"   ...:::           ::::::::::::'              ``::.\r\n" + 
				"  ```` ':.          ':::::::::'                  ::::..\r\n" + 
				"                     '.:::::'                    ':'````..\r\n" + 
				"");
	}
}
