package com.example.fy071.floatingwidget.component.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.entity.BluetoothConnectService;
import com.example.fy071.floatingwidget.entity.BluetoothDeviceItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PairingActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;

    private static final int SCAN_PERIOD = 12000;
    private final PairingActivity.MyHandler handler = new PairingActivity.MyHandler(this);
    BluetoothConnectService bluetoothConnectService;

    private static final String TAG = "PairingActivity";
    private BluetoothAdapter bluetoothAdapter = null;
    @BindView(R.id.scan_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textView_devices)
    TextView devicesTextView;
    @BindView(R.id.recyclerview_device_list)
    RecyclerView recyclerView;
    @BindView(R.id.fab_search)
    FloatingActionButton floatingActionButton;
    private ItemAdapter<BluetoothDeviceItem> itemAdapter;
    private Handler bleSearchHandler;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (!isExist(device)) addDevice(device);
                        break;

                    default:
                }
            }
        }
    };

    @OnClick(R.id.fab_search)
    void search() {
        devicesTextView.setVisibility(View.VISIBLE);

        itemAdapter.clear();

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                BluetoothDeviceItem bluetoothDeviceItem = new BluetoothDeviceItem().withBluetoothDevice(device);
                itemAdapter.add(bluetoothDeviceItem);
            }
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            scanLeDevice(false);
        }

        scanLeDevice(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!isExist(device)) {
                addDevice(device);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();//确保不继续搜索
        bluetoothConnectService.cancelAll();
        this.unregisterReceiver(receiver);
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

        FastAdapter<BluetoothDeviceItem> fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true)
                .withOnClickListener(new OnClickListener<BluetoothDeviceItem>() {
                    @Override
                    public boolean onClick(@Nullable View v, IAdapter<BluetoothDeviceItem> adapter, BluetoothDeviceItem item, int position) {
                        Intent intent = new Intent(PairingActivity.this, ConnectedActivity.class);
                        intent.putExtra("device", item.bluetoothDevice);
                        startActivity(intent);
                        return false;
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);

        // 注册广播接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        bleSearchHandler = new Handler();
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
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothConnectService = BluetoothConnectService.getInstance();
            bluetoothConnectService.startServer();
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            int locationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (locationPermission == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_permission_location)
                            .setMessage(R.string.dialog_message_permission_location)
                            .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            REQUEST_ACCESS_COARSE_LOCATION);
                                }
                            })
                            .show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_ACCESS_COARSE_LOCATION);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Permission denied, unable to find BLE devices", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (Build.VERSION.SDK_INT >= 18) {
            if (enable) {
                bleSearchHandler.postDelayed(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        bluetoothAdapter.stopLeScan(leScanCallback);
                        bluetoothAdapter.startDiscovery();
                    }
                }, SCAN_PERIOD);

                bluetoothAdapter.startLeScan(leScanCallback);
            } else {
                bluetoothAdapter.stopLeScan(leScanCallback);
            }
        }
    }

    private void addDevice(BluetoothDevice device) {
        BluetoothDeviceItem bluetoothDeviceItem = new BluetoothDeviceItem().withBluetoothDevice(device);
        itemAdapter.add(bluetoothDeviceItem);
    }

    private void start() {
        Intent intent = new Intent(this, ConnectedActivity.class);
        startActivity(intent);
    }

    private boolean isExist(BluetoothDevice device) {
        List<BluetoothDeviceItem> deviceList = itemAdapter.getAdapterItems();
        for (BluetoothDeviceItem deviceItem : deviceList) {
            //任意item的地址与参数地址相同则返回真
            if (deviceItem.address.toString().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    static class MyHandler extends Handler {
        WeakReference<PairingActivity> mActivity;

        MyHandler(PairingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PairingActivity activity = mActivity.get();
            if (activity != null) {
                activity.start();
            }
        }
    }
}