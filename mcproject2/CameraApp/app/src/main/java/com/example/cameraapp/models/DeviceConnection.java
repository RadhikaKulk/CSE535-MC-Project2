package com.example.cameraapp.models;

import java.io.Serializable;

public class DeviceConnection implements Serializable {

    private String endpointId;
    private String endpointName;
    private DeviceStatistics deviceStats;
    private String requestStatus;
    public DeviceConnection(String endpointId, String endpointName, String requestStatus, DeviceStatistics deviceStats)
    {
        this.endpointId = endpointId;
        this.endpointName = endpointName;
        this.deviceStats = deviceStats;
        this.requestStatus = requestStatus;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public DeviceStatistics getDeviceStats() {
        return deviceStats;
    }

    public void setDeviceStats(DeviceStatistics deviceStats) {
        this.deviceStats = deviceStats;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

}
