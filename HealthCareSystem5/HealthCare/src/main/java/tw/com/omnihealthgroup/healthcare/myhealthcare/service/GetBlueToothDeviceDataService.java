package tw.com.omnihealthgroup.healthcare.myhealthcare.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taidoc.pclinklibrary.android.bluetooth.util.BluetoothUtil;
import com.taidoc.pclinklibrary.connection.AndroidBluetoothConnection;
import com.taidoc.pclinklibrary.connection.util.ConnectionManager;
import com.taidoc.pclinklibrary.constant.PCLinkLibraryConstant;
import com.taidoc.pclinklibrary.constant.PCLinkLibraryEnum.User;
import com.taidoc.pclinklibrary.exception.commu.CommunicationTimeoutException;
import com.taidoc.pclinklibrary.meter.AbstractMeter;
import com.taidoc.pclinklibrary.meter.record.AbstractRecord;
import com.taidoc.pclinklibrary.meter.record.BloodGlucoseRecord;
import com.taidoc.pclinklibrary.meter.record.BloodPressureRecord;
import com.taidoc.pclinklibrary.meter.record.TemperatureRecord;
import com.taidoc.pclinklibrary.meter.util.MeterManager;
import com.taidoc.pclinklibrary.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth.BTAsVoice2GSConnService;
import tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth.BTAsVoice2GlucoSure;
import tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth.BTWatchBPhome;
import tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth.BTWatchBPhomeConnService;
import tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth.BluetoothChatService;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.DeviceMapping;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.DeviceMappingAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;

public class GetBlueToothDeviceDataService extends Service {
    public static String TAG = "GetBlueToothDeviceDataService";
    SharedPreferences prf;
    private final static int REQUEST_ENABLE_BT = 1;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdfForDataId = new SimpleDateFormat("yyyyMMddHHmmss");

    public AcceptThread mainThread;

    private static ConnectivityManager connMgr;

    public boolean isStart = false;

    public int deviceSwitch = 0;

    // Member fields
    private BluetoothAdapter mBtAdapter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "GetBlueToothDeviceDataService onCreate");
        //Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // james
        //        if( mBtAdapter == null )
        //        {
        //        		Toast.makeText(this, "您的裝置沒有藍芽設備，無法使用藍芽功能。");
        //        }
        //
        //        if( !mBtAdapter.isEnabled())
        //        {
        // 強制啟動藍芽
        //mBtAdapter.enable();
        // 詢問使用者是否啟用藍芽
        //        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //        		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        //        		enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        		startActivity(enableIntent);
        //        }

        // 取得已配對的設備device
        //        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        //        if( pairedDevices.size() > 0 )
        //        {
        //	        	for( BluetoothDevice device : pairedDevices )
        //	        	{
        //	        		Log.i("info", "pared devices: " + device.getName() );
        //	        	}
        //        }

        mSynchMsgQueue = new LinkedBlockingQueue<byte[]>(32);
        //		if(mBTAV2ConnService==null){
        //			initBTAV2ConnService();
        //		}
        //		if(mBTWBPHConnService==null){
        //			initBTWBPHConnService();
        //		}

        mainThread = null;
        mainThread = new AcceptThread();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.i(TAG, "onStart");
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();

        isStart = true;
        //新建排程
        if (mBtAdapter != null) {
            mainThread.setCmd(0);
            mainThread.start();
        } else {
            mainThread.setCmd(1);
            mainThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        //Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        //		if(mBTAV2ConnService!=null){
        //			mBTAV2ConnService.stop();
        //		}
        //		if(mBTWBPHConnService!=null){
        //			mBTWBPHConnService.stop();
        //		}
        isStart = false;
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mReceiver.onReceive()");
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    Log.i("info", "findeDevice: " + device.getName() + "\n" + "device address: " + device.getAddress());

                }
            }
        }
    };

    private AbstractMeter meter = null;
    private String mTaidocMacAddress;

    public void getTaidocDeviceDataV110(String deviceMac) {
        handler.sendEmptyMessage(65);
        Log.i(TAG, "getTaidocDeviceDataV110 : " + deviceMac);
        try {
            //            /*
            //            // Local Bluetooth adapter
            //	    	BluetoothAdapter localBluetoothAdapter = null;
            //	    	*/
            //            // Member object for the android bluetooth connection
            //            AndroidBluetoothConnection connection = null;
            //            Log.i(TAG, "AndroidBluetoothConnection connection = null");
            //	    	/*
            //	    	// Create an android bluetooth connection and start to listen mode(type II)
            //	    	// Get local Bluetooth adapter
            //	    	localBluetoothAdapter = BluetoothUtil.getBluetoothAdapter();
            //	    	// If BT is not on, let it be enabled.
            //	    	if (!localBluetoothAdapter.isEnabled()) {
            //	    		BluetoothUtil.turnOnBlutoothModule();
            //	    	}
            //	    	*/
            //	    	/* end of if */
            //            // Initialize the connection to perform android bluetooth connections
            //            Log.i(TAG, "AndroidBluetoothConnection connection = null");
            //            connection = ConnectionManager.createAndroidBluetoothConnection();
            //            /* end of if */
            ////	    	// When android Bluetooth connection was be create, start to connect to a meter
            ////	    	if (connection.getState() != AndroidBluetoothConnection.STATE_CONNECTED) {
            ////	    		Log.i(TAG, "connection.getState() != AndroidBluetoothConnection.STATE_CONNECTED");
            ////	    		BluetoothDevice device = BluetoothUtil.getPairedDevice(deviceMac);
            ////	    		// Attempt to connect to the device, Type I
            ////	    		connection.connect(device);
            ////	    		// connect time out default is 10s
            ////	    		long startConnectTime = System.currentTimeMillis();
            ////	    		while (connection.getState() != AndroidBluetoothConnection.STATE_CONNECTED) {
            ////	    			Log.i(TAG, "connection.getState() != AndroidBluetoothConnection.STATE_CONNECTED");
            ////	    			long conntectTime = System.currentTimeMillis();
            ////	    			if ((conntectTime - startConnectTime) > AndroidBluetoothConnection.BT_CONNECT_TIMEOUT){
            ////	    				// throw a CommunicationTimeoutException and break the loop.
            ////	    				throw new CommunicationTimeoutException();
            ////	    			}
            ////	    			/* end of if */
            ////	    		}
            ////	    		/* end of while */
            ////    		}
            //            if (connection.getState() == AndroidBluetoothConnection.STATE_NONE) {
            //                // Start the Android Bluetooth connection services to listen mode
            //                connection.listen();
            //
            //            } /* end of if */
            //	    	/* end of if */
            //            // Detect connected meter information.
            //            AbstractMeter meter = MeterManager.detectConnectedMeter(connection);
            //            Log.i(TAG, "AbstractMeter meter = MeterManager.detectConnectedMeter(connection);");
            //            //handler.sendEmptyMessage(65);
            //            // Start to import the medical record in meter
            //            // Sync current time to meter
            //            if (meter == null) {
            //                Log.i(TAG, "meter is null");
            //            }
            Date nowTime = new Date();
            //            assert meter != null;
            meter.setSystemClock(nowTime);
            // Get the project code
            String projectCode = meter.getDeviceModel().getProjectCode();
            // Get the serial number
            String serialNumber = meter.getSerialNumberRecord().getSerialNumber();
            // Import all medical record in meter
            List<AbstractRecord> recordList = new ArrayList<AbstractRecord>();
            // Get the storage record count
            for (int t = 0; t < 4; t++) {
                if (t == 0) {
                    int storageCount = meter.getStorageNumberAndNewestIndex(User.User1).getStorageNumber();
                    for (int i = 0; i < storageCount; i++) {
                        AbstractRecord record = meter.getStorageDataRecord(i, User.User1);
                        recordList.add(record);
                    }
                }
                if (t == 1) {
                    int storageCount = meter.getStorageNumberAndNewestIndex(User.User2).getStorageNumber();
                    for (int i = 0; i < storageCount; i++) {
                        AbstractRecord record = meter.getStorageDataRecord(i, User.User2);
                        recordList.add(record);
                    }
                }
                if (t == 2) {
                    int storageCount = meter.getStorageNumberAndNewestIndex(User.User3).getStorageNumber();
                    for (int i = 0; i < storageCount; i++) {
                        AbstractRecord record = meter.getStorageDataRecord(i, User.User3);
                        recordList.add(record);
                    }
                }
                if (t == 3) {
                    int storageCount = meter.getStorageNumberAndNewestIndex(User.User4).getStorageNumber();
                    for (int i = 0; i < storageCount; i++) {
                        AbstractRecord record = meter.getStorageDataRecord(i, User.User4);
                        recordList.add(record);
                    }
                }
            }
            /* end of for */
            // Convert the List<AbstractRecord> to Java object
            //取得UserId
            UserAdapter userAdapter = new UserAdapter(getApplicationContext());
            String userId = userAdapter.getUID();
            BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
            for (AbstractRecord record : recordList) {
                if (record instanceof BloodGlucoseRecord) {
                    //BG record
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String measurementDate = formatterDate.format(((BloodGlucoseRecord) record).getMeasureTime());
                    int bloodGlucoseTypeInt = ((BloodGlucoseRecord) record).getType().getValue();
                    int bgValue = ((BloodGlucoseRecord) record).getGlucoseValue();
                    //建立table欄位資料
                    BioData bioData = new BioData();
                    bioData.set_id(userId + serialNumber + bgValue + sdfForDataId.format(((BloodGlucoseRecord) record).getMeasureTime()));
                    bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                    bioData.setUserId(userId);
                    bioData.setDeviceId("TD-3261B");
                    if (bloodGlucoseTypeInt == 1) {
                        bioData.setAc(String.valueOf(bgValue));
                    } else if (bloodGlucoseTypeInt == 2) {
                        bioData.setPc(String.valueOf(bgValue));
                    } else if (bloodGlucoseTypeInt == 0) {
                        bioData.setNm(String.valueOf(bgValue));
                    }
                    bioData.setDeviceTime(measurementDate);
                    //存進Sqlite
                    if (bloodGlucoseTypeInt != 3) {
                        bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                        bioDataAdapter.createGlucose(bioData);
                    }
                } else if (record instanceof BloodPressureRecord) {
                    //BP record
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String measurementDate = formatterDate.format(((BloodPressureRecord) record).getMeasureTime());
                    int sysValue = ((BloodPressureRecord) record).getSystolicValue();
                    int diaValue = ((BloodPressureRecord) record).getDiastolicValue();
                    int pulseValue = ((BloodPressureRecord) record).getPulseValue();
                    //建立table欄位資料
                    BioData bioData = new BioData();
                    bioData.set_id(userId + serialNumber + sysValue + diaValue + pulseValue + sdfForDataId.format(((BloodPressureRecord) record).getMeasureTime()));
                    bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                    bioData.setDeviceId("TD-3261B");
                    bioData.setUserId(userId);
                    bioData.setPulse(String.valueOf(pulseValue));
                    bioData.setBhp(String.valueOf(sysValue));
                    bioData.setBlp(String.valueOf(diaValue));
                    bioData.setDeviceTime(measurementDate);
                    bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                    //存進Sqlite
                    bioDataAdapter.createBloodPressure(bioData);
                } else if (record instanceof TemperatureRecord) {
                    //Temperature record
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String measurementDate = formatterDate.format(((TemperatureRecord) record).getMeasureTime());
                    double thermometerValue = ((TemperatureRecord) record).getObjectTemperatureValue();
                    DecimalFormat df = new DecimalFormat("#.##");
                    String thermometerStringValue = df.format(thermometerValue);
                }
            /* end of if */
            }
            /* end of for */
            // Import done, clear all record in meter
            //meter.clearMeasureRecords(User.CurrentUser);
            // Clear done, power off the meter
            meter.turnOffMeterOrBluetooth(0);
            mConnection.disconnect();
            meter = null;
            handler.sendEmptyMessage(64);
        } catch (Exception e) {
            Log.e(TAG, "getTaidocDeviceDataV110", e);
            mConnection.disconnect();
            handler.sendEmptyMessage(64);
        }
    }

    public static final int MESSAGE_STATE_CONNECTING = 1;
    public static final int MESSAGE_STATE_CONNECT_FAIL = 2;
    public static final int MESSAGE_STATE_CONNECT_DONE = 3;
    public static final int MESSAGE_STATE_CONNECT_NONE = 4;
    public static final int MESSAGE_STATE_CONNECT_METER_SUCCESS = 5;
    public static final int MESSAGE_STATE_CHECK_METER_INFORMATION = 6;
    public static final int MESSAGE_STATE_CHECK_METER_BT_DISTENCE = 7;
    public static final int MESSAGE_STATE_CHECK_METER_BT_DISTENCE_FAIL = 8;
    /**
     * Android BT connection
     */
    private AndroidBluetoothConnection mConnection;

    /**
     * 控制Meter連通時以UI互動的Handler
     */
    private final Handler meterCommuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CONNECTING:
                    Log.i(TAG, "MESSAGE_STATE_CONNECTING");
                    break;
                case MESSAGE_STATE_CONNECT_DONE:
                    Log.i(TAG, "MESSAGE_STATE_CONNECT_DONE");
                    break;
                case MESSAGE_STATE_CONNECT_FAIL:
                    Log.i(TAG, "MESSAGE_STATE_CONNECT_FAIL");
                    break;
                case MESSAGE_STATE_CONNECT_NONE:
                    Log.i(TAG, "MESSAGE_STATE_CONNECT_NONE");
                    break;
                case MESSAGE_STATE_CONNECT_METER_SUCCESS:
                    Log.i(TAG, "MESSAGE_STATE_CONNECT_METER_SUCCESS");
                    break;
                case MESSAGE_STATE_CHECK_METER_BT_DISTENCE:
                    Log.i(TAG, "MESSAGE_STATE_CHECK_METER_BT_DISTENCE");
                    break;
                case MESSAGE_STATE_CHECK_METER_BT_DISTENCE_FAIL:
                    Log.i(TAG, "MESSAGE_STATE_CHECK_METER_BT_DISTENCE_FAIL");
                    break;
            } /* end of switch */
        }
    };

    /**
     * 初始化 Android Bluetooth Connection
     */
    private void setupAndroidBluetoothConnection() {
        if (mConnection == null) {
            Log.d(TAG, "setupAndroidBluetoothConnection()");
            // Initialize the android bluetooth connection to perform bluetooth connections
            mConnection = ConnectionManager.createAndroidBluetoothConnection(mBTConnectionHandler);
        } /* end of if */
    }

    /**
     * Connect Meter
     */
    private void connectMeter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_CONNECTING);
                    Log.i(TAG, "mConnection.getState() : " + mConnection.getState());
                    // 判斷是以Type One還是Type Two連接Meter
                    //                    switch (mConnection.getState()) {
                    //                        case AndroidBluetoothConnection.STATE_NONE:
                    //                            // Start the Android Bluetooth connection services to listen mode
                    //                            mConnection.listen();
                    //                            Log.i(TAG, "into listen mode");
                    //                            break;
                    //
                    //                        default:
                    if (mConnection.getState() != AndroidBluetoothConnection.STATE_CONNECTED) {
                        // 取得Bluetooth Device資訊
                        BluetoothDevice device = BluetoothUtil.getPairedDevice(mTaidocMacAddress);
                        // Attempt to connect to the device
                        mConnection.connect(device);
                                /* 確認是否連接上meter，time out 為 10秒 */
                        long startConnectTime = System.currentTimeMillis();
                        while (mConnection.getState() != AndroidBluetoothConnection.STATE_CONNECTED) {
                            long conntectTime = System.currentTimeMillis();
                            if ((conntectTime - startConnectTime) > (AndroidBluetoothConnection.BT_CONNECT_TIMEOUT)) {
                                throw new CommunicationTimeoutException();
                            } /* end of if */
                        } /* end of while */
                        meter = MeterManager.detectConnectedMeter(mConnection);
                        Log.i(TAG, "meter is connected!!!");
                        // Only if the state is STATE_NONE, do we know that we haven't started
                        // already
                    }
                    //                            break;
                    //                    }
                } catch (CommunicationTimeoutException e) {
                    LogUtil.error(this.getClass(), e.getMessage(), e);
                    meterCommuHandler.sendEmptyMessage(MESSAGE_STATE_CONNECT_FAIL);
                }   /* end of try-catch-finally */
                Looper.loop();
            }
        }).start();
    }

    // Handlers
    // The Handler that gets information back from the android bluetooth connection
    private final Handler mBTConnectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PCLinkLibraryConstant.MESSAGE_STATE_CHANGE:
                    if (true) {
                        Log.i(TAG, "mBTConnectionHandler MESSAGE_STATE_CHANGE: " + msg.arg1);
                    } /* end of if */
                    switch (msg.arg1) {
                        case AndroidBluetoothConnection.STATE_CONNECTED_BY_LISTEN_MODE:
                            //                            Activity actMain = null;
                            //                            if (MainActivity.instance != null) {
                            //                                actMain = MainActivity.instance;
                            //                            }
                            //                            TextView textViewMainMessage = null;
                            //                            if (actMain != null) {
                            //                                Log.i(TAG, "Taidoc On line");
                            ////                                textViewMainMessage = (TextView) actMain.findViewById(R.id.textviewmessagearea);
                            ////                                textViewMainMessage.setVisibility(View.VISIBLE);
                            ////                                textViewMainMessage.setText("血糖血壓機已連線");
                            //                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        meter = MeterManager.detectConnectedMeter(mConnection);
                                        getTaidocDeviceDataV110(null);
                                    } catch (Exception e) {
                                        Log.e(TAG, "meter getTaidocDeviceDataV110 : ", e);
                                        mConnection.disconnect();
                                        meter = null;
                                    }
                                }
                            }.start();

                            break;
                        case AndroidBluetoothConnection.STATE_CONNECTED:
                            //  關閉BLE掃描
                            BluetoothLeService.BLEService.removeHandler();
                            BluetoothLeService.BLEService.searchBLEDevice(false);
                            break;
                        case AndroidBluetoothConnection.STATE_CONNECTING:
                            // 暫無需特別處理的事項
                            break;
                        case AndroidBluetoothConnection.STATE_LISTEN:
                            // 暫無需特別處理的事項
                            break;
                        case AndroidBluetoothConnection.STATE_NONE:
                            // 暫無需特別處理的事項
                            break;
                    } /* end of switch */
                    break;
                case PCLinkLibraryConstant.MESSAGE_TOAST:
                    // 暫無需特別處理的事項
                    break;
                default:
                    break;
            }
        }
    };

    //泰博血壓血糖機Taidoc-Device TD3261B  V1.0.3
    //  	public void getTaidocDeviceData(String deviceMac){
    //  		Log.i(TAG, "getTaidocDeviceData()");
    //  		try{
    //
    //  			Log.i(TAG, "Get BluetoothDevice");
    //  			// Get the paired meter to a BluetoothDevice object
    //  			BluetoothDevice remoteDevice = BluetoothUtil.getPairedDevice(deviceMac);
    //  			Log.i(TAG, "Get BluetoothDevice");
    //
    //  			AbstractMeter meter = MeterManager.detectConnectedMeter(new BluetoothConnection(remoteDevice));
    //  			Log.i(TAG, "Get meter");
    //
    //  			handler.sendEmptyMessage(65);
    //
    //  			// Start to import the medical record in meter
    //  			// Connect the meter
    //  			meter.connectMeter();
    //  			Log.i(TAG, "meter connectMeter");
    //
    //  			// Sync current time to meter
    //  			Date nowTime = new Date();
    //  			meter.setSystemClock(nowTime);
    //
    //  			// Get the project code
    //  			String projectCode = meter.getDeviceModel().getProjectCode();
    //  			Log.i(TAG, "projectCode : " + projectCode);
    //
    //  			// Get the serial number
    //  			String serialNumber = meter.getSerialNumberRecord().getSerialNumber();
    //  			Log.i(TAG, "serialNumber : " + serialNumber);
    //
    //  			// Get the storage record count
    //			int storageCount = meter.getStorageNumberAndNewestIndex(User.CurrentUser).getStorageNumber();
    //			Log.i(TAG, "storageCount : " + storageCount);
    //
    //			//meter.getAllStorageDataRecord(User.AllUser);
    //			/*
    //			// Import all medical record in meter
    //			List<AbstractRecord> recordList = new ArrayList<AbstractRecord>();
    //
    //			for (int i = 0; i < storageCount; i++) {
    //				AbstractRecord record = meter.getStorageDataRecord(i, User.CurrentUser);
    //				recordList.add(record);
    //			}
    //			*/
    //			for(int i=0;i<4;i++){
    //				Log.i(TAG, "before recordList");
    //				List<AbstractRecord> recordList = null;
    //				if(i==0){
    //					recordList = meter.getAllStorageDataRecord(User.User1);
    //				}else if(i==1){
    //					recordList = meter.getAllStorageDataRecord(User.User2);
    //				}else if(i==2){
    //					recordList = meter.getAllStorageDataRecord(User.User3);
    //				}else if(i==3){
    //					recordList = meter.getAllStorageDataRecord(User.User4);
    //				}
    //				Log.i(TAG, "after recordList");
    //	  			/* end of for */
    //
    //	  			//取得UserId
    //	  			UserAdapter userAdapter = new UserAdapter(getApplicationContext());
    //	  			String userId = userAdapter.getUID();
    //	  			BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
    //	  			// Convert the List<AbstractRecord> to Java object
    //	  			for (AbstractRecord record : recordList) {
    //	  				if (record instanceof BloodGlucoseRecord) {
    //	  					Log.i(TAG, "BloodGlucoseRecord");
    //	  					//BG record
    //
    //	  					String measurementDate = formatterDate.format(((BloodGlucoseRecord) record).getMeasureTime());
    //	  					int bgValue = ((BloodGlucoseRecord) record).getGlucoseValue();
    //	  					BloodGlucoseType bloodGlucoseType = ((BloodGlucoseRecord) record).getType();
    //	  					int bloodGlucoseTypeInt = bloodGlucoseType.getValue();
    //	  					Log.i(TAG, "measurementDate : " + measurementDate + " , bgValue : " + bgValue + ",bloodGlucoseTypeInt : " + bloodGlucoseTypeInt);
    //
    //	  					//建立table欄位資料
    //	  					BioData bioData = new BioData();
    //	  					bioData.set_id(serialNumber + sdfForDataId.format(((BloodGlucoseRecord) record).getMeasureTime()));
    //	  					bioData.setInputType(Constant.UPLOAD_INPUT_TYPE_DEVICE);
    //	  					bioData.setUserId(userId);
    //	  					bioData.setDeviceId("TD-3261B");
    //	  					if(bloodGlucoseTypeInt==1){
    //	  						bioData.setAc(String.valueOf(bgValue));
    //	  					}else if(bloodGlucoseTypeInt==2){
    //	  						bioData.setPc(String.valueOf(bgValue));
    //	  					}else if(bloodGlucoseTypeInt==0){
    //	  						bioData.setNm(String.valueOf(bgValue));
    //	  					}
    //	  					bioData.setDeviceTime(measurementDate);
    //	  					//存進Sqlite
    //	  					if(bloodGlucoseTypeInt!=3){
    //	  						bioDataAdapter.createGlucose(bioData);
    //	  					}
    //
    //	  				}
    //	  				if (record instanceof BloodPressureRecord) {
    //	  					Log.i(TAG, "BloodPressureRecord");
    //	  					//BP record
    //
    //	  					String measurementDate = formatterDate.format(((BloodPressureRecord) record).getMeasureTime());
    //	  					int sysValue = ((BloodPressureRecord) record).getSystolicValue();
    //	  					int diaValue = ((BloodPressureRecord) record).getDiastolicValue();
    //	  					int pulseValue = ((BloodPressureRecord) record).getPulseValue();
    //	  					Log.i(TAG, "measurementDate : " + measurementDate + " , sysValue : " + sysValue + " , diaValue : " + diaValue + " , pulseValue : " + pulseValue);
    //
    //	  					//建立table欄位資料
    //	  					BioData bioData = new BioData();
    //	  					bioData.set_id(serialNumber + sdfForDataId.format(((BloodPressureRecord) record).getMeasureTime()));
    //	  					bioData.setInputType(Constant.UPLOAD_INPUT_TYPE_DEVICE);
    //	  					bioData.setDeviceId("TD-3261B");
    //	  					bioData.setUserId(userId);
    //	  					bioData.setPulse(String.valueOf(pulseValue));
    //	  					bioData.setBhp(String.valueOf(sysValue));
    //	  					bioData.setBlp(String.valueOf(diaValue));
    //	  					bioData.setDeviceTime(measurementDate);
    //	  					//存進Sqlite
    //	  					bioDataAdapter.createBloodPressure(bioData);
    //	  				}
    //	  				if (record instanceof TemperatureRecord) {
    //	  					Log.i(TAG, "TemperatureRecord");
    //	  					//Temperature record
    //
    //	  					String measurementDate = formatterDate.format(((TemperatureRecord) record).getMeasureTime());
    //	  					Log.i(TAG, "measurementDate : " + measurementDate);
    //	  					double thermometerValue = ((TemperatureRecord) record).getObjectTemperatureValue();
    //	  					DecimalFormat df = new DecimalFormat("#.##");
    //	  					String thermometerStringValue = df.format(thermometerValue);
    //	  					Log.i(TAG, "thermometerStringValue : " + thermometerStringValue);
    //	  				}
    //	  				/* end of if */
    //	  			}
    //			}
    //  			/* end of for */
    //
    //  			// Import done, clear all record in meter
    //  			//meter.clearMeasureRecords(User.CurrentUser);
    //
    //  			// Clear done, power off the meter
    //  			meter.turnOffMeterOrBluetooth(0);
    //  			meter.disconnectMeter();
    //
    //  		}catch(Exception e){
    //  			Log.e(TAG, "Method getTaidocDeviceData() Error : " + e);
    //  			handler.sendEmptyMessage(64);
    //  		}
    //  	}

    //========================百略==========================
    private BTWatchBPhomeConnService mBTWBPHConnService = null;
    //private BluetoothAdapter mBluetoothAdapter = null;
    private BTWatchBPhome mBTWatchhomeCmd;
    //handle message
    public static final int MESSAGE_WATCHBPHOME_RECOGNIZE = 30;
    public static final int MESSAGE_WATCHBPHOME_VERSION = 31;
    public static final int MESSAGE_WATCHBPHOME_WRITE_DATE_TIME = 32;
    public static final int MESSAGE_WATCHBPHOME_READ_NONUPLOAD_COUNT_AND_SID = 33;

    private boolean getWatchBPhomeData(String address, boolean secure) {
        // Get the device MAC address
        Log.i(TAG, "getWatchBPhomeData()");

        mBtDeviceAddress = address;
        // Get the BluetoothDevice object
        // Attempt to connect to the device
        Log.d(TAG, "getWatchBPhomeData(): mac address = " + address);
        try {
            Log.i("info", "mBTWBPHConnService.getState():" + mBTWBPHConnService.getState() + " / BTWatchBPhomeConnService.STATE_CONNECTED: " + BTWatchBPhomeConnService.STATE_CONNECTED);
            Log.i("info", "address :" + address + " / mBtDeviceAddress : " + mBtDeviceAddress);

            if (address.equals(mBtDeviceAddress)) {
                if (mBTWBPHConnService.getState() != BTWatchBPhomeConnService.STATE_CONNECTED) {
                    Log.i(TAG, "getWatchBPhomeData, mBTWBPHConnService.getState() != mBTWBPHConnService.STATE_CONNECTED");
                    if (null == mBtDevice) {
                        Log.i(TAG, "null==mBtDevice");
                        mBtDevice = mBtAdapter.getRemoteDevice(mBtDeviceAddress);
                    }
                    mBTWBPHConnService.connect(mBtDevice, secure);
                } else {
                    Log.i(TAG, "getWatchBPhomeData, start cmd");
                    // a sepcial case: since no reconnection, we need to start all future
                    startBTWBPCmdService();
                }
            } else {
                mBtDeviceAddress = address;
                Log.i(TAG, "mBtDeviceAddress = " + mBtDeviceAddress);
                // 指定連接藍芽設備mBtDeviceAddress
                mBtDevice = mBtAdapter.getRemoteDevice(mBtDeviceAddress);
                mBTWBPHConnService.connect(mBtDevice, secure);
            }
        } catch (Exception e) {
            Log.e(TAG, "getWatchBPhomeData() Error : " + e + "error message : " + e.getMessage());
            handler.sendEmptyMessage(62);
        }
        handler.sendEmptyMessage(100);
        return true;
    }

    private void startBTWBPCmdService() {
        Log.i(TAG, "startBTWBPCmdService ()");
        //mWaitResponseProgBar.setVisibility(View.VISIBLE);
        if (mBTAsVoice2Cmd != null) {
            stopBTWBPHCmdService();
        }
        Log.i("info", "===============mBTAV2ConnService========" + mBTAV2ConnService);
        mBTWatchhomeCmd = new BTWatchBPhome(this, mHandler, mBTAV2ConnService);
        Log.i(TAG, "startBTWBPCmdService();mBtDeviceAddress = " + mBtDeviceAddress);

        //mBHDevCmd.set
        mBTWatchhomeCmd.startBHDCmdService(mBtDeviceAddress);
    }

    private void initBTWBPHConnService() {
        Log.d(TAG, "initBTWBPHConnService()");
        // Initialize the BHDCService to perform bluetooth connections
        mBTWBPHConnService = new BTWatchBPhomeConnService(this, mHandler);
        //mWaitForConnection = 1;
    }

    private void startBTWBPHCmdService() {
        Log.i(TAG, "startBTWBPHCmdService ()");
        //mWaitResponseProgBar.setVisibility(View.VISIBLE);
        if (mBTWatchhomeCmd != null) {
            stopBTWBPHCmdService();
        }
        mBTWatchhomeCmd = new BTWatchBPhome(this, mHandler, mBTAV2ConnService);
        Log.i(TAG, "startBTWBPHCmdService();mBtDeviceAddress = " + mBtDeviceAddress);
        //mBHDevCmd.set
        mBTWatchhomeCmd.startBHDCmdService(mBtDeviceAddress);
    }

    private void stopBTWBPHCmdService() {
        if (mBTWatchhomeCmd != null) {
            while (mBTWatchhomeCmd.mCmdThread.isAlive()) {
                try {
                    //先執行 mCmdThread
                    mBTWatchhomeCmd.mCmdThread.join(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mBTWatchhomeCmd.mCmdThread.isAlive()) {
                    mBTWatchhomeCmd.mCmdThread.interrupt();
                }
            }
        }
    }
    //=======================百略End========================

    //========================五鼎===========================
    private String mBtDeviceAddress = null;
    //private BTAsVoice2GSConnService mBTAV2ConnService = null;
    private BluetoothChatService mBTAV2ConnService = null;
    private BluetoothDevice mBtDevice = null;
    //private BluetoothAdapter mBluetoothAdapter = null;
    private BTAsVoice2GlucoSure mBTAsVoice2Cmd;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public LinkedBlockingQueue<byte[]> mSynchMsgQueue;
    //handle message
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_RECOGNIZE = 20;
    public static final int MESSAGE_VERSION = 21;
    public static final int MESSAGE_WRITE_DATE_TIME = 22;
    public static final int MESSAGE_READ_NONUPLOAD_COUNT_AND_SID = 23;
    public static final int MESSAGE_DISABLE_BT = 24;
    //百略
    public static final int MESSAGE_READ_FOR_TIME_OF_DEVICE = 30;
    public static final int MESSAGE_SET_THE_TIME_OF_DEVICE = 31;
    public static final int MESSAGE_SET_THE_TIME_OF_DEVICE_END = 32;
    public static final int MESSAGE_SENT_USUAL_MODE_MEMORY = 33;
    public static final int MESSAGE_SENT_DIAGNOSTIC_MODE_MEMORY = 34;
    public static final int MESSAGE_DISCONNECT = 35;
    public static final int MESSAGE_WAIT_PUSH_DATA = 36;

    public static final int MESSAGE_CHANGE_TEXT = 80;

    //五鼎欣聲血糖機 ASVoice2系列
    private boolean connASVoice2Dev(String deviceMAC, boolean secure) {
        try {
            // Get the device MAC address
            Log.i(TAG, "connASVoice2Dev()");

            mBtDeviceAddress = deviceMAC;
            // Get the BluetoothDevice object
            // Attempt to connect to the device
            Log.d(TAG, "connASVoice2Dev(): mac address = " + deviceMAC);

            if (deviceMAC.equals(mBtDeviceAddress)) {
                if (mBTAV2ConnService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    Log.i(TAG, "mBHDConnService.getState() != BHDConnService.STATE_CONNECTED");
                    if (null == mBtDevice) {
                        Log.i(TAG, "null==mBtDevice");
                        mBtDevice = mBtAdapter.getRemoteDevice(mBtDeviceAddress);
                    }
                    mBTAV2ConnService.connect(mBtDevice, secure);
                } else {
                    // a sepcial case: since no reconnection, we need to start all future
                    startBTAVCmdService();
                }
            } else {
                mBtDeviceAddress = deviceMAC;
                Log.i(TAG, "mBtDeviceAddress = " + mBtDeviceAddress);
                mBtDevice = mBtAdapter.getRemoteDevice(mBtDeviceAddress);
                mBTAV2ConnService.connect(mBtDevice, secure);
            }
            handler.sendEmptyMessage(100);
        } catch (Exception e) {
            handler.sendEmptyMessage(62);
        }
        return true;
    }

    private void initBTAV2ConnService() {
        Log.d(TAG, "initBTAV2ConnService()");
        // Initialize the BHDCService to perform bluetooth connections
        mBTAV2ConnService = new BluetoothChatService(this, mHandler);
        //mWaitForConnection = 1;
    }

    private void startBTAVCmdService() {
        Log.i(TAG, "startBTAVCmdService ()");
        //mWaitResponseProgBar.setVisibility(View.VISIBLE);
        if (mBTAsVoice2Cmd != null) {
            stopBTASVCmdService();
        }
        mBTAsVoice2Cmd = new BTAsVoice2GlucoSure(this, mHandler, mBTAV2ConnService);
        Log.i(TAG, "startBTAVCmdService();mBtDeviceAddress = " + mBtDeviceAddress);
        //mBHDevCmd.set
        mBTAsVoice2Cmd.startBHDCmdService(mBtDeviceAddress);
    }

    private void stopBTASVCmdService() {
        if (mBTAsVoice2Cmd != null) {
            while (mBTAsVoice2Cmd.mCmdThread.isAlive()) {
                try {
                    //先執行 mCmdThread
                    mBTAsVoice2Cmd.mCmdThread.join(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mBTAsVoice2Cmd.mCmdThread.isAlive()) {
                    mBTAsVoice2Cmd.mCmdThread.interrupt();
                }
            }
        }
    }

    private class AcceptThread extends Thread {

        private int mCmd;
        public static final int SEARCH_DEVICE = 0;
        public static final int BLUETOOTH_CLOSE = 1;
        public static final int AUTO_UPLOAD = 50;

        public static final int SLEEP_TIME = 10000;
        public static final int WAIT_TIME = 5000;

        public void setCmd(int cmd) {
            mCmd = cmd;
        }

        public void setStart(boolean isStart) {

        }

        @Override
        public void run() {
            switch (mCmd) {
                case SEARCH_DEVICE:
                    // Register for broadcasts when a device is discovered
                    //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    //GetBlueToothDeviceDataService.this.registerReceiver(mReceiver, filter);
                    try {
                        // Get a set of currently paired devices
                        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
                        Log.i(TAG, "pairedDevices size:" + pairedDevices.size());
                        // If there are paired devices, add each one to the ArrayAdapter
                        if (pairedDevices.size() > 0) {
                            //從SQLite取得支援的藍芽裝置名稱
                            DeviceMappingAdapter deviceMappingAdapter = new DeviceMappingAdapter(getApplicationContext());
                            ArrayList<DeviceMapping> listDevice = deviceMappingAdapter.getAllDeviceData();

                            for (BluetoothDevice device : pairedDevices) {
                                Log.i(TAG, device.getName() + "," + device.getAddress());
                                for (DeviceMapping deviceMapping : listDevice) {
                                    Log.i(TAG, "DeviceMapping.GetDeviceSn : " + deviceMapping.getDeviceId());
                                    // 如果抓到的設備 名稱 有在sqllite 內的 device id
                                    //                                    if (moduleDevice.indexOf(deviceMapping.getDeviceId()) == 0) {
                                    if (device.getName().indexOf(deviceMapping.getDeviceId()) == 0) {
                                        //Toast.makeText(getApplicationContext(), "MAC:" + device.getAddress(), Toast.LENGTH_LONG).show();
                                        if (deviceMapping.getDeviceSn().equals("2") || deviceMapping.getDeviceSn().equals("5")) {

                                            // ↓↓↓↓ james add
                                            //                                            handler.sendEmptyMessage(622);
                                            Log.i(TAG, "get Taidoc Device || FORA D40");
                                            //泰博3261B
                                            mTaidocMacAddress = device.getAddress();
                                            Log.i(TAG, "Taidoc meter : " + meter);
                                            if (meter == null) {
                                                setupAndroidBluetoothConnection();
                                                connectMeter();
                                            }
                                            getTaidocDeviceDataV110(device.getAddress());
                                        } else if (deviceMapping.getDeviceSn().equals("1")) {

                                            // ↓↓↓↓ james add
                                            //handler.sendEmptyMessage(621);
                                            Log.i(TAG, "get AS Voice 2 - 五鼎 Device");
                                            if (mBTAV2ConnService == null) {
                                                initBTAV2ConnService();
                                            }
                                            deviceSwitch = 0;
                                            //五鼎AS Voice 2
                                            connASVoice2Dev(device.getAddress(), true);
                                        } else if (deviceMapping.getDeviceSn().equals("3")) {
                                            //Log.i(TAG, "deviceMapping : " + deviceMapping.getDeviceId() + ", deviceName : " + device.getName());
                                            Log.i(TAG, "get Wath BP home Device, Device ID is ML & Microlife");

                                            // ↓↓↓↓ james add
                                            //handler.sendEmptyMessage(620);
                                            if (mBTWBPHConnService == null) {
                                                initBTWBPHConnService();
                                            }
                                            deviceSwitch = 1;
                                            //百略Watch BP home,  bluetooth Name:ML-xxxx or Microlife
                                            getWatchBPhomeData(device.getAddress(), true);
                                        }
                                        sleep(1000);
                                    }
                                }
                            }
                        }
                        sleep(WAIT_TIME);
                    } catch (Exception e) {
                        Log.e(TAG, "SEARCH_DEVICE ERROR / error message: " + e.getMessage());
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                    break;

                case BLUETOOTH_CLOSE:
                    try {
                        sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "BLUETOOTH_CLOSE ERROR/ error message: " + e.getMessage());
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                    break;

                case AUTO_UPLOAD:
                    Log.i(TAG, "AUTO_UPLOAD");
                    try {
                        // James modify
                        WebServiceConnection webServiceConnection = new WebServiceConnection();
                        if (webServiceConnection.isOnline(getApplicationContext())) {
                            //查詢Update = 0
                            ArrayList<BioData> listBioData;
                            BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
                            listBioData = bioDataAdapter.getUploaded();

                            Log.i(TAG, "listBioData size : " + listBioData.size());

                            if (listBioData.size() > 0) {
                                //UserData
                                //                                UserAdapter userAdapter = new UserAdapter(getApplicationContext());
                                //                                tw.com.omnihealthgroup.healthcare.myhealthcare.dba.User user = userAdapter.getUserUIdAndPassword();
                                //                                Log.i(TAG, "user : " + user);
                                //上傳資料
                                //                                NetService netService = new NetService();
                                //                                String response = netService.CallUploadVitalSign(user, listBioData, true);
                                prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
                                JSONObject response = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, true);

                                //                                if (response != null) {
                                //                                    JSONObject sonResponse = new JSONObject(response);
                                //                                    String messageCode = sonResponse.getString("Message");
                                //
                                //                                    if (messageCode.equals(WebServiceConnection.MessageCodeSuccess) || messageCode.equals(WebServiceConnection.MessageCodeDataExist)) {
                                if (response != null && response.getString("message").toString().equals("Success.")) {
                                    //  送handler 呈現資料上傳成功
                                    handler.sendEmptyMessage(99);
                                    //  更新sql lite資料庫
                                    bioDataAdapter.updataUploaded(listBioData);
                                }
                                //                                }
                            }
                        }
                        sleep(SLEEP_TIME);

                    } catch (InterruptedException e) {
                        Log.e(TAG, "AutoUpdateService thread InterruptedException : " + e + " / error message :" + e.getMessage());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.e(TAG, "AutoUpdateService thread JSONException : " + e + " / error message :" + e.getMessage());
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(50);
                    break;
            }
        }
    }


    // 檢測網路是否連上
    private boolean checkInternet() {
        boolean result;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            result = info.isAvailable();
        }
        Log.d("info", "網路是否連上:" + result);
        return result;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "msg : " + msg.toString());
            //MainActivity
            //            Activity actMain = null;
            //
            //            if (MainActivity.instance != null) {
            //                actMain = MainActivity.instance;
            //            }
            //            TextView textViewMainMessage = null;
            //            if (actMain != null) {
            //                textViewMainMessage = (TextView) actMain.findViewById(R.id.textviewmessagearea);
            //                textViewMainMessage.setVisibility(View.VISIBLE);
            //                //textViewMainMessage.setText("blooth is connect");
            //            }
            switch (msg.what) {
                case 0:
                    Log.i(TAG, "handler case 0");
                    if (isStart) {
                        Log.i(TAG, "GetBlueToothDeviceDataService running");
                        //Toast.makeText(getApplicationContext(), "running", Toast.LENGTH_LONG).show();

                        // james add
                        mainThread = null;
                        mainThread = new AcceptThread();
                        mainThread.setCmd(50);
                        mainThread.start();
                    } else {
                        Log.i(TAG, "GetBlueToothDeviceDataService end");
                        //Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();
                    }
                    break;

                case 50:
                    // james add
                    mainThread = null;
                    mainThread = new AcceptThread();
                    if (mBtAdapter != null) {
                        mainThread.setCmd(0);
                        mainThread.start();
                    } else {
                        mainThread.setCmd(1);
                        mainThread.start();
                    }
                    break;

                //                case 60:
                //                    Log.i(TAG, "BloodPressure is not exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("設備未連線");
                //                    }
                //                    break;
                //                case 61:
                //                    Log.i(TAG, "BloodPressure is exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("血壓機已連線");
                //                        // ↓↓↓↓ james add
                ////		    				textViewMainMessage.setText("資料已從設備送出至手機");
                //                    }
                //                    break;
                //                case 62:
                //                    Log.i(TAG, "BloodGlucose is not exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("設備未連線");
                //                    }
                //                    break;
                //                case 63:
                //                    Log.i(TAG, "BloodGlucose is exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("血糖機已連線");
                //                    }
                //                    break;
                //                case 64:
                //                    Log.i(TAG, "checkboxTwoInOne is not exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("設備未連線");
                //                    }
                //                    break;
                //                case 65:
                //                    Log.i(TAG, "checkboxTwoInOne is exsit");
                //                    if (textViewMainMessage != null) {
                ////                        textViewMainMessage.setText("血糖血壓機已連線");
                //                    }
                //                    break;
                case 99:
                    //提示訊息
                    /*
                    Toast toast = Toast.makeText(getApplicationContext(), "資料上傳成功", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 20, 140);
					toast.show();
					*/
                    //                    getMessageDialog("藍芽3.0", "資料上傳成功");
                    initToast("藍芽3.0\n資料上傳成功");
                    break;
                case 100:
                    Log.i(TAG, "handler case 100");
                    break;
                // ↓↓↓↓ james add
                //				case 600:
                //					if(textViewMainMessage!=null){
                //						textViewMainMessage.setText("");
                //					}
                //					break;
                //				case 620:
                //					if(textViewMainMessage!=null){
                //						textViewMainMessage.setText("百略設備已連線");
                //					}
                //					break;
                //				case 621:
                //					if(textViewMainMessage!=null){
                //						textViewMainMessage.setText("五鼎設備已連線");
                //					}
                //					break;
                //				case 622:
                //					if(textViewMainMessage!=null){
                //						textViewMainMessage.setText("泰博設備已連線");
                //					}
                //					break;
            }
        }
    };

    //
    private void initToast(String message) {
        LinearLayout ll = new LinearLayout(this);
        ll.setBackgroundColor(Color.BLACK);
        TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setPadding(10, 5, 10, 5);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setText(message);

        ll.addView(tv);

        Toast toastStart = new Toast(this);
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(ll);
        toastStart.show();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage ().............................................................. ");
            switch (msg.what) {
                //五鼎
                case MESSAGE_RECOGNIZE:
                    Log.i(TAG, "MESSAGE_RECOGNIZE");
                    handler.sendEmptyMessage(63);
                    String strrecognize = (String) msg.obj;
                    if (strrecognize != null && strrecognize != "") {
                        Log.i(TAG, "Device Line On! strrecognize:" + strrecognize);
                        //Toast.makeText(getApplicationContext(), "五鼎血糖機已連線！", Toast.LENGTH_LONG).show();
                        mBTAsVoice2Cmd.startCmdThread(BTAsVoice2GlucoSure.CMD_GET_METER_VERSION);
                    }
                    break;
                case MESSAGE_VERSION:
                    Log.i(TAG, "MESSAGE_VERSION");
                    mBTAsVoice2Cmd.startCmdThread(BTAsVoice2GlucoSure.CMD_WRITE_DATE_TIME);
                    break;
                case MESSAGE_WRITE_DATE_TIME:
                    Log.i(TAG, "MESSAGE_WRITE_DATE_TIME");
                    String srtReturn = (String) msg.obj;
                    if (srtReturn.equals("OK")) {
                        //Toast.makeText(getApplicationContext(), "已設定藍芽裝置的日期與時間！", Toast.LENGTH_LONG).show();
                        mBTAsVoice2Cmd.startCmdThread(BTAsVoice2GlucoSure.CMD_READ_NONUPLOAD_COUNT_AND_SID);
                    }
                    break;
                case MESSAGE_READ_NONUPLOAD_COUNT_AND_SID:
                    Log.i(TAG, "MESSAGE_READ_NONUPLOAD_COUNT_AND_SID");
                    String srtCountSIDReturn = (String) msg.obj;
                /*
                if (srtCountSIDReturn.equals("OK")){
            		mBTAsVoice2Cmd.startCmdThread(BTAsVoice2GlucoSure.CMD_DISABLE_BT);
            	}
            	*/
                    break;
                case MESSAGE_DISABLE_BT:
                    Log.i(TAG, "MESSAGE_DISABLE_BT");
                    break;
                //百略
                case MESSAGE_READ_FOR_TIME_OF_DEVICE:
                    Log.i(TAG, "MESSAGE_READ_FOR_TIME_OF_DEVICE");
                    String srtReadForTimeOfDeviceReturn = (String) msg.obj;
                    Log.i(TAG, "srtReadForTimeOfDeviceReturn : " + srtReadForTimeOfDeviceReturn);
                    mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.CMD_SET_THE_TIME_OF_DEVICE);
                    break;
                case MESSAGE_SET_THE_TIME_OF_DEVICE:
                    Log.i(TAG, "MESSAGE_SET_THE_TIME_OF_DEVICE");
                    String srtSetTimeReturn = (String) msg.obj;
                    if (srtSetTimeReturn.equals("6")) {
                        mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.CMD_SET_TIME);
                    }
                    break;
                case MESSAGE_CHANGE_TEXT:
                    handler.sendEmptyMessage(61);
                    break;
                case MESSAGE_SET_THE_TIME_OF_DEVICE_END:
                    Log.i(TAG, "MESSAGE_SET_THE_TIME_OF_DEVICE_END");
                    String srtSetTheTimeOfDeviceEnd = (String) msg.obj;
                    Log.i(TAG, "srtSetTheTimeOfDeviceEnd : " + srtSetTheTimeOfDeviceEnd);
                    if (srtSetTheTimeOfDeviceEnd.equals("6")) {
                        mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.CMD_SENT_USUAL_MODE_MEMORY);
                    }
                    break;
                case MESSAGE_SENT_USUAL_MODE_MEMORY:
                    Log.i(TAG, "MESSAGE_SENT_USUAL_MODE_MEMORY");
                    String srtSentUsualModeMemory = (String) msg.obj;
                    handler.sendEmptyMessage(61);
                    Log.i(TAG, "srtSentUsualModeMemory : " + srtSentUsualModeMemory);
                    mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.CMD_SENT_DIAGNOSTIC_MODE_MEMORY);
                    break;
                case MESSAGE_SENT_DIAGNOSTIC_MODE_MEMORY:
                    Log.i(TAG, "MESSAGE_SENT_DIAGNOSTIC_MODE_MEMORY");
                    String srtSentDiagnosticModeMemory = (String) msg.obj;
                    Log.i(TAG, "srtSentDiagnosticModeMemory : " + srtSentDiagnosticModeMemory);
                    mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.CMD_DISCONNECT);
                    break;

                case MESSAGE_DISCONNECT:
                    Log.i(TAG, "MESSAGE_DISCONNECT");
                    String srtDisconnect = (String) msg.obj;
                    Log.i(TAG, "srtDisconnect : " + srtDisconnect);

                    //                    //MainActivity
                    //                    Activity actMain = null;
                    //                    if (MainActivity.instance != null) {
                    //                        actMain = MainActivity.instance;
                    //                    }
                    //                    TextView textViewMainMessage = null;
                    //                    if (actMain != null) {
                    //                        textViewMainMessage = (TextView) actMain.findViewById(R.id.textviewmessagearea);
                    //                        textViewMainMessage.setVisibility(View.VISIBLE);
                    //                    }
                    //                    textViewMainMessage.setText("設備未連線");
                    break;

                case MESSAGE_WAIT_PUSH_DATA:
                    Log.i(TAG, "MESSAGE_WAIT_PUSH_DATA");
                    mBTWatchhomeCmd.startCmdThread(BTWatchBPhome.WAIT_PUSH_DATA);
                    //========================================================================================
                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "mHandler MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BTAsVoice2GSConnService.STATE_CONNECTED:
                            Log.i(TAG, "deviceSwitch : " + deviceSwitch);
                            switch (deviceSwitch) {
                                case 0:
                                    startBTAVCmdService();
                                    break;
                                case 1:
                                    startBTWBPHCmdService();
                                    break;
                            }
                            break;
                        case BTAsVoice2GSConnService.STATE_CONNECTING:
                            Log.i(TAG, "BTAsVoice2GSConnService.STATE_CONNECTING: ");
                            break;
                        case BTAsVoice2GSConnService.STATE_LISTEN:
                            Log.i(TAG, "BTAsVoice2GSConnService.STATE_LISTEN: ");
                            break;
                        case BTAsVoice2GSConnService.STATE_NONE:
                            Log.i(TAG, "BTAsVoice2GSConnService.STATE_NONE: ");
                            /////////////////////////////////////////////////////////////////
                            // mb-ebti 12-21-2011
                            /////////////////////////////////////////////////////////////////
                            //setStatus(R.string.title_not_connected);
                            break;
                        case BTWatchBPhomeConnService.STATE_CONNECTED:
                            startBTWBPHCmdService();
                            Log.i(TAG, "mBTWatchBPhomeCmd.mCmdThread is over (=" + mBTWatchhomeCmd.mCmdThread);
                            break;
                        case BTWatchBPhomeConnService.STATE_CONNECTING:
                            Log.i(TAG, "BTWatchBPhomeConnService.STATE_CONNECTING: ");
                            break;
                        case BTWatchBPhomeConnService.STATE_LISTEN:
                            Log.i(TAG, "BTWatchBPhomeConnService.STATE_LISTEN: ");
                            break;
                        case BTWatchBPhomeConnService.STATE_NONE:
                            Log.i(TAG, "BTWatchBPhomeConnService.STATE_NONE: ");
                            break;
                    }
                    break;
            /*
             * 	message from "CmdThread"  
             */
                case MESSAGE_WRITE:
                    Log.i(TAG, "GetBlueToothDeviceDataService.MESSAGE_WRITE: ");
                    byte[] writeBuf = (byte[]) msg.obj;
                    break;
                case MESSAGE_READ:
                    Log.i(TAG, "GetBlueToothDeviceDataService.MESSAGE_READ: ");
                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.i(TAG, "GetBlueToothDeviceDataService.MESSAGE_DEVICE_NAME: ");
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Log.i(TAG, "DEVICE_NAME : " + mConnectedDeviceName);
                    //Toast.makeText(getApplicationContext(), "Connected to "
                    //               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Log.i(TAG, "GetBlueToothDeviceDataService.MESSAGE_TOAST: ");
                    //血糖
                    String messageGlucoseReturn = msg.getData().getString("glucose");
                    Log.i(TAG, "messageGlucoseReturn : " + messageGlucoseReturn);
                    if (messageGlucoseReturn != null) {
                        handler.sendEmptyMessage(62);
                    }
                    //血壓
                    String messagePressureReturn = msg.getData().getString("pressure");
                    Log.i(TAG, "messagePressureReturn : " + messagePressureReturn);
                    if (messagePressureReturn != null) {
                        handler.sendEmptyMessage(60);
                    }
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
}