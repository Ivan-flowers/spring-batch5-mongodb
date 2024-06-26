package com.bootlabs.springbatch5mongodb.batch.entity;

import com.bootlabs.springbatch5mongodb.batch.utils.BatchRepositoryUtils;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

import java.util.HashMap;
import java.util.Map;

import static com.bootlabs.springbatch5mongodb.batch.utils.BatchConstants.JOB_KEY_GENERATOR;

@Entity("JobInstance")
public class JobInstanceEntity {
    @Id
    private ObjectId _id;

    private String jobName;

    private long jobInstanceId;

    private int version;

    @Indexed
    private String jobKey;

    private Map<String, Object> jobParameters = new HashMap<>();

    public String getJobName() {
        return jobName;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }

    public int getVersion() {
        return version;
    }

    public String getJobKey() {
        return jobKey;
    }

    public Map<String, Object> getJobParameters() {
        return jobParameters;
    }

    public static JobInstanceEntity toEntity(
            JobInstance jobInstance, final JobParameters jobParameters) {

        Map<String, Object> paramMap = BatchRepositoryUtils.convertToMap(jobParameters);

        JobInstanceEntity jobInstanceEntity = new JobInstanceEntity();

        jobInstanceEntity.jobInstanceId = jobInstance.getInstanceId();
        jobInstanceEntity.jobName = jobInstance.getJobName();
        jobInstanceEntity.jobKey = JOB_KEY_GENERATOR.generateKey(jobParameters);
        jobInstanceEntity.version = jobInstance.getVersion();
        jobInstanceEntity.jobParameters = paramMap;

        return jobInstanceEntity;
    }

    public static JobInstance fromEntity(JobInstanceEntity jobInstanceEntity) {
        if (jobInstanceEntity == null) {
            return null;
        }

        JobInstance jobInstance =
                new JobInstance(jobInstanceEntity.getJobInstanceId(), jobInstanceEntity.getJobName());

        /*
         * Instance version is incremented here in Spring Batch {@link org.springframework.batch.core.repository.dao.JdbcJobInstanceDao}
         * Not sure why though.
         */

        jobInstance.incrementVersion();

        return jobInstance;
    }
}
