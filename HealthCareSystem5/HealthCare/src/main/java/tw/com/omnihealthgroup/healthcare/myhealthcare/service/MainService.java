package tw.com.omnihealthgroup.healthcare.myhealthcare.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;

public class MainService extends Service {
    // Debugging
    private static final String TAG = "MainService";
    private static final boolean D = true;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_CLOSE = 6;

    // Message types sent from XenonBlueService Handler
    public static final int MESSAGE_XENON_INFO = 1;
    public static final int MESSAGE_DEVICE_ID = 2;
    public static final int MESSAGE_DEVICE_DATA = 3;
    public static final int MESSAGE_XENON_PACKAGE = 4;

    //Message types sent from XenonBPParser Handler
    public static final int MESSAGE_GOT_BP = 1;
    public static final int MESSAGE_GOT_BP_ERROR = 2;


    public static final int NOTIF_MAXID = 20;

    // Key names received from the ECGService Handler
    public static final String KY_INFO = "KY_Info";

    // Key Name of Burrfer Length Preference 
    public static final String PREF_XENON = "Xenon_Preferences";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //VibrationPattern
    private static final long[] VibrationPattern = new long[]{0, 30, 30, 50};

    // Name of the connected device
    private String mConnectedDeviceName = "";
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object of services
    private BluetoothService mBTService = null;
    private XenonBlueService mXBlueService = null;
    private XenonBPParser mXenonBPParser = null;

    @Override
    public void onCreate() {
        if (D)
            Log.e(TAG, "onCreate()");
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if (D)
            Log.e(TAG, "onStartCommand()");
        if (mBTService == null) {
            setupService();
            setupBroadcastReceiver();
            StartBTService();
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //return START_STICKY;

        //or
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (D)
            Log.e(TAG, "onDestroy()");
        if (mBTService != null)
            mBTService.stop();
        this.unregisterReceiver(mBroadcastReceiver);
    }


    private void setupService() {
        if (D)
            Log.e(TAG, "setupService()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTService = new BluetoothService(MainService.this, mHandler);
        mXBlueService = new XenonBlueService(MainService.this, mXenonBlueHandler);
        mXenonBPParser = new XenonBPParser(MainService.this, mXenonBPHandler);
    }

    private void setupBroadcastReceiver() {
        if (D)
            Log.e(TAG, "setupBroadcastReceiver()");
        IntentFilter filter = new IntentFilter();
        //Bluetooth功能狀態改變
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //網路連線能力改變
        filter.addAction(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
        //網路狀態改變
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void BlueToothSend(byte[] Cmd) {
        if (D)
            Log.e(TAG, "BlueToothSend()");
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothService.STATE_CONNECTED) {
            //  Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (Cmd.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            mBTService.write(Cmd);
        }
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //            Activity actMain = null;
            //            if (MainActivity.instance != null) {
            //                actMain = MainActivity.instance;
            //            }
            //            TextView textViewMainMessage = null;
            //            if (actMain != null) {
            //                textViewMainMessage = (TextView) actMain.findViewById(R.id.textviewmessagearea);
            //                textViewMainMessage.setVisibility(View.VISIBLE);
            //            }
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) {
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    }
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if (D) {
                                Log.e(TAG, "BluetoothService.STATE_CONNECTED");
                            }
                            //title bar右側顯示連線狀態
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            if (D) {
                                Log.e(TAG, "BluetoothService.STATE_CONNECTING");
                            }
                            break;
                        case BluetoothService.STATE_LISTEN:
                            if (D) {
                                Log.e(TAG, "BluetoothService.STATE_LISTEN");
                            }
                            break;
                        case BluetoothService.STATE_NONE:
                            if (D) {
                                Log.e(TAG, "BluetoothService.STATE_NONE");
                            }
                            //                            textViewMainMessage.setText("設備未連線");
                            //                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    if (D) {
                        Log.e(TAG, "MESSAGE_WRITE");
                    }
                    break;
                case MESSAGE_READ:
                    String moduleNameTag = "XenonBlue";
                    if (D) {
                        Log.e(TAG, "MESSAGE_READ, : " + moduleNameTag);
                    }
                    if (mConnectedDeviceName.contains(moduleNameTag)) {
                        //            		textViewMainMessage.setText("血壓計已連線");
                        byte[] readBuf = (byte[]) msg.obj;
                        mXBlueService.RcvDataHandler(readBuf);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Log.i(TAG, "mConnectedDeviceName : " + mConnectedDeviceName);
                    //textViewMainMessage.setText("血壓計已連線");
                /*
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                */
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_CONNECTION_CLOSE:
                    if (D) {
                        Log.i(TAG, "MESSAGE_CONNECTION_CLOSE:" + mConnectedDeviceName);
                    }
                    //textViewMainMessage.setText("設備未連線");
                    //BluetoothSocket Closed, reStart server Linstening
                    if (msg.arg1 == 1) {
                        if (D) {
                            Log.e(TAG, "ReStartBTService");
                        }
                        StopBTService();    //important
                        StartBTService();
                    } else if (D) {
                        Log.e(TAG, "NOT ReStartBTService");
                    }
                    break;
            }
        }
    };//end of [private final Handler mHandler = new Handler() ]

    //Xenon Ack Routine
    private void XenonAck(byte[] XenonInfo) {
        if (XenonInfo.length >= 4) {
            byte[] Ack = new byte[5];
            Ack[0] = 0x41;    //'A'
            Ack[1] = XenonInfo[0];
            Ack[2] = XenonInfo[1];
            Ack[3] = XenonInfo[2];
            Ack[4] = XenonInfo[3];
            BlueToothSend(Ack);
        }
    }

    private final Handler mXenonBlueHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (D) {
                Log.d(TAG, "in XenonBlueHandler");
            }
            switch (msg.what) {

                case MESSAGE_XENON_INFO:
                    if (D) {
                        Log.d(TAG, "MESSAGE_XENON_INFO");
                    }
                    //Xenon Info
                    //byte[] XenonInfo = (byte[]) msg.obj;
                    //XenonAck(XenonInfo);
                    break;
                case MESSAGE_DEVICE_ID:

                    break;
                case MESSAGE_DEVICE_DATA:
                    byte[] BPData = (byte[]) msg.obj;
                    mXenonBPParser.AddBPData(BPData);
                    break;
                case MESSAGE_XENON_PACKAGE:
                    if (D) {
                        Log.d(TAG, "MESSAGE_XENON_PACKAGE");
                    }
                    byte[] XenonPackage = (byte[]) msg.obj;
                    XenonAck(XenonPackage);        //Xenon Acking Back
                    boolean xenonVibration = true;
                    if (xenonVibration) {
                        //Start the vibration
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(VibrationPattern, -1);
                    }
                    break;
            }
        }
    };//end of [private final Handler mXenonBlueHandler = new Handler()]

    private final Handler mXenonBPHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (D) {
                Log.d(TAG, "in XenonBPHandler");
            }

            switch (msg.what) {

                case MESSAGE_GOT_BP:
                    Log.i(TAG, "MESSAGE_GOT_BP");
                    Bundle BPBundle = (Bundle) msg.obj;
                    int sys = BPBundle.getInt("SYS");
                    int dia = BPBundle.getInt("DIA");
                    int hr = BPBundle.getInt("HR");
                    long time = BPBundle.getLong("TIME");
                    Date BPDate = new Date(time);

                    UserAdapter userAdapter = new UserAdapter(getApplicationContext());
                    String uID = userAdapter.getUID();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //將資料寫進SQLite
                    BioData bioData = new BioData();
                    bioData.set_id(sdf.format(BPDate) + uID + "Xenonblue");
                    bioData.setDeviceId("Xenonblue");
                    bioData.setPulse(String.valueOf(hr));
                    bioData.setBhp(String.valueOf(sys));
                    bioData.setBlp(String.valueOf(dia));
                    bioData.setDeviceTime(sdf.format(BPDate));
                    bioData.setUserId(uID);
                    bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                    bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                    BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
                    bioDataAdapter.createBloodPressure(bioData);
                /*
                Toast.makeText(getApplicationContext(),
            			"S:"+Integer.toString(sys)+
            			"D:"+Integer.toString(dia)+
            			"H:"+Integer.toString(hr)+
            			BPDate.toString() ,Toast.LENGTH_SHORT).show();
            			*/
                    break;
                case MESSAGE_GOT_BP_ERROR:
                    break;
            }
        }
    };//end of [private final Handler mXenonBPHandler = new Handler()]

    //Dymanic System Info Broadcast Receiver
    //藍芽功能開啟/關閉處理器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    StopBTService();
                    //                    setOngoingNotification("藍牙已關閉，XenonBlue暫停中", "暫停XenonBlue上傳程式");

                } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    StartBTService();
                    //                    setOngoingNotification("XenonBlue上傳程式已啟用", "重新啟用XenonBlue上傳程式");
                }
            } else if (action.equals(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED)) {
                //NetworkInfo NetInfo = CM.getActiveNetworkInfo();
                //NetworkInfo NetInfo = intent.getExtras();
                if (D) {
                    Log.d(TAG, "ACTION_BACKGROUND_DATA_SETTING_CHANGED");
                }
                //Bundle Pref_Bundle = intent.getExtras();

                //Start the vibration
                //Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                //vibrator.vibrate(VibrationPattern,-1);

            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (D) {
                    Log.e(TAG, "CONNECTIVITY_ACTION");
                }
            }
        }
    };

    /*
    //將service執行權限拉到前景
    //置於進行中(Ongoing)背景程式，可避免被android系統移除
    public void setOngoingNotification(String titleStr,String NoticeStr )
    {
    	if (D) Log.d(TAG, "in OngoingNotif");
       	Intent notifyIntent = new Intent(this, null);
       	PendingIntent appIntent = PendingIntent.getActivity(this, 0,
    			notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    	//Notification OngoingNotif = new Notification(R.drawable.xenonblue, NoticeStr ,0);
    	//OngoingNotif.setLatestEventInfo(this,titleStr , "選取進入程式設定畫面", appIntent);
    	//startForeground(NOTIF_MAXID,OngoingNotif);//以NOTIF_MAXID作為固定ID
    }

    //通知提示
    private void setNotification(int iconImg,String icontext)
    {
    	//Intent notifyIntent = new Intent(this,KY202Monitor.class);//每次新增new activity
    	Intent notifyIntent = new Intent();							//空 activity
       	PendingIntent appIntent = PendingIntent.getActivity(this, 0,
    			notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    	Notification newNotif = new Notification(iconImg,"XenonBlue訊息",System.currentTimeMillis());
  		if (NotifSound){newNotif.defaults = Notification.DEFAULT_SOUND;}//提示音
    	newNotif.flags = Notification.FLAG_AUTO_CANCEL;		//點選後清除
    	newNotif.setLatestEventInfo(this,"收到XenonBlue資料",icontext,appIntent);

    	//新增的notification，各自獨立顯示於status bar，超過NOTIF_MAXID上限才複寫，暫定20筆
    	iNotifID++;
    	if (iNotifID == NOTIF_MAXID){iNotifID = 0;}
    	notifManager.notify(iNotifID,newNotif);
    }
    */
    private void StartBTService() {
        mBTService = new BluetoothService(this, mHandler);
        if (D) {
            Log.d(TAG, "on BTService.start() ");
        }
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBTService.start();
            }
        } else {
            if (D) {
                Log.d(TAG, "mBTService == null");
            }
        }
    }

    private void StopBTService() {
        if (D) {
            Log.d(TAG, "on BTService.Stop() ");
        }
        if (mBTService != null) {
            // Stop the Bluetooth chat services               	
            mBTService.stop();
        } else {
            if (D) {
                Log.d(TAG, "mBTService == null");
            }
        }
    }   
    
    /*
    private void setXenonVibration(boolean b)
    {
    //	SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(this);
	//	XenonVibration = Settings.getBoolean(KY202Monitor.PREF_VIBRATION,false);
    	XenonVibration = b;
    	if (D){Log.d(TAG, "XenonVibration :"+XenonVibration);}
    }
    private void setNotifSound()
    {
    	//SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(this);
    	NotifSound = Settings.getBoolean(SettingsPreferenceActivity.PREF_SOUND,false);   	
    	//NotifSound
    	if (D){  Log.d(TAG, "NotifSound "+NotifSound);}
    }
    private void setXenonUpload()
    {
    	SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(MainService.this);
    	XenonUpload = Settings.getBoolean(SettingsPreferenceActivity.PREF_UPLOAD,false);	
    	if (D){  Log.d(TAG, "XenonUpload "+ XenonUpload);}
    }
    private void setUploadIP()
    {
    	SharedPreferences Settings = PreferenceManager.getDefaultSharedPreferences(MainService.this);
    	String IPStr = Settings.getString(SettingsPreferenceActivity.PREF_IP,UploadService.DEFAULTADDRESS);
    	mUploadService.SetIP(IPStr);
    	if (D){  Log.d(TAG, "IP set:"+IPStr);}
    }
    */
    
    /*
    //Save the BufferLength in Shared Preferences
    private void SaveBufferLengthInfo(int Length)
    {
    	SharedPreferences XenonPref = getSharedPreferences(PREF_XENON,MODE_PRIVATE);
    	SharedPreferences.Editor PE = XenonPref.edit();
    	PE.putInt("BUFFER_LENGTH", Length);
    	PE.commit();
    }
    */

    //判斷是否為空字串
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return "".equals(str.trim());
    }

    //判斷是否為整數
    public static boolean isDigit(String str) {
        if (!isEmpty(str)) {
            String digitPattern = "^[0-9]+$";
            return str.matches(digitPattern);
        }
        return false;
    }
    /*
    private String BytesToString(byte[] ByteData)
    {	
    	String ByteString = "";
    	
    	for(int i=0;i< ByteData.length;i++)
    	{ 
    		ByteString = ByteString + '['+Integer.toString(ByteData[i] & 0xff)+']';
    	}
    	return ByteString;
    }
    */
}

