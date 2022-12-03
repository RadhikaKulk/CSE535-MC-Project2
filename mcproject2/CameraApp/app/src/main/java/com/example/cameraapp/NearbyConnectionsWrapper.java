package com.example.cameraapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.util.HashSet;

public class NearbyConnectionsWrapper {

    private static NearbyConnectionsWrapper nearbyConnectionsWrapper;
    private Context context;

    private HashSet<ClientListener> clientConnectionListenerSet = new HashSet<>();
    private HashSet<InfoPayloadListener> payloadListenersSet = new HashSet<>();

    private ConnectionLifecycleCallback connectionLifecycleCallback;

    private HashSet<String> disconnectedEndpoints = new HashSet<>();

    public NearbyConnectionsWrapper(Context context) {
        this.context = context;
        this.connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                for (ClientListener clientConnectionListener : clientConnectionListenerSet) {
                    try {
                        clientConnectionListener.onConnectionInitiated(endpointId, connectionInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
                for (ClientListener clientConnectionListener : clientConnectionListenerSet) {
                    try {
                        clientConnectionListener.onConnectionResult(endpointId, connectionResolution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                Toast.makeText(context, "DISCONNECTED", Toast.LENGTH_SHORT).show();
//                NearbyConnectionsWrapper.getInstance(context).rejectConnection(endpointId);
                for (ClientListener clientConnectionListener : clientConnectionListenerSet) {
                    try {
                        clientConnectionListener.onDisconnected(endpointId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public static NearbyConnectionsWrapper getInstance(Context context) {
        if (nearbyConnectionsWrapper == null) {
            nearbyConnectionsWrapper = new NearbyConnectionsWrapper(context);
        }

        return nearbyConnectionsWrapper;
    }

    public boolean registerPayloadListener(InfoPayloadListener payloadListener) {
        if (payloadListener != null) {
            return payloadListenersSet.add(payloadListener);
        }
        return false;
    }

    public boolean registerClientConnectionListener(ClientListener clientConnectionListener) {
        if (clientConnectionListener != null) {
            return clientConnectionListenerSet.add(clientConnectionListener);
        }
        return false;
    }

    public boolean unregisterPayloadListener(InfoPayloadListener payloadListener) {
        if (payloadListener != null) {
            return payloadListenersSet.remove(payloadListener);
        }
        return false;
    }


    public boolean unregisterClientConnectionListener(ClientListener clientConnectionListener) {
        if (clientConnectionListener != null) {
            return clientConnectionListenerSet.remove(clientConnectionListener);
        }
        return false;
    }

    public void acceptConnection(String endpointId) {
        Nearby.getConnectionsClient(context).acceptConnection(endpointId, new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                for (InfoPayloadListener payloadListener : payloadListenersSet) {
                    try {
                        payloadListener.onPayloadReceived(endpointId, payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

            }
        });
    }

    public void rejectConnection(String endpointId) {

        Log.d("Debug", "LOOPING");
//        TPayload tPayload = new TPayload();
//        tPayload.setTag(Constants.PayloadTags.DISCONNECTED);
//
//        Communicator.sendToDevice(context, endpointId, tPayload);

        Nearby.getConnectionsClient(context)
                .rejectConnection(endpointId);
    }

    public void advertise(String clientId, AdvertisingOptions advertisingOptions) {
        Nearby.getConnectionsClient(context)
                .startAdvertising(clientId, context.getPackageName(), connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener((unused) -> {
                    Log.d("Debug", "STILL ADVERTISING");
                    Log.d("Debug", unused + "");
                })
                .addOnFailureListener((Exception e) -> {
                    Log.d("Debug", "ADVERTISING FAILED");
                    e.printStackTrace();
                });
    }

    public void requestConnection(String endpointId, String clientId) {
        Nearby.getConnectionsClient(context)
                .requestConnection(clientId, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener(unused -> {
                    Log.d("Debug", "CONNECTION REQUESTED");
                })
                .addOnFailureListener((Exception e) -> {
                    Log.d("Debug", "CONNECTION FAILED");
                    e.printStackTrace();
                });
    }
}
