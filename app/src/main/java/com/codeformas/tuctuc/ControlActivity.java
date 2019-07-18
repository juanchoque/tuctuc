package com.codeformas.tuctuc;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ControlActivity extends AppCompatActivity {
    @BindView(R.id.imgBtnTop)
    ImageButton btnTop;
    @BindView(R.id.imgBtnBotton)
    ImageButton btnButton;
    @BindView(R.id.imgBtnRigth)
    ImageButton btnRigth;
    @BindView(R.id.imgBtnLeft)
    ImageButton btnLeft;

    private ArrayList<BluetoothDevice> mDeviceList;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private String command = "";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        ButterKnife.bind(this, this);

        this.mDeviceList = getIntent().getExtras().getParcelableArrayList("device.list");

        try {
            this.device = this.mDeviceList.get(0);
        }catch (Exception ex){
        }

        this.btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("1");
            }
        });
        this.btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("2");
            }
        });

        this.BTconnect();
    }

    private boolean BTconnect() {
        boolean connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }catch(IOException e){
            e.printStackTrace();
            connected = false;
        }

        if(connected){
            try{
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        return connected;
    }

    private void sendData(String command) {
        this.command = command;

        try {
            outputStream.write(this.command.getBytes()); //transmits the value of command to the bluetooth module
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
