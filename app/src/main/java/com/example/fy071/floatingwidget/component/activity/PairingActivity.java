package com.example.fy071.floatingwidget.component.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.service.BluetoothService;
import com.example.fy071.floatingwidget.entity.BluetoothDeviceItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

public class PairingActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter = null;

    private BluetoothService bluetoothService = null;

    private static final String TAG = "PairingActivity";
    @BindView(R.id.recycler_view_devices)
    RecyclerView recyclerView;
    private ItemAdapter<BluetoothDeviceItem> itemAdapter;
    @BindView(R.id.fab_search)
    FloatingActionButton floatingActionButton;
    //save our FastAdapter
    private FastAdapter<BluetoothDeviceItem> fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        initToolbar();

        ButterKnife.bind(this);

        itemAdapter = items();

        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<BluetoothDeviceItem>() {
            @Override
            public boolean onClick(@Nullable View v, IAdapter<BluetoothDeviceItem> adapter, BluetoothDeviceItem item, int position) {
                Toast.makeText(PairingActivity.this, item.name.getText(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果为null则本设备不支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @OnClick(R.id.fab_search)
    void search() {
        //获取配对过设备
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        //若有配对过设备则更新列表
        if (pairedDevices.size() > 0) {
            List<BluetoothDeviceItem> list = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                list.add(new BluetoothDeviceItem()
                        .withName(device.getName())
                        .withAddress(device.getAddress())
                );
                Log.d(TAG, "search: " + list);
                Log.d(TAG, "search: " + device.getName());
                Log.d(TAG, "search: " + device.getAddress());
            }
            itemAdapter.add(list);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (bluetoothService == null) {
            //setupChat();
        }
    }

/*    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                bluetoothService.start();
            }
        }
    }*/

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
/*            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;*/

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Bluetooth not enabled, leaving activity", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //设置300秒蓝牙可见
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

/*    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothService.connect(device);
    }*/


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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

