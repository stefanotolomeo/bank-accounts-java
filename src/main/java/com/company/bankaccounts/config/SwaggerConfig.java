package com.company.bankaccounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// swagger-ui resources
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public Docket api() {
		//@formatter:off
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
              .apis(RequestHandlerSelectors.basePackage("com.company.bankaccounts.controller"))
              .paths(PathSelectors.any())
              .build()
            .apiInfo(apiInfo());
        //@formatter:on
	}

	private ApiInfo apiInfo() {
		//@formatter:off
        return new ApiInfoBuilder()
            .title("Bank-Accounts Application - RESTful API")
            .description("The RESTful APIs to check the status of the application. These are debugging/troubleshooting APIs only.")
            .build();
        //@formatter:on
	}
}
