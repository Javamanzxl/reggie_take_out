package com.zxl.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.zxl.common.JacksonObjectMapper;
import com.zxl.interceptor.BackendLoginInterceptor;
import com.zxl.interceptor.FrontLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/01/31 17:49
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private BackendLoginInterceptor backendLoginInterceptor;
    @Autowired
    private FrontLoginInterceptor frontLoginInterceptor;

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(backendLoginInterceptor).addPathPatterns("/backend/page/**").excludePathPatterns("/backend/page/login/login.html");
        registry.addInterceptor(frontLoginInterceptor).addPathPatterns("/front/page/**", "/front/index.html").excludePathPatterns("/front/page/login.html");
    }

    /**
     * 扩展mvc框架的消息转换器
     *
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson,讲java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //讲上面的消息转换器对象追加到mvc框架集合中
        converters.add(0, messageConverter);
    }

    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zxl.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
    }
}
