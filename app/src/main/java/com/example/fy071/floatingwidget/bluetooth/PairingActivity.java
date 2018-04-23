package com.example.fy071.floatingwidget.bluetooth;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fy071.floatingwidget.BaseActivity;
import com.example.fy071.floatingwidget.R;
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
    private static final String TAG = "PairingActivity";

    BluetoothConnectService bluetoothConnectService;

    private BluetoothAdapter bluetoothAdapter = null;

    @BindView(R.id.scan_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView_pair_hint)
    ImageView imageView;

    @BindView(R.id.textView_pairing_hint)
    TextView textView;

    @BindView(R.id.textView_devices)
    TextView devicesTextView;

    @BindView(R.id.recyclerView_device_list)
    RecyclerView recyclerView;

    @BindView(R.id.fab_search)
    FloatingActionButton floatingActionButton;

    private ItemAdapter<DeviceListItem> itemAdapter;

    @OnClick(R.id.fab_search)
    void search() {
        devicesTextView.setVisibility(View.VISIBLE);

        itemAdapter.clear();

        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                DeviceListItem deviceListItem = new DeviceListItem().withBluetoothDevice(device);
                itemAdapter.add(deviceListItem);
            }
        }

        scanDevice(false);
        scanDevice(true);
    }
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

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!isExist(device)) {
                addDevice(device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        ButterKnife.bind(this);

        initToolbar();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果为null则本设备不支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device, leaving activity", Toast.LENGTH_LONG).show();
            finish();
        }

        itemAdapter = new ItemAdapter<>();

        FastAdapter<DeviceListItem> fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter
                .withSelectable(true)
                .withOnClickListener(new OnClickListener<DeviceListItem>() {
                    @Override
                    public boolean onClick(@Nullable View v, IAdapter<DeviceListItem> adapter, DeviceListItem item, int position) {
                        bluetoothConnectService.connectServer(item.bluetoothDevice);
                        Toast.makeText(PairingActivity.this, "Connecting", Toast.LENGTH_SHORT).show();

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
    protected void onStart() {
        super.onStart();
        requestBluetoothEnable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothConnectService = BluetoothConnectService.getInstance();
            bluetoothConnectService.setHandler(handler);
            bluetoothConnectService.startServer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // 如果蓝牙开启则进入位置权限获取
                if (resultCode == Activity.RESULT_OK) {
                    requestLocationPermission();
                } else {
                    Toast.makeText(this, "Bluetooth not enabled, leaving activity", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanDevice(false);//确保不继续搜索
        if (bluetoothConnectService != null) {
            bluetoothConnectService.cancelAll();
        }
        this.unregisterReceiver(receiver);
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

    private void requestBluetoothEnable() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int locationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            // 如果无位置权限
            if (locationPermission == PackageManager.PERMISSION_DENIED) {
                // 如果需要解释为何需要权限
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // 弹出对话框
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_title_permission_location)
                            .setMessage(R.string.dialog_message_permission_location)
                            .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击确认则弹出获取位置权限的对话框
                                    requestPermissions(
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                            REQUEST_ACCESS_COARSE_LOCATION);
                                }
                            })
                            .show();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
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

    private void scanDevice(final boolean enable) {
        if (enable) {
            progressBar.setVisibility(View.VISIBLE);

            bluetoothAdapter.startLeScan(leScanCallback);

            bleSearchHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 停止BLE扫描
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    // 开始传统扫描
                    bluetoothAdapter.startDiscovery();
                }
            }, SCAN_PERIOD);
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private void addDevice(BluetoothDevice device) {
        DeviceListItem deviceListItem = new DeviceListItem().withBluetoothDevice(device);
        itemAdapter.add(deviceListItem);
    }

    private void start() {
        Intent intent = new Intent(this, ConnectedActivity.class);
        startActivity(intent);
    }

    private boolean isExist(BluetoothDevice device) {
        List<DeviceListItem> deviceList = itemAdapter.getAdapterItems();
        for (DeviceListItem deviceItem : deviceList) {
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
                Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                activity.start();
            }
        }
    }
}