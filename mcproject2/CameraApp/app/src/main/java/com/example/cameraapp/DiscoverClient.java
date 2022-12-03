package com.example.cameraapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;

public class DiscoverClient {

    private Context context;
    private DiscoveryOptions discoveryOptions;

    public DiscoverClient(Context context) {
        this.context = context;

        this.discoveryOptions =
                new DiscoveryOptions.Builder()
                        .setStrategy(Strategy.P2P_CLUSTER)
                        .build();
    }

    public void start(EndpointDiscoveryCallback endpointDiscoveryCallback) {
        Nearby.getConnectionsClient(context)
                .startDiscovery(context.getPackageName(), endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener((unused) -> {
                    Log.d("Debug", "STILL DISCOVERING");
                    Log.d("Debug", unused + "");
                })
                .addOnFailureListener((Exception e) -> {
                    Log.d("Debug", "DISCOVERING FAILED");
                    e.printStackTrace();
                });
    }

    public void stop() {
        Nearby.getConnectionsClient(context).stopDiscovery();
    }
}
