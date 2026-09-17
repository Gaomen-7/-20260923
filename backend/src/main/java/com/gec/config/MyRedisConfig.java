package com.gec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class MyRedisConfig {

    @Bean(value="myJdkTemp")
    public RedisTemplate<String,Object> myJdkTemp(
        RedisConnectionFactory factory ){
        RedisTemplate<String, Object> temp = new RedisTemplate<>();
        temp.setConnectionFactory( factory );
        temp.setKeySerializer( new JdkSerializationRedisSerializer() );
        temp.setValueSerializer( new JdkSerializationRedisSerializer() );
        return temp;
    }

    @Bean(value="myJacksonTemp")
    public RedisTemplate<String,Object> myJacksonTemp(
            RedisConnectionFactory factory ){
        RedisTemplate<String, Object> temp = new RedisTemplate<>();
        temp.setConnectionFactory( factory );
        RedisSerializer valserializer = makeGenericJackon2Serializer();
        RedisSerializer keySerializer = makeStringSerializer();
        temp.setKeySerializer( keySerializer );
        temp.setValueSerializer( valserializer );

        temp.setHashKeySerializer( keySerializer );
        temp.setHashValueSerializer( valserializer );
        return temp;
    }

    public RedisSerializer makeGenericJackon2Serializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
    public RedisSerializer makeStringSerializer(){
        return new StringRedisSerializer();
    }
}


