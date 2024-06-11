package com.bootlabs.springbatch5mongodb.batch.utils;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.util.HashMap;
import java.util.Map;

import static com.bootlabs.springbatch5mongodb.batch.utils.BatchConstants.DOT_ESCAPE_STRING;
import static com.bootlabs.springbatch5mongodb.batch.utils.BatchConstants.DOT_STRING;

public class BatchRepositoryUtils {
    public static Map<String, Object> convertToMap(JobParameters jobParameters) {
        // first clean the parameters, as we can't have "." within mongo field names
        Map<String, JobParameter<?>> jobParams = jobParameters.getParameters();
        Map<String, Object> paramMap = new HashMap<>(jobParams.size());
        for (Map.Entry<String, JobParameter<?>> entry : jobParams.entrySet()) {
            paramMap.put(
                    entry.getKey().replaceAll(DOT_STRING, DOT_ESCAPE_STRING), entry.getValue().getValue());
        }
        return paramMap;
    }

    public static JobParameters convertFromMap(Map<String, Object>  jobParameters) {
        // first clean the parameters, as we can't have "." within mongo field names
        Map<String, JobParameter<?>> jobParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : jobParameters.entrySet()) {
            jobParams.put(entry.getKey().replaceAll(DOT_ESCAPE_STRING, DOT_STRING), new JobParameter(entry.getValue(), entry.getValue().getClass()));
        }
        return new JobParameters(jobParams);
    }
}
