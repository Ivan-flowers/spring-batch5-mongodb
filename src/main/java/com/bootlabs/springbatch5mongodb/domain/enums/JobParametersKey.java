package com.bootlabs.springbatch5mongodb.domain.enums;

import lombok.Getter;

@Getter
public enum JobParametersKey {

    //@// @formatter:off
    PATH_DIRECTORY("pathDirectory"),
    JOB_ID("jobId"),
    CURRENT_TIME("currentTime");
    // @formatter:on

    private final String key;

    JobParametersKey(String key) {
        this.key = key;
    }

}
