package cn.wtyoha.miaosha.config;

import cn.wtyoha.miaosha.iterceptor.HtmlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    HtmlInterceptor htmlInterceptor;

//    @Bean
    public WebMvcConfigurer webMvcConfigurerAdaptor() {
        return new WebMvcConfigurer() {
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(htmlInterceptor).addPathPatterns("/*.html");
            }
        };
    }
}
