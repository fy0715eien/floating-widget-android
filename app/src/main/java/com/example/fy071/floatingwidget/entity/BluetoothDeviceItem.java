package com.example.fy071.floatingwidget.entity;

import android.bluetooth.BluetoothClass;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fy071.floatingwidget.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothDeviceItem extends AbstractItem<BluetoothDeviceItem, BluetoothDeviceItem.ViewHolder> {
    public BluetoothClass bluetoothClass;
    public StringHolder name;
    public StringHolder address;

    public BluetoothDeviceItem withBluetoothClass(BluetoothClass mBluetoothClass) {
        this.bluetoothClass = mBluetoothClass;
        return this;
    }

    public BluetoothDeviceItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public BluetoothDeviceItem withAddress(String Address){
        this.address=new StringHolder(Address);
        return this;
    }

    @NonNull
    @Override
    public BluetoothDeviceItem.ViewHolder getViewHolder(@NonNull View v) {
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

        @BindView(R.id.bt_item_class)
        ImageView bluetoothClass;

        @BindView(R.id.bt_item_name)
        TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        public void bindView(@NonNull BluetoothDeviceItem item, @NonNull List<Object> payloads) {
            switch (item.bluetoothClass.getDeviceClass()) {
                case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER:
                case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
                case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:
                case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
                case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
                case BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER:
                case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:
                case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:
                case BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX:
                case BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED:
                case BluetoothClass.Device.AUDIO_VIDEO_VCR:
                case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA:
                case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING:
                case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER:
                case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY:
                case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR:
                case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                    bluetoothClass.setImageResource(R.drawable.ic_headset_black_24dp);
                    break;
                case BluetoothClass.Device.COMPUTER_DESKTOP:
                case BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA:
                case BluetoothClass.Device.COMPUTER_LAPTOP:
                case BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA:
                case BluetoothClass.Device.COMPUTER_SERVER:
                case BluetoothClass.Device.COMPUTER_UNCATEGORIZED:
                case BluetoothClass.Device.COMPUTER_WEARABLE:
                    bluetoothClass.setImageResource(R.drawable.ic_computer_black_24dp);
                    break;
                case BluetoothClass.Device.HEALTH_BLOOD_PRESSURE:
                case BluetoothClass.Device.HEALTH_DATA_DISPLAY:
                case BluetoothClass.Device.HEALTH_GLUCOSE:
                case BluetoothClass.Device.HEALTH_PULSE_OXIMETER:
                case BluetoothClass.Device.HEALTH_PULSE_RATE:
                case BluetoothClass.Device.HEALTH_THERMOMETER:
                case BluetoothClass.Device.HEALTH_UNCATEGORIZED:
                case BluetoothClass.Device.HEALTH_WEIGHING:
                    bluetoothClass.setImageResource(R.drawable.ic_favorite_black_24dp);
                    break;
                case BluetoothClass.Device.PHONE_CELLULAR:
                case BluetoothClass.Device.PHONE_CORDLESS:
                case BluetoothClass.Device.PHONE_ISDN:
                case BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY:
                case BluetoothClass.Device.PHONE_SMART:
                case BluetoothClass.Device.PHONE_UNCATEGORIZED:
                    bluetoothClass.setImageResource(R.drawable.ic_smartphone_black_24dp);
                    break;
                case BluetoothClass.Device.TOY_CONTROLLER:
                case BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE:
                case BluetoothClass.Device.TOY_GAME:
                case BluetoothClass.Device.TOY_ROBOT:
                case BluetoothClass.Device.TOY_UNCATEGORIZED:
                case BluetoothClass.Device.TOY_VEHICLE:
                    bluetoothClass.setImageResource(R.drawable.ic_extension_black_24dp);
                    break;
                case BluetoothClass.Device.WEARABLE_GLASSES:
                case BluetoothClass.Device.WEARABLE_HELMET:
                case BluetoothClass.Device.WEARABLE_JACKET:
                case BluetoothClass.Device.WEARABLE_PAGER:
                case BluetoothClass.Device.WEARABLE_UNCATEGORIZED:
                case BluetoothClass.Device.WEARABLE_WRIST_WATCH:
                    bluetoothClass.setImageResource(R.drawable.ic_watch_black_24dp);
                    break;
                    default:
                        bluetoothClass.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            }
            StringHolder.applyTo(item.name, name);
        }

        @Override
        public void unbindView(@NonNull BluetoothDeviceItem item) {
            bluetoothClass.setImageDrawable(null);
            name.setText(null);
        }
    }
}