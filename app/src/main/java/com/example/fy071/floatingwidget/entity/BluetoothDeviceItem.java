package com.example.fy071.floatingwidget.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothDeviceItem extends AbstractItem<BluetoothDeviceItem, BluetoothDeviceItem.ViewHolder> {
    public StringHolder name;
    public StringHolder address;

    public BluetoothDeviceItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public BluetoothDeviceItem withAddress(String Address) {
        this.address = new StringHolder(Address);
        return this;
    }

    @NonNull
    @Override
    public BluetoothDeviceItem.ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.bt_device_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.bt_device_item;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<BluetoothDeviceItem> {
        protected View view;
        @BindView(R.id.bt_device_name)
        TextView name;
        @BindView(R.id.bt_device_address)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        public void bindView(@NonNull BluetoothDeviceItem item, @NonNull List<Object> payloads) {
            Context context = itemView.getContext();
            StringHolder.applyTo(item.name, name);
            StringHolder.applyTo(item.address, description);
        }

        @Override
        public void unbindView(@NonNull BluetoothDeviceItem item) {
            name.setText(null);
            description.setText(null);
        }
    }
}