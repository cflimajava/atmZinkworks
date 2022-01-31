package com.zinkworks.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AtmZinkworksApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtmZinkworksApplication.class, args);
	}

}
