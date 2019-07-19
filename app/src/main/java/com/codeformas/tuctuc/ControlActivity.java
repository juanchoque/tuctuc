package com.codeformas.tuctuc;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.drawable.Drawable;
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
    ImageButton btnBotton;
    @BindView(R.id.imgBtnRigth)
    ImageButton btnRigth;
    @BindView(R.id.imgBtnLeft)
    ImageButton btnLeft;
    @BindView(R.id.imgBtnStart)
    ImageButton btnBtnStart;

    private ArrayList<BluetoothDevice> mDeviceList;

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private String command = "";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private boolean isPlay = false;

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

        this.btnBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeAllButtons();
            }
        });

        this.btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("1");
            }
        });
        this.btnBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("2");
            }
        });
        this.btnRigth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("3");
            }
        });
        this.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("4");
            }
        });

        this.initData();

        this.BTconnect();
    }

    private void initData(){

        this.btnBtnStart.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        this.btnTop.setEnabled(false);
        this.btnBotton.setEnabled(false);
        this.btnLeft.setEnabled(false);
        this.btnRigth.setEnabled(false);
    }

    private void activeAllButtons() {
        int icon = R.drawable.ic_stop_black_24dp;
        if(this.isPlay){
            //Drawable drawable =  null;
            icon = R.drawable.ic_play_arrow_black_24dp;
            this.isPlay = false;
        }
        else{
            this.isPlay = true;
        }

        this.btnBtnStart.setImageResource(icon);

        this.btnTop.setEnabled(this.isPlay);
        this.btnBotton.setEnabled(this.isPlay);
        this.btnLeft.setEnabled(this.isPlay);
        this.btnRigth.setEnabled(this.isPlay);
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
