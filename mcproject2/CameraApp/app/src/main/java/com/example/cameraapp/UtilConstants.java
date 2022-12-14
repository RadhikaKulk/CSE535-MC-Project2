package com.example.cameraapp;

public class UtilConstants {
    public static final String MASTER_TAG = "MASTER";
    public static final String WORKER_TAG = "WORKER";

    public static final String CONNECTED_CLIENTS = "CONNECTED_CLIENTS";
    public static final String MASTER_ENDPOINT_ID = "MASTER_ENDPOINT_ID";

    public static final class PayloadTags {
        public static final String DEVICE_STATS = "DEVICE_STATS";
        public static final String WORK_STATUS = "WORK_STATUS";
        public static final String WORK_DATA = "WORK_DATA";
        public static final String DISCONNECTED = "DISCONNECTED";
        public static final String FAREWELL = "FAREWELL";
    }

    public static final class WorkStatus {
        public static final String WORKING = "WORKING";
        public static final String FINISHED = "WORK_FINISHED";
        public static final String FAILED = "WORK_FAILED";
        public static final String DISCONNECTED = "WORKER_DISCONNECTED";
    }
}
