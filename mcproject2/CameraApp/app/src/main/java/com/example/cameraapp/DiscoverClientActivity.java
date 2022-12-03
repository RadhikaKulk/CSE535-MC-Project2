package com.example.cameraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.cameraapp.models.Comm;
import com.example.cameraapp.models.DeviceConnection;
import com.example.cameraapp.models.DeviceStatistics;
import com.example.cameraapp.models.InfoPayload;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.example.cameraapp.ConnectionsAdapter;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;


public class DiscoverClientActivity extends AppCompatActivity {
    private List<DeviceConnection> connectedClients = new ArrayList<>();
    private ConnectionsAdapter connectedClientsAdapter;
    private Button discoveryButton;
    private RecyclerView clients;
    private DiscoverClient discoverClient;
    private InfoPayloadListener infoPayloadListener;
    private ClientListener clientListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_client);
        setupViewsAndAdapters();
        RegisterEventListeners();
        startClientDiscovery();
    }

    private void setupViewsAndAdapters()
    {
        connectedClientsAdapter = new ConnectionsAdapter(this, connectedClients);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        discoveryButton = findViewById(R.id.b_done_discovering);
        clients = findViewById(R.id.rv_connected_devices);
        clients.setLayoutManager(LinearLayoutManager);

        clients.setAdapter(connectedClientsAdapter);
        connectedClientsAdapter.notifyDataSetChanged();
    }

    private ArrayList<DeviceConnection> getAvailableDevices() {
        ArrayList<DeviceConnection> res = new ArrayList<>();

        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getRequestStatus().equals(Request.ACCEPTED)) {
                if (connectedClients.get(i).getDeviceStats().getBatteryLevel() > 20) {
                    res.add(connectedClients.get(i));
                } else {
//                    NearbyConnectionsManager.getInstance(getApplicationContext()).rejectConnection(connectedClients.get(i).getEndpointId());

                    InfoPayload infoPayload = new InfoPayload();
                    infoPayload.setTag(UtilConstants.PayloadTags.DISCONNECTED);

                    Comm.sendToDevice(getApplicationContext(), connectedClients.get(i).getEndpointId(), infoPayload);
                }
            } else {
//                NearbyConnectionsManager.getInstance(getApplicationContext()).rejectConnection(connectedClients.get(i).getEndpointId());

                // Log.d("Debug", "LOOPING");
                InfoPayload infoPayload = new InfoPayload();
                infoPayload.setTag(UtilConstants.PayloadTags.DISCONNECTED);

                Comm.sendToDevice(getApplicationContext(), connectedClients.get(i).getEndpointId(), infoPayload);
            }

        }
        return res;
    }

    private void RegisterEventListeners() {
        discoveryButton.setOnClickListener(view -> {
            ArrayList<DeviceConnection> readyDevices = getAvailableDevices();
            if (readyDevices.size() == 0) {
                Toast.makeText(this, "No workers found", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                discoverClient.stop();
                System.out.println("Sending payload");
               // startMasterActivity(readyDevices);
                finish();
            }
        });

        infoPayloadListener = new InfoPayloadListener() {
            @Override
            public void onPayloadReceived(String endpointId, Payload payload) {

                try {
                    InfoPayload infoPayload = TransformPayloadData.fromPayload(payload);
                    if (infoPayload.getTag().equals(UtilConstants.PayloadTags.DEVICE_STATS)) {
                        updateDeviceStats(endpointId, (DeviceStatistics) infoPayload.getData());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        clientListener = new ClientListener() {
            @Override
            public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                NearbyConnectionsWrapper.getInstance(getApplicationContext()).acceptConnection(endpointId);
            }

            @Override
            public void onConnectionResult(String endpointId, ConnectionResolution connectionResolution) {
                int statusCode = connectionResolution.getStatus().getStatusCode();

                if (statusCode == ConnectionsStatusCodes.STATUS_OK) {
                    updateDeviceConnectionRequestStatus(endpointId, Request.ACCEPTED);
                } else if (statusCode == ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED) {
                    updateDeviceConnectionRequestStatus(endpointId, Request.REJECTED);
                } else if (statusCode == ConnectionsStatusCodes.STATUS_ERROR) {
                    removeDeviceConnection(endpointId);
                }
            }

            @Override
            public void onDisconnected(String endpointId) {
                removeDeviceConnection(endpointId);
            }
        };
    }

    private void startClientDiscovery() {
        EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                // Log.d("Debug", "ENDPOINT FOUND");
                // Log.d("Debug", endpointId);
                // Log.d("Debug", discoveredEndpointInfo.getServiceId() + " " + discoveredEndpointInfo.getEndpointName());

                DeviceConnection connectedDevice = new DeviceConnection(endpointId,discoveredEndpointInfo.getEndpointName(),Request.PENDING,new DeviceStatistics());
//                connectedDevice.setEndpointId(endpointId);
//                connectedDevice.setEndpointName(discoveredEndpointInfo.getEndpointName());
//                connectedDevice.setRequestStatus(Request.PENDING);
//                connectedDevice.setDeviceStats(new DeviceStats());

                connectedClients.add(connectedDevice);
                connectedClientsAdapter.notifyItemChanged(connectedClients.size() - 1);

                NearbyConnectionsWrapper.getInstance(getApplicationContext()).requestConnection(endpointId, "MASTER");
            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {
                // Log.d("Debug", "ENDPOINT LOST");
                // Log.d("Debug", endpointId);

                removeDeviceConnection(endpointId);
            }
        };

        discoverClient = new DiscoverClient(this);
        discoverClient.start(endpointDiscoveryCallback);
    }

    private void updateDeviceConnectionRequestStatus(String endpointId, String status) {
        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getEndpointId().equals(endpointId)) {
                connectedClients.get(i).setRequestStatus(status);
                connectedClientsAdapter.notifyItemChanged(i);
            }
        }
    }

    private void removeDeviceConnection(String endpointId) {
        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getEndpointId().equals(endpointId)) {
                connectedClients.remove(i);
                connectedClientsAdapter.notifyItemChanged(i);
                i--;
            }
        }
    }
    private void updateDeviceStats(String endpointId, DeviceStatistics deviceStats) {
        for (int i = 0; i < connectedClients.size(); i++) {
            if (connectedClients.get(i).getEndpointId().equals(endpointId)) {
                connectedClients.get(i).setDeviceStats(deviceStats);
                connectedClients.get(i).setRequestStatus(Request.ACCEPTED);
                connectedClientsAdapter.notifyItemChanged(i);
            }
        }
    }

//    private void startMasterActivity(ArrayList<DeviceConnection> connectedClients) {
//        Intent intent = new Intent(getApplicationContext(), MasterActivity.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(UtilConstants.CONNECTED_CLIENTS, connectedClients);
//        intent.putExtras(bundle);
//
//        startActivity(intent);
//    }

}
