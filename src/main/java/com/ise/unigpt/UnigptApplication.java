package com.ise.unigpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ise.unigpt.utils.SetProxy;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UnigptApplication {

	public static void main(String[] args) {
		SetProxy.setProxy();
		SpringApplication.run(UnigptApplication.class, args);
	}

}
