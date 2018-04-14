package com.example.fy071.floatingwidget.entity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.fy071.floatingwidget.util.Key;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothConnectService {
    private static final String TAG = "BluetoothConnectService";
    private static final UUID MY_UUID = UUID.fromString("517e4089-5a50-455b-be79-125c83a5c84e");// Unique UUID for this application
    private static final BluetoothConnectService ourInstance = new BluetoothConnectService();
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private AcceptThread acceptThread;//服务器线程对象
    private ConnectThread connectThread;//客户端线程对象
    private ConnectedThread connectedThread;//连接进程对象

    private BluetoothConnectService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothConnectService getInstance() {
        return ourInstance;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /*
        开启服务器
        从外部调用
         */
    public synchronized void startServer() {
        cancelConnect();
        cancelConnected();
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
            Log.d(TAG, "startServer: started");
        }
    }

    /*
    连接服务器
    从外部调用
     */
    public synchronized void connectServer(BluetoothDevice targetDevice) {
        cancelConnect();
        cancelConnected();
        connectThread = new ConnectThread(targetDevice);
        connectThread.start();
    }

    //连接后处理
    private synchronized void manageConnectedSocket(BluetoothSocket socket) {
        cancelAccept();
        cancelConnect();
        cancelConnected();

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    //关闭服务器
    private synchronized void cancelAccept() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    //关闭客户端
    private synchronized void cancelConnect() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
    }

    //关闭连接
    private synchronized void cancelConnected() {
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    /*
    关闭全部
    从外部调用
     */
    public synchronized void cancelAll() {
        cancelAccept();
        cancelConnect();
        cancelConnected();
    }

    /*
    发送数据，两组int
    需要发送数据直接调用这个方法
    */
    public synchronized void sendData(int x, int y) {
        //检查连接
        if (connectedThread == null) {
            return;
        }
        //int转byte[]
        byte[] data = new byte[8];

        data[0] = (byte) ((x >>> 24) & 0xff);
        data[1] = (byte) ((x >>> 16) & 0xff);
        data[2] = (byte) ((x >>> 8) & 0xff);
        data[3] = (byte) ((x) & 0xff);

        data[4] = (byte) ((y >>> 24) & 0xff);
        data[5] = (byte) ((y >>> 16) & 0xff);
        data[6] = (byte) ((y >>> 8) & 0xff);
        data[7] = (byte) ((y) & 0xff);

        connectedThread.write(data);
    }

    //接收数据
    private synchronized void receiveData(int length, byte[] data) {
        //检查传入字节数
        if (length < 8) {
            //TODO SET_TOO_SHORT
        }
        //byte[]转int
        int[] pos = new int[2];

        int a = (data[0] & 0xff) << 24;
        int b = (data[1] & 0xff) << 16;
        int c = (data[2] & 0xff) << 8;
        int d = (data[3] & 0xff);
        pos[0] = a | b | c | d;

        a = (data[4] & 0xff) << 24;
        b = (data[5] & 0xff) << 16;
        c = (data[6] & 0xff) << 8;
        d = (data[7] & 0xff);
        pos[1] = a | b | c | d;

        //用构造方法传入的handler传回message
        Message msg = Message.obtain();
        msg.arg1 = pos[0];
        msg.arg2 = pos[1];
        handler.sendMessage(msg);
    }

    //服务器接收线程
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("FloatingWidget", MY_UUID);
            } catch (IOException e) {
                //TODO IOE
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    //TODO IOE
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        //TODO IOE
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                //TODO IOE
            }
        }
    }

    //客户端连接线程
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        //private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            //mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //TODO IOE
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    //TODO IOE
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //TODO IOE
            }
        }
    }

    //连接后进程
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.d(TAG, "ConnectedThread: connected");

            handler.sendEmptyMessage(Key.MESSAGE_WHAT_PAIRED);

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //TODO IOE
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    receiveData(bytes, buffer);
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                //TODO IOE
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //TODO IOE
            }
        }
    }
}
