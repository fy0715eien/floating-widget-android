package com.example.fy071.floatingwidget.component.activity;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;

import com.example.fy071.floatingwidget.component.service.BluetoothService;

public class PairingActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter = null;

    private BluetoothService bluetoothService = null;

    private static final String TAG = "PairingActivity";

    //save our FastAdapter
  /*  private FastAdapter<BluetoothDeviceItem> fastAdapter;
    private ItemAdapter<BluetoothDeviceItem> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        initToolbar();
        ButterKnife.bind(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果为null则本设备不支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @OnClick(R.id.fab_search)
    void search() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            List<BluetoothDeviceItem> list=new List
            for (BluetoothDevice device : pairedDevices) {
                itemAdapter.add(new BluetoothDeviceItem(device.getName(),device.getAddress()));
                Log.d(TAG, "search: " + device.getName() + "\n" + device.getAddress());
            }
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

    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothService != null) {
            if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
                bluetoothService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;

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

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothService.connect(device);
    }


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
    }*/
}

