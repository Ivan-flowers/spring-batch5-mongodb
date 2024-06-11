package com.bootlabs.springbatch5mongodb.batch.entity;

import com.bootlabs.springbatch5mongodb.batch.utils.BatchRepositoryUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.batch.core.*;

@Entity("JobExecution")
public class JobExecutionEntity {

    @Id
    private ObjectId _id;

    private int version;

    @Indexed
    private long jobExecutionId;

    @Indexed
    private long jobInstanceId;

    private String startTime;

    private String endTime;

    private String status;

    private String exitCode;

    private String exitMessage;

    private String createTime;

    private String lastUpdated;

    private Map<String, Object> jobParameters = new HashMap<>();

    public int getVersion() {
        return version;
    }

    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
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

    public String getCreateTime() {
        return createTime;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }


    public Map<String, Object> getJobParameters() {
        return jobParameters;
    }

    public static JobExecutionEntity toEntity(JobExecution jobExecution) {
        JobExecutionEntity jobExecutionEntity = new JobExecutionEntity();

        Map<String, Object> paramMap = BatchRepositoryUtils.convertToMap(jobExecution.getJobParameters());

//        jobExecutionEntity._id = new ObjectId(jobExecution.getId().toString());
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

    private static String getDateFromLocalDateTime(LocalDateTime localDateTime) {
        if(localDateTime == null) return "";
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Convert LocalDateTime to String
            String dateTimeString = localDateTime.format(formatter);

            return localDateTime.format(formatter);
        }
    }

    public static JobExecution fromEntity(JobExecutionEntity jobExecutionEntity) {
        if (jobExecutionEntity == null) {
            return null;
        }

        JobParameters jobParameters = BatchRepositoryUtils.convertFromMap(jobExecutionEntity.getJobParameters());

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

    private static LocalDateTime getLocalDateTimeFromDate(String date) {
        if(date == null) return LocalDateTime.now();

        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return LocalDateTime.parse(date, formatter);
        }
    }
}
