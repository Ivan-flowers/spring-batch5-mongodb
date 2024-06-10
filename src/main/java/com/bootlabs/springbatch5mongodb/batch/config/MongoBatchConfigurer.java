package com.bootlabs.springbatch5mongodb.batch.config;

import com.bootlabs.springbatch5mongodb.batch.repository.PackageMapper;
import com.bootlabs.springbatch5mongodb.batch.repository.ExecutionContextRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.JobExecutionRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.JobInstanceRepository;
import com.bootlabs.springbatch5mongodb.batch.repository.StepExecutionRepository;
import com.bootlabs.springbatch5mongodb.config.MongodbProperties;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;


//@Configuration
//@EnableBatchProcessing
public class MongoBatchConfigurer{

    private final ExecutionContextDao mongoExecutionContextDao;
    private final JobExecutionDao mongoJobExecutionDao;
    private final JobInstanceDao mongoJobInstanceDao;
    private final StepExecutionDao mongoStepExecutionDao;

    private final TaskExecutor taskExecutor;

    /**
     * Instantiates the Mongo DAO implementations with the provided datastore
     *
     */
    public MongoBatchConfigurer(MongodbProperties mongodbProperties, TaskExecutor taskExecutor) {
       var connectionString = MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}?authSource=admin&ssl=false",
       mongodbProperties.getUsername(), mongodbProperties.getPassword(), mongodbProperties.getHost(), mongodbProperties.getPort(), mongodbProperties.getDatabase());

        Datastore datastore = Morphia.createDatastore(
                MongoClients.create(connectionString), mongodbProperties.getDatabase());

        // Required to create indices on database
        datastore.getMapper().mapPackage(PackageMapper.class.getPackageName());
        datastore.ensureIndexes();

        this.mongoExecutionContextDao = new ExecutionContextRepository(datastore);
        this.mongoJobExecutionDao = new JobExecutionRepository(datastore);
        this.mongoJobInstanceDao = new JobInstanceRepository(datastore);
        this.mongoStepExecutionDao = new StepExecutionRepository(datastore);
        this.taskExecutor = taskExecutor;
    }

//    @Bean
    public JobRepository getJobRepository() throws Exception {
        return new SimpleJobRepository(
                mongoJobInstanceDao, mongoJobExecutionDao, mongoStepExecutionDao, mongoExecutionContextDao);
    }

//    @Bean
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return new ResourcelessTransactionManager();
    }

//    @Bean
    public JobLauncher getJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new  TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.setTaskExecutor(taskExecutor);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

//    @Bean
    public JobExplorer getJobExplorer() throws Exception {
        return new SimpleJobExplorer(
                mongoJobInstanceDao, mongoJobExecutionDao, mongoStepExecutionDao, mongoExecutionContextDao);
    }
}
