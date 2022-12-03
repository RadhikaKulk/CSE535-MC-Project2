package com.example.cameraapp.models;

import android.content.Context;

import com.example.cameraapp.TransformPayloadData;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;


import java.io.IOException;

public class Comm {
    public static void sendToDevice(Context context, String endpointId, InfoPayload tPayload) {
        try {
            Payload payload = TransformPayloadData.toPayload(tPayload);
            Comm.sendToDevice(context, endpointId, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToDevice(Context context, String endpointId, byte[] data) {
        Payload payload = Payload.fromBytes(data);
        Comm.sendToDevice(context, endpointId, payload);
    }

    public static void sendToDevice(Context context, String endpointId, Payload payload) {
        Nearby.getConnectionsClient(context).sendPayload(endpointId, payload);
    }
}
