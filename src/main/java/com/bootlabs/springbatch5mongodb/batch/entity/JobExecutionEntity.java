package com.bootlabs.springbatch5mongodb.batch.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

@Entity("JobExecution")
public class JobExecutionEntity {

    @Id
    private ObjectId _id;

    private int version;

    @Indexed
    private long jobExecutionId;

    @Indexed
    private long jobInstanceId;

    private Date startTime;

    private Date endTime;

    private String status;

    private String exitCode;

    private String exitMessage;

    private Date createTime;

    private Date lastUpdated;

    private JobParameters jobParameters = null;

    public int getVersion() {
        return version;
    }

    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    public JobParameters getJobParameters() {
        return jobParameters;
    }

    public static JobExecutionEntity toEntity(JobExecution jobExecution) {
        JobExecutionEntity jobExecutionEntity = new JobExecutionEntity();

        JobParameters paramMap = jobExecution.getJobParameters();

        jobExecutionEntity.version = jobExecution.getVersion();
        jobExecutionEntity.jobExecutionId = jobExecution.getId();
        jobExecutionEntity.startTime = getDateFromLocalDateTime(jobExecution.getStartTime());
        jobExecutionEntity.endTime = getDateFromLocalDateTime(jobExecution.getEndTime());
        jobExecutionEntity.status = jobExecution.getStatus().toString();
        jobExecutionEntity.exitCode = jobExecution.getExitStatus().getExitCode();
        jobExecutionEntity.exitMessage = jobExecution.getExitStatus().getExitDescription();
        jobExecutionEntity.jobParameters = paramMap;
        jobExecutionEntity.createTime = getDateFromLocalDateTime(jobExecution.getCreateTime());
        jobExecutionEntity.lastUpdated = getDateFromLocalDateTime(jobExecution.getLastUpdated());

        return jobExecutionEntity;
    }

    private static Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
        if(localDateTime == null) return null;
        else{
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    public static JobExecution fromEntity(JobExecutionEntity jobExecutionEntity) {
        if (jobExecutionEntity == null) {
            return null;
        }

        JobParameters jobParameters = jobExecutionEntity.getJobParameters();

        JobExecution jobExecution =
                new JobExecution(jobExecutionEntity.getJobExecutionId(), jobParameters);
        jobExecution.setStartTime(getLocalDateTimeFromDate(jobExecutionEntity.getStartTime()));
        jobExecution.setEndTime(getLocalDateTimeFromDate(jobExecutionEntity.getEndTime()));
        jobExecution.setStatus(BatchStatus.valueOf(jobExecutionEntity.getStatus()));
        jobExecution.setExitStatus(new ExitStatus(jobExecutionEntity.getExitCode()));

        jobExecution.setCreateTime(getLocalDateTimeFromDate(jobExecutionEntity.getCreateTime()));
        jobExecution.setLastUpdated(getLocalDateTimeFromDate(jobExecutionEntity.getLastUpdated()));
        jobExecution.setVersion(jobExecutionEntity.getVersion());

        return jobExecution;
    }

    private static LocalDateTime getLocalDateTimeFromDate(Date date) {
        if(date == null) return null;
        else{
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
    }
}
