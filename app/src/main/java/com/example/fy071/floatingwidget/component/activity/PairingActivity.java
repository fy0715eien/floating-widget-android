package com.example.fy071.floatingwidget.component.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.BluetoothDeviceItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PairingActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter = null;

    private ItemAdapter<BluetoothDeviceItem> itemAdapter;

    private FastAdapter<BluetoothDeviceItem> fastAdapter;

    private static final String TAG = "PairingActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textView_devices)
    TextView devicesTextView;

    @BindView(R.id.recyclerview_device_list)
    RecyclerView recyclerView;

    @BindView(R.id.fab_search)
    FloatingActionButton floatingActionButton;

    @OnClick(R.id.fab_search)
    void search() {
        devicesTextView.setVisibility(View.VISIBLE);

        itemAdapter.clear();

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if(devices.size()>0){
            for(BluetoothDevice device:devices){
                itemAdapter.add(
                        new BluetoothDeviceItem().withName(device.getName())
                        .withAddress(device.getAddress())
                        .withBluetoothClass(device.getBluetoothClass())
                );
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        ButterKnife.bind(this);

        initToolbar();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果为null则本设备不支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();
            finish();
        }

        itemAdapter = new ItemAdapter<>();

        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true)
                .withOnClickListener(new OnClickListener<BluetoothDeviceItem>() {
                    @Override
                    public boolean onClick(@Nullable View v, IAdapter<BluetoothDeviceItem> adapter, BluetoothDeviceItem item, int position) {
                        return false;
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                } else {
                    Toast.makeText(this, "Bluetooth not enabled, leaving activity", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.drawer_item_pairing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}