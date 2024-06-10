package com.bootlabs.springbatch5mongodb.controllers;

import com.bootlabs.springbatch5mongodb.domain.enums.JobParametersKey;
import com.bootlabs.springbatch5mongodb.runner.ScheduledJobLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class LaunchJobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledJobLauncher.class);

    @Value("${batch.csv-path-directory}")
    private String pathDirectory;
    private final Job job;

    private final JobLauncher jobLauncher;

    LaunchJobController(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    // run every 2 min
    // @Scheduled(fixedRate = 120000)
    @GetMapping("/launchJob")
    void launchFileToJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException {
        LOGGER.info("Scheduled Starting job");

        JobParameters params = new JobParametersBuilder()
                .addLong(JobParametersKey.JOB_ID.getKey(), System.currentTimeMillis())
                .addDate(JobParametersKey.CURRENT_TIME.getKey(),new Date())
                .addString(JobParametersKey.PATH_DIRECTORY.getKey(), pathDirectory)
                .toJobParameters();

        jobLauncher.run(job, params);

        LOGGER.info("Scheduled Stopping job");
    }
}
