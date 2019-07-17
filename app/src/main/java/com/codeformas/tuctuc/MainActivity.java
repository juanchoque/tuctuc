package com.codeformas.tuctuc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView mStatusTv;
    private Button mActivateBtn;
    private Button mPairedBtn;
    private Button mScanBtn;
    private Button mSendBtn;

    private ProgressDialog mProgressDlg;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;

    private final String DEVICE_ADDRESS = "20:15:11:23:93:85"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private String command = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusTv 			= (TextView) findViewById(R.id.tv_status);
        mActivateBtn 		= (Button) findViewById(R.id.btn_enable);
        mPairedBtn 			= (Button) findViewById(R.id.btn_view_paired);
        mScanBtn 			= (Button) findViewById(R.id.btn_scan);
        mSendBtn = findViewById(R.id.btn_send);

        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        mProgressDlg 		= new ProgressDialog(this);

        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mBluetoothAdapter.cancelDiscovery();
            }
        });

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            mPairedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices == null || pairedDevices.size() == 0) {
                        showToast("No Paired Devices Found");
                    } else {
                        ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

                        list.addAll(pairedDevices);

                        findPaired(list);

                        Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);

                        intent.putParcelableArrayListExtra("device.list", list);

                        startActivity(intent);
                    }
                }
            });

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //mBluetoothAdapter.startDiscovery();

                    checkLocationPermission();
                }
            });

            mActivateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();

                        showDisabled();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        startActivityForResult(intent, 1000);
                    }
                }
            });

            if (mBluetoothAdapter.isEnabled()) {
                showEnabled();
            } else {
                showDisabled();
            }
        }

        this.mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

        // this.BTconnect();
    }

    private void findPaired(List<BluetoothDevice> data){
        if(data != null){
            for(BluetoothDevice iterator : data){
                //if(iterator.getAddress().equals(DEVICE_ADDRESS)){
                    device = iterator;
                    break;
                //}
            }
        }

        this.BTconnect();

        Log.v("////////////>>>>>>>>>",">>>>>>>>>>" + device);

    }

    private void sendData() {
        command = "1";

        try
        {
            outputStream.write(command.getBytes()); //transmits the value of command to the bluetooth module
            Log.v("==========>>>>>>>>>>", "<<<<<<<<<<<<<Send Data>>>>>>>>>>>>");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean BTconnect()
    {
        boolean connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private void showEnabled() {
        mStatusTv.setText("Bluetooth is On");
        mStatusTv.setTextColor(Color.BLUE);

        mActivateBtn.setText("Disable");
        mActivateBtn.setEnabled(true);

        mPairedBtn.setEnabled(true);
        mScanBtn.setEnabled(true);
    }

    private void showDisabled() {
        mStatusTv.setText("Bluetooth is Off");
        mStatusTv.setTextColor(Color.RED);

        mActivateBtn.setText("Enable");
        mActivateBtn.setEnabled(true);

        mPairedBtn.setEnabled(false);
        mScanBtn.setEnabled(false);
    }

    private void showUnsupported() {
        mStatusTv.setText("Bluetooth is unsupported by this device");

        mActivateBtn.setText("Enable");
        mActivateBtn.setEnabled(false);

        mPairedBtn.setEnabled(false);
        mScanBtn.setEnabled(false);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");

                    showEnabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<BluetoothDevice>();

                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();

                /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
                if(pairedDevices != null){
                    list.addAll(pairedDevices);
                }*/

                Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);

                newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
                //Log.v("$$$$$$$$$>>",">>>>>>>" + list);
                //newIntent.putParcelableArrayListExtra("device.list", list);

                startActivity(newIntent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device);

                showToast("Found device " + device.getName());
            }
        }
    };

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    12);
        }
        else{
            proceedDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
        }
    }

    protected void proceedDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

}
