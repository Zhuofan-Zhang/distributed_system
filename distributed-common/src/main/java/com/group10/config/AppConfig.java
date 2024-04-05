package com.group10.config;

import feign.RequestInterceptor;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Configuration
@Data
public class AppConfig {


    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;


    @Value("${spring.redis.password}")
    private String redisPwd;



    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();

        config.useSingleServer().setPassword(redisPwd).setAddress("redis://"+redisHost+":"+redisPort);


        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        RedisSerializer redisSerializer = new StringRedisSerializer();

        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);

        return redisTemplate;
    }

    @Bean
    public RequestInterceptor requestInterceptor(){

        return template -> {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes!=null){

                HttpServletRequest httpServletRequest = attributes.getRequest();

                if(httpServletRequest == null){
                    return;
                }
                String token = httpServletRequest.getHeader("token");
                template.header("token",token);
            }
        };

    }



}
