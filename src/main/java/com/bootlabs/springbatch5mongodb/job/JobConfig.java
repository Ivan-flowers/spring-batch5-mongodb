package com.bootlabs.springbatch5mongodb.job;

import com.bootlabs.springbatch5mongodb.batch.repository.PackageMapper;
import com.bootlabs.springbatch5mongodb.batch.repository.StepExecutionRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.JobInstanceRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.ExecutionContextRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.JobExecutionRepository;
import com.bootlabs.springbatch5mongodb.config.MongodbProperties;
import com.bootlabs.springbatch5mongodb.domain.document.Trips;
import com.bootlabs.springbatch5mongodb.domain.model.TripCsvLine;
import com.bootlabs.springbatch5mongodb.job.step.TripItemProcessor;
import com.bootlabs.springbatch5mongodb.job.step.TripItemReader;
import com.bootlabs.springbatch5mongodb.job.step.TripItemWriter;
import com.bootlabs.springbatch5mongodb.job.step.TripStepListener;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import dev.morphia.Morphia;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import java.text.MessageFormat;

import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_CHUNK_SIZE;
import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_LIMIT_SIZE;


@Configuration
@EnableBatchProcessing
public class JobConfig {
    private final ExecutionContextDao mongoExecutionContextDao;
    private final JobExecutionDao mongoJobExecutionDao;
    private final JobInstanceDao mongoJobInstanceDao;
    private final StepExecutionDao mongoStepExecutionDao;
    private final TaskExecutor taskExecutor;
    private final JobRepository jobRepository;


    public JobConfig() {

        var connectionString = "mongodb://admin_user:admin_pass@localhost:27018/sample_training?authSource=admin&ssl=false";

        Datastore datastore = Morphia.createDatastore(
                MongoClients.create(connectionString), "sample_training");

        // Required to create indices on database
        datastore.getMapper().mapPackage(PackageMapper.class.getPackageName());
        datastore.ensureIndexes();

        this.mongoExecutionContextDao = new ExecutionContextRepository(datastore);
        this.mongoJobExecutionDao = new JobExecutionRepository(datastore);
        this.mongoJobInstanceDao = new JobInstanceRepository(datastore);
        this.mongoStepExecutionDao = new StepExecutionRepository(datastore);
        this.taskExecutor = new SimpleAsyncTaskExecutor();
        this.jobRepository = new SimpleJobRepository(this.mongoJobInstanceDao, this.mongoJobExecutionDao, this.mongoStepExecutionDao, this.mongoExecutionContextDao);
    }


    @Bean
    public Datastore getDataSource() {
        var connectionString = MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}?authSource=admin&ssl=false",
                "admin_user", "admin_pass", "localhost", "27018", "sample_training");

        Datastore datastore = Morphia.createDatastore(
                MongoClients.create(connectionString), "sample_training");

        // Required to create indices on database
        datastore.getMapper().mapPackage(PackageMapper.class.getPackageName());
        datastore.ensureIndexes();
        return datastore;
    }

    @Bean
    public JobRepository getJobRepository() {
        return new SimpleJobRepository(this.mongoJobInstanceDao, this.mongoJobExecutionDao, this.mongoStepExecutionDao, this.mongoExecutionContextDao);
    }


    @Bean
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobLauncher getJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(this.getJobRepository());
        jobLauncher.setTaskExecutor(this.taskExecutor);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public JobExplorer getJobExplorer() throws Exception {
        return new SimpleJobExplorer(this.mongoJobInstanceDao, this.mongoJobExecutionDao, this.mongoStepExecutionDao, this.mongoExecutionContextDao);
    }

    @Bean
    public Job tripJob(PlatformTransactionManager transactionManager,
                       MongoTemplate mongoTemplate) {

        return new JobBuilder("tripJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tripJobStep(transactionManager, mongoTemplate))
                .listener(new TripJobCompletionListener())
                .build();
    }

    @Bean
    public Step tripJobStep(PlatformTransactionManager transactionManager,
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


