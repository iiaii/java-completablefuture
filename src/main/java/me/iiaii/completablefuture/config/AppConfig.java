
package me.iiaii.completablefuture.config;

import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class AppConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(30);
        taskExecutor.setMaxPoolSize(30);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean(name = "jsonPlaceHolderApiCaller")
    public ApiCaller jsonPlaceHolderApiCaller() {
        return new ApiCaller("https://jsonplaceholder.typicode.com");
    }

    @Bean
    public SimpleKeyGenerator simpleKeyGenerator() {
        return new SimpleKeyGenerator();
    }

}