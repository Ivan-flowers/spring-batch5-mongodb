package com.bootlabs.springbatch5mongodb.domain.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Allows you to define the constant parameters.
 *
 * @author aek
 */public class BatchConstants {

    private BatchConstants(){}

    public static final String START_BLOC_STEP_LISTENER_LOGGER_SEPARATOR = "################################################# STEP {} ###################################################";
    public static final String START_BLOC_JOB_LISTENER_LOGGER_SEPARATOR = "-------------------------------------------------- JOB {} ----------------------------------------------------";
    public static final String END_BLOC_STEP_LISTENER_LOGGER_SEPARATOR = "#############################################################################################################################\n";
    public static final String END_BLOC_JOB_LISTENER_LOGGER_SEPARATOR = "-----------------------------------------------------------------------------------------------------------------------------\n";
    public static final String JOB_NAME = "BATCH LABS - Spring Batch Version";

    public static final int DEFAULT_LIMIT_SIZE = 500;
    public static final int DEFAULT_CHUNK_SIZE = 500;
    public static final String CSV_BASE_NAME = "citibike_extract";
}