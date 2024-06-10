package com.bootlabs.springbatch5mongodb.job;

import com.bootlabs.springbatch5mongodb.batch.config.MongoBatchConfigurer;
import com.bootlabs.springbatch5mongodb.config.MongodbProperties;
import com.bootlabs.springbatch5mongodb.domain.document.Trips;
import com.bootlabs.springbatch5mongodb.domain.model.TripCsvLine;
import com.bootlabs.springbatch5mongodb.job.step.TripItemProcessor;
import com.bootlabs.springbatch5mongodb.job.step.TripItemReader;
import com.bootlabs.springbatch5mongodb.job.step.TripItemWriter;
import com.bootlabs.springbatch5mongodb.job.step.TripStepListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.text.MessageFormat;

import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_CHUNK_SIZE;
import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_LIMIT_SIZE;


@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class JobConfig {
    private final MongodbProperties mongodbProperties;


//    @Bean
//    public DataSource getDataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.valueOf("MongoDb"))
//                .build();
//    }

    @Bean
    public MongoBatchConfigurer mongoBatchConfigurer() {
        return new MongoBatchConfigurer(mongodbProperties, new SimpleAsyncTaskExecutor());
    }

//    @Bean
//    public Datastore getDataSource() {
//        var connectionString = MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}?authSource=admin&ssl=false",
//                mongodbProperties.getUsername(), mongodbProperties.getPassword(), mongodbProperties.getHost(), mongodbProperties.getPort(), mongodbProperties.getDatabase());
//
//        Datastore datastore = Morphia.createDatastore(
//                MongoClients.create(connectionString), mongodbProperties.getDatabase());
//
//        // Required to create indices on database
//        datastore.getMapper().mapPackage(PackageMapper.class.getPackageName());
//        datastore.ensureIndexes();
//        return datastore;
//    }
//
//    @Bean
//    public MongoBatchConfigurer mongoBatchConfigurer() {
//        return new MongoBatchConfigurer(getDataSource(), new SimpleAsyncTaskExecutor());
//    }

    @Bean
    public Job tripJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                       MongoTemplate mongoTemplate) {

        return new JobBuilder("tripJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tripJobStep(jobRepository, transactionManager, mongoTemplate))
                .listener(new TripJobCompletionListener())
                .build();
    }

    @Bean
    public Step tripJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            MongoTemplate mongoTemplate) {
        return new StepBuilder("tripJobCSVGenerator", jobRepository)
                .startLimit(DEFAULT_LIMIT_SIZE)
                .<Trips, TripCsvLine>chunk(DEFAULT_CHUNK_SIZE, transactionManager)

                .reader(new TripItemReader(mongoTemplate))
                .processor(new TripItemProcessor())
                .writer(new TripItemWriter())
                .listener(new TripStepListener())
                .build();
    }

}


