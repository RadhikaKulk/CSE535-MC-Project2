package com.example.cameraapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cameraapp.models.DeviceConnection;

import java.util.List;



public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    private Context context;
    private List<DeviceConnection> connectedDevices;

    public ConnectionsAdapter(@NonNull Context context, List<DeviceConnection> connectedDevices) {
        this.context = context;
        this.connectedDevices = connectedDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.device_connected, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setClientId(connectedDevices.get(position).getEndpointId(), connectedDevices.get(position).getEndpointName());
        holder.setBatteryLevel(connectedDevices.get(position).getDeviceStats().getBatteryLevel());
        holder.setRequestStatus(connectedDevices.get(position).getRequestStatus());
    }

    @Override
    public int getItemCount() {
        return connectedDevices.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvClientId;
        private TextView tvBatteryLevel;
        private TextView tvRequestStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientId = itemView.findViewById(R.id.tv_client_id);
            tvBatteryLevel = itemView.findViewById(R.id.tv_battery_level);
            tvRequestStatus = itemView.findViewById(R.id.tv_request_status);
        }

        public void setClientId(String endpointId, String endpointName) {
            this.tvClientId.setText(endpointId + " (" + endpointName + ")");
        }

        public void setBatteryLevel(int batteryLevel) {
            if (batteryLevel > 0 && batteryLevel <= 100) {
                this.tvBatteryLevel.setText(batteryLevel + "%");
            } else {
                this.tvBatteryLevel.setText("--");
            }
        }

        public void setRequestStatus(String requestStatus) {
            if (requestStatus.equals(Request.ACCEPTED)) {
                this.tvRequestStatus.setText("Accepted");
            } else if (requestStatus.equals(Request.REJECTED)) {
                this.tvRequestStatus.setText("Rejected");
            } else {
                this.tvRequestStatus.setText("Pending");
            }
        }
    }
}
