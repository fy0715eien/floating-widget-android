/*
package com.example.fy071.floatingwidget.entity;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;

import java.util.List;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>{
    private List<BluetoothDeviceItem> deviceItemList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bt_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDeviceItem item=deviceItemList.get(position);
        holder.name.setText(item.name);
        holder.address.setText(item.address);
    }

    @Override
    public int getItemCount() {
        return deviceItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView address;
        public ViewHolder(View view){
            super(view);
            name=view.findViewById(R.id.bt_item_name);
            address=view.findViewById(R.id.bt_item_address);
        }
    }

    public BluetoothDeviceAdapter(List<BluetoothDeviceItem> list){
        deviceItemList=list;
    }


}
*/
