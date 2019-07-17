package com.codeformas.tuctuc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

import com.codeformas.tuctuc.adapter.BluetoothDevicesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDivicesActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevicesAdapter bluetoothDevicesAdapter;

    private RecyclerView recycler = null;
    private RecyclerView.LayoutManager lManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_divices);

        this.recycler = this.findViewById(R.id.recycler_content);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.getBluetoothDevices();
    }

    private void getBluetoothDevices() {
        Set<BluetoothDevice> bondedDevices = this.bluetoothAdapter.getBondedDevices();
        Log.v(">>>>>>>>>>>>>>>>>", bondedDevices.size() + "");
        this.showBluetoothDevices(bondedDevices);
    }

    private void showBluetoothDevices(Set<BluetoothDevice> bondedDevices){
        List<BluetoothDevice> deviceList = new ArrayList<>(bondedDevices);
        //deviceList.add(new BluetoothDevice());
        this.bluetoothDevicesAdapter = new BluetoothDevicesAdapter(this.getApplicationContext(), deviceList);

        this.recycler.setHasFixedSize(true);
        this.lManager = new LinearLayoutManager(this.getApplicationContext());

        this.recycler.setAdapter(this.bluetoothDevicesAdapter);
        this.recycler.setLayoutManager(this.lManager);
        this.recycler.setItemAnimator(new DefaultItemAnimator());
    }
}
