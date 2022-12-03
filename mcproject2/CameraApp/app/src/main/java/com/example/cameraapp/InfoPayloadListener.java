package com.example.cameraapp;

import com.google.android.gms.nearby.connection.Payload;

public interface InfoPayloadListener {
    void onPayloadReceived(String endpointId, Payload payload);
}
