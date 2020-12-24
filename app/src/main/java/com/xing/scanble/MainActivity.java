package com.xing.scanble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements  ScrlViewItemClickListener {

    private static final int REQUEST_CODE_FINE_LOCATION = 100010;
    ArrayList<HashMap<String,Object>> mListItem;
    private TextView                mTextView;
    private RecyclerView            mBlueDevList;
    private RecycleViewAdapter      mRVAdapter;

    private BluetoothManager        mBleManager;
    private BluetoothAdapter        mBleAdapter;
    private BluetoothLeAdvertiser   mBleAdvt;
    private BluetoothLeScanner      mBleScanner;
    private ScanSettings            mScanSettings;
    private ScanCallback            mBleScanCallback;
    private AdvertiseCallback       mAdvtCallback;
    private AdvertiseSettings       mAdvtSettings;
    private AdvertiseData           mAdvtData;

    private boolean                 isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_info);

        initData();
        initView();
        
        initBle();
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            addListItem("mBroadcastReceiver.onReceive", "............");
            mRVAdapter.notifyDataSetChanged();
            if (param1Intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intVal = param1Intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 0x80000000);
                addListItem("mBroadcastReceiver.onReceive", Integer.toString(intVal));
                mRVAdapter.notifyDataSetChanged();
                switch (intVal) {
                    default:
                        //BLEHandler.this.notifyObserversBLESupportChanged();
                        return;
                    case 12:
                        break;
                }
            } else {
                return;
            }
            if(isScanning) {
                addListItem("mBroadcastReceiver.onReceive", "............");
                mRVAdapter.notifyDataSetChanged();
                isScanning = false;
                startScanning();
            }
        }
    };

    private void initBle() {
        if(! getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            addListItem("FEATURE_BLUETOOTH_LE", "NOT SUPPORTED");
            mRVAdapter.notifyDataSetChanged();
            return;
        }
        mBleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if( mBleManager == null ) {
            addListItem("Context.BLUETOOTH_SERVICE", "NOT FOUND");
            mRVAdapter.notifyDataSetChanged();
            return;
        }
        mBleAdapter = mBleManager.getAdapter();
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(mBroadcastReceiver, intentFilter);
        if (Build.VERSION.SDK_INT >= 21) {
            mBleScanner = mBleAdapter.getBluetoothLeScanner();
            ScanSettings.Builder builder = new ScanSettings.Builder();
            builder.setReportDelay(0L);
            builder.setScanMode(2);
            builder.setCallbackType(1);
            if (Build.VERSION.SDK_INT >= 23) {
                builder.setNumOfMatches(3);
            }
            mScanSettings = builder.build();
        }

    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//使用线性布局
        mBlueDevList = (RecyclerView) findViewById(R.id.dev_list);
        mRVAdapter = new RecycleViewAdapter(this, mListItem);
        mRVAdapter.setOnItemClickListener(this);
        // 设置分割线
        mBlueDevList.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        mBlueDevList.setLayoutManager(layoutManager);
        mBlueDevList.setHasFixedSize(true);
        mBlueDevList.setAdapter(mRVAdapter);
    }

    private void addListItem(String title, String text) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", title);
        map.put("ItemText", text);
        map.put("ItemImage", R.mipmap.ic_launcher);
        mListItem.add(map);
    }

    private void initData() {
        mListItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 0; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", "第" + i + "行");
            map.put("ItemText", "这是第" + i + "行");
            map.put("ItemImage", R.mipmap.ic_launcher);
            mListItem.add(map);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mTextView.setText((String) mListItem.get(position).get("ItemText"));
    }

    public boolean bleIsRunning() {
        return true;
//        boolean bool2 = true;
//        boolean bool1 = true;
//        if (this.btstack != null) {
//            if (this.btstackState != BTSTACK_STATE.READY)
//                bool1 = false;
//            return Boolean.valueOf(bool1);
//        }
//        if (Build.VERSION.SDK_INT >= 18) {
//            if (myBluetoothAdapter != null && myBluetoothAdapter.isEnabled()) {
//                bool1 = bool2;
//                return Boolean.valueOf(bool1);
//            }
//            bool1 = false;
//            return Boolean.valueOf(bool1);
//        }
//        return Boolean.valueOf(false);
    }


    public void startScanning() {
        if (!isScanning) {
            isScanning = true;
            if (bleIsRunning()) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (this.mBleScanner != null) {
                        boolean granted = false;
                        if (Build.VERSION.SDK_INT >= 23) {
                            granted = checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED;
                            if(!granted) {
                                requestPermissions(new String[] {"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE_FINE_LOCATION);
                            }
                            else {
                                addListItem("ACCESS_FINE_LOCATION", "granted");
                                mRVAdapter.notifyDataSetChanged();
                            }
                        }
                        if (granted) {
                            this.mBleScanner.startScan(null, mScanSettings, getLeScanCallback());

                            addListItem("mBleScanner", "startScan");
                            mRVAdapter.notifyDataSetChanged();

                            Log.d("BLEHandler", "BLE scan started");

                            Timer nTimer = new Timer();
                            nTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    mBleScanner.stopScan(getLeScanCallback());
                                    Log.d("mBleScanner", "stopScan");
                                    isScanning = false;
                                }
                                },20000);

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_FINE_LOCATION) {
            addListItem(permissions.toString(), grantResults.toString());
            mRVAdapter.notifyDataSetChanged();
        }
    }

    private ScanCallback getLeScanCallback() {
        if (mBleScanCallback == null)
            mBleScanCallback = new ScanCallback() {
                public void onBatchScanResults(List<ScanResult> param1List) {
                    super.onBatchScanResults(param1List);
                    addListItem("getLeScanCallback", "onBatchScanResults");
                    for (ScanResult scanResult : param1List) {
                        addListItem(scanResult.getDevice().toString(), Integer.toString(scanResult.getRssi()));
                        //scanResult.getScanRecord().getBytes()
                    }
                    mRVAdapter.notifyDataSetChanged();
                }
                public void onScanFailed(int param1Int) {
                    super.onScanFailed(param1Int);
                    addListItem("onScan failed:", Integer.toString(param1Int));
                    mRVAdapter.notifyDataSetChanged();
                    Log.e("BLEHandler", "onScan failed: " + param1Int);
                }
                public void onScanResult(int param1Int, ScanResult param1ScanResult) {
                    super.onScanResult(param1Int, param1ScanResult);
                    String info = param1ScanResult.getScanRecord().getDeviceName();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        info += Integer.toString(param1ScanResult.getAdvertisingSid());
                        info += param1ScanResult.getDevice().getUuids().toString();
                    }
                    addListItem(param1ScanResult.getDevice().toString(), info);
                    mRVAdapter.notifyDataSetChanged();
                }
            };
        return this.mBleScanCallback;
    }

    public void onBleStartScan(View view) {
        addListItem("onBleStartScan", "............");
        mRVAdapter.notifyDataSetChanged();
        startScanning();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }
}