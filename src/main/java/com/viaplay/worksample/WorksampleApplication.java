package com.viaplay.worksample;

import com.viaplay.worksample.util.ApiUrlConfig;
import com.viaplay.worksample.util.ThreadPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableConfigurationProperties({ThreadPoolConfig.class, ApiUrlConfig.class})
@EnableAsync
@EnableCaching
public class WorksampleApplication {

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

	public static void main(String[] args) {
		SpringApplication.run(WorksampleApplication.class, args);
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(threadPoolConfig.getSize());
		executor.setMaxPoolSize(threadPoolConfig.getSize());
		executor.setQueueCapacity(threadPoolConfig.getCapacity());
		executor.setThreadNamePrefix(threadPoolConfig.getPrefix());
		executor.initialize();
		return executor;
	}
}
