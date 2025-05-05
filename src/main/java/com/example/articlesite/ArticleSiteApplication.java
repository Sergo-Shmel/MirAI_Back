package com.example.articlesite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ArticleSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleSiteApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins(
								"https://mirai-tech.ru",
								"https://zen.yandex.ru",
								"https://zen.yandex.com"
						)
						.allowedMethods("GET")
						.allowedHeaders("*")
						.exposedHeaders("Access-Control-Allow-Origin");
			}
		};
	}
}