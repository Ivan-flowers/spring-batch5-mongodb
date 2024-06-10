package com.bootlabs.springbatch5mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
/*@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories*/

@SpringBootApplication
public class SpringBatch5MongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatch5MongodbApplication.class, args);
	}

}
