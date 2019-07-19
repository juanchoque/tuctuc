package com.codeformas.tuctuc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codeformas.tuctuc.adapter.DeviceListAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.lv_paired)
    ListView mListView;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter mAdapter;
    private Menu menu;

    private boolean enableDisableFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAdapter = new DeviceListAdapter(this);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        enableDisableMenu();
        return true;
    }

    private void enableDisableMenu() {
        MenuItem menuEnable = menu.findItem(R.id.menuEnable);
        MenuItem menuSearch = menu.findItem(R.id.menuSearch);

        if(this.mBluetoothAdapter.isEnabled()){
            menuEnable.setIcon(R.drawable.ic_bluetooth_disabled_black_24dp);
            menuSearch.setVisible(true);
            this.enableDisableFlag = true;
        }
        else{
            menuEnable.setIcon(R.drawable.ic_bluetooth_connected_black_24dp);
            menuSearch.setVisible(false);
            this.enableDisableFlag = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEnable && !this.enableDisableFlag) {
            clearDeviceList();

            if(!this.enableDisableFlag){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1000);
            }
            else{
                clearDeviceList();
                this.mBluetoothAdapter.disable();
            }

        }
        else if (id == R.id.menuSearch){
            this.checkLocationPermission();
        }
        return true;
    }

    private void clearDeviceList() {
        if(this.mDeviceList != null){
            this.mDeviceList.clear();
        }
    }

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery();
                }
                break;
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_ON) {
                showToast("Enabled");
            }

            try {
                enableDisableMenu();
            }catch (Exception ex){
            }
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            showToast("Strarted");
            mDeviceList = new ArrayList<BluetoothDevice>();
            progressBar.setVisibility(View.VISIBLE);
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            showToast("Finished");
            //mProgressDlg.dismiss();
            progressBar.setVisibility(View.GONE);

            setDevicesInList();
        }
        else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mDeviceList.add(device);
            showToast("Found device " + device.getName());
        }
        else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                showToast("Paired");
            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                showToast("Unpaired");
            }

            mAdapter.notifyDataSetChanged();
        }
        }
    };

    private void setDevicesInList() {
        mAdapter.setData(mDeviceList);
        mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {
            @Override
            public void onPairButtonClick(int position) {
                /*BluetoothDevice device = mDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    unpairDevice(device);
                } else {
                    pairDevice(device);
                }*/

                BluetoothDevice device = mDeviceList.get(position);

                ArrayList<BluetoothDevice>selectedList = new ArrayList<>(1);
                selectedList.add(device);

                Intent newIntent = new Intent(MainActivity.this, ControlActivity.class);
                newIntent.putParcelableArrayListExtra("device.list", selectedList);
                startActivity(newIntent);
            }
        });
        mAdapter.setpListener(new DeviceListAdapter.OnPlayButtonClickListener() {
            @Override
            public void onPlayButtonClick(int position) {
                BluetoothDevice device = mDeviceList.get(position);

                ArrayList<BluetoothDevice>selectedList = new ArrayList<>(1);
                selectedList.add(device);

                Intent newIntent = new Intent(MainActivity.this, ControlActivity.class);
                newIntent.putParcelableArrayListExtra("device.list", selectedList);
                startActivity(newIntent);
            }
        });

        mListView.setAdapter(mAdapter);
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void proceedDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter.startDiscovery();
    }

}
