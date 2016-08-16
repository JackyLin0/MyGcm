package tw.com.omnihealthgroup.healthcare.myhealthcare.service;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;

public class BluetoothLeService {
    private final static String TAG = "BluetoothLeService";
    public static BluetoothLeService BLEService = new BluetoothLeService();
    private SharedPreferences prf;
    private String macId = null;

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private ProgressDialog loadingProgressDialog;

    private BluetoothAdapter mBluetoothAdapter = null;
    public static final long SCAN_PERIOD = 30000;  // 掃描的時間長度
    private BluetoothDevice bleDevice;
    private Handler mHandler;
    private Runnable mRunnable;

    private boolean checkBT30 = false;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    private byte[] writeValue = new byte[8];
    private static final byte START_BYTE = (byte) 0x51;
    private static final byte STOP_BYTE = (byte) 0xA3;
    private int userDataIndex = 0;
    private int dataIndex = 0;
    private int pOrG;

    //sqlite的資料格式
    private String measurementDate;
    private BioDataAdapter mBioDataAdapter;

    public static final String DeviceID = "FORA D40";
    public static final UUID Device_SERVICE_UUID = UUID.fromString("00001523-1212-EFDE-1523-785FEABCD123");
    public static final UUID Device_CHARACTERISTIC_UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123");

    /**
     * 初始化BLE管理器、適配器
     *
     * @param context 執行的Activity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothAdapter initBlueTooth(Context context, boolean androidVersion) {
        mContext = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (!androidVersion) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();

        return mBluetoothAdapter;
    }

    /**
     * 確認藍芽是否開啟
     */
    public boolean checkBluetoothOpen() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 掃描BLE裝置
     *
     * @param enable 開始掃描為true，停止掃描為false
     */
    public void searchBLEDevice(final boolean enable) {
        if (enable) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    dismissProgressDialog();
                    getMessageDialog(
                            mContext.getString(R.string.auto_upload_timeout),
                            mContext.getString(R.string.auto_upload_not_found),
                            false,
                            null,
                            null
                    ).show();

                    Log.i(TAG, "TimeOut");
                }
            };
            mHandler.postDelayed(mRunnable, SCAN_PERIOD);    //30sec
            startScan();
        } else {
            //            removeHandler();
            if (mProgressDialog != null) {
                dismissProgressDialog();
            }
            stopScan();
        }
    }

    public void removeHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);    //關閉TIME OUT等待
            Log.i(TAG, "Handler Removed");
        }
    }

    /**
     * 開始BLE裝置掃瞄
     */
    public void startScan() {
        Log.i(TAG, "Scan Start");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 停止BLE裝置掃瞄
     */
    public void stopScan() {
        if ((mBluetoothAdapter != null)) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i(TAG, "Scan Stop");
        }
    }

    /**
     * 掃描BLE裝置的呼叫
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (DeviceID.equals(device.getName())) {
                if (bleDevice == null) {
                    bleDevice = device;
                    // 搜尋回饋
                    Log.i(TAG, "BLE DeviceID : " + bleDevice.getName());
                    connectBLEDevice(bleDevice);
                } else {
                    Log.e(TAG, "Another BLE DeviceID : " + device.getName());
                }
            } else {
                Log.e(TAG, "BLE DeviceID : NO DEVICE");
            }
        }
    };

    /**
     * 與指定的BLE裝置建立連接
     */
    public void connectBLEDevice(BluetoothDevice device) {
        //  true為自動連接(速度慢)，false為一次性連接(速度快)
        Log.i(TAG, "Connect to device");
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
    }

    /**
     * 連接BLE裝置的呼叫
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //連接狀態出現改變時
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) { //藍芽裝置完成連接時
                Log.i(TAG, "GATT server 連線");
                Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices());
                Log.i(TAG, "取得裝置:" + gatt.getDevice());
                macId = gatt.getDevice().toString();
                closeBT30Service(); //關閉BT3.0相關的所有Service
                removeHandler();    //關閉TIME OUT等待
                if (mProgressDialog != null) {
                    dismissProgressDialog();
                }
                loadingThread.start();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //藍芽裝置無法連接時
                Log.i(TAG, "Can't Find GATT server");
                // do nothing
            }
        }

        // 藍芽裝置中有可用的Service時
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService btGattWriteService = mBluetoothGatt.getService(Device_SERVICE_UUID);
                Log.i(TAG, "BluetoothGatt.GATT_SUCCESS");
                if (btGattWriteService != null) {
                    mCharacteristic = btGattWriteService.getCharacteristic(Device_CHARACTERISTIC_UUID);
                }
            }
            // 打開藍芽裝置的CharacteristicNotification以進行溝通
            mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
            List<BluetoothGattDescriptor> testList = mCharacteristic.getDescriptors();
            BluetoothGattDescriptor descriptor = testList.get(0);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            // 打開sqlite準備儲存資料
            mBioDataAdapter = new BioDataAdapter(mContext);
        }

        //  對藍芽裝置進行寫入完成時呼叫
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "寫入完成");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] readValue = mCharacteristic.getValue();
            Log.i(TAG, "取得藍芽裝置回傳的值:" + Arrays.toString(readValue));
            writeCharacterisricValue(readValue);
            mCharacteristic.setValue(writeValue);
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
            Log.i(TAG, "readValue : " + String.valueOf(readValue[1]));
            // 關機
            if (readValue[1] == (byte) 0x52) {
                Log.i(TAG, "關機");
                //                closeBLE();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            closeBLE();
                            autoUpLoadData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

        // 資料上傳進度條的執行緒
        Thread loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                loadingProgressDialog = new ProgressDialog(mContext);
                loadingProgressDialog.setTitle(mContext.getString(R.string.auto_upload_ble));
                loadingProgressDialog.setMessage(mContext.getString(R.string.auto_upload_uploading));
                loadingProgressDialog.setCancelable(false);
                loadingProgressDialog.show();
                Looper.loop();
            }
        });
    };

    /**
     * 轉換寫入藍芽裝置的值
     */
    private void writeCharacterisricValue(byte[] readValue) {
        // 從取得第一位user資料開始
        byte cmdByte = readValue[1];
        byte data0Byte = (byte) (dataIndex & 0xFF);
        byte data1Byte = (byte) ((dataIndex >> 8) & 0xFF);

        //  判斷被寫入的物件類型
        switch (readValue.length) {
            case 2: // 確認Chracteristic Notfication Enalbed後，要求第一位user的資料
                getUserData();
                //                Log.i(TAG, "要求第一位user的資料 回傳 0x2B ： " + Arrays.toString(writeValue));

                break;
            case 8:
                if (cmdByte == (byte) 0x52) {   // 取得user最後一筆資料後，要求關機
                    //要求裝置關機
                    deviceShutDown();

                } else if (cmdByte == (byte) 0x2B) { // 取得第一位user的資料後，要求user第n筆資料的TIME
                    userDataIndex = readValue[2] + (readValue[3] << 8);
                    getDataTimeByteArray(data0Byte, data1Byte);
                    //                    Log.i(TAG, "要求user第n筆資料的TIME 回傳0x25 ： " + Arrays.toString(writeValue));

                } else if (cmdByte == (byte) 0x25) { //取得第一位user第n筆資料的TIME後，要求第n筆資料的RESULT
                    pOrG = (readValue[4] & 0x80); //判斷為血壓或血糖
                    Log.i(TAG, "P / G : " + pOrG);
                    setDataTime(readValue);
                    getDataResultByteArray(data0Byte, data1Byte);
                    //                    Log.i(TAG, "要求第n筆資料的RESULT 回傳0x26 ： " + Arrays.toString(writeValue));

                } else if (cmdByte == (byte) 0x26) { //取得第一位user第n筆資料的RESULT後，要求第n筆資料的TIME或要求關機
                    setDataResult(readValue);
                    //是否為最後一筆資料
                    if (dataIndex < userDataIndex - 1) {
                        getUserData();
                        dataIndex++;
                        //                        Log.i(TAG, "要求第一位user的資料 回傳 0x2B ： " + Arrays.toString(writeValue));

                    } else {
                        //刪除裝置內的資料
                        deleteDeviceData();
                    }
                }
                break;
        }
    }

    /**
     * 計算CheckSum
     */
    private byte getCheckSum(byte[] writeValue) {
        int checkSum = writeValue[0];
        for (int i = 1; i < 7; i++) {
            checkSum += writeValue[i];
        }
        return (byte) checkSum;
    }

    /**
     * 取得第一位使用者資料的byte[]
     */
    private void getUserData() {
        writeValue = new byte[]{START_BYTE, (byte) 0x2B, (byte) 0x1, (byte) 0x0, (byte) 0x0, (byte) 0x0, STOP_BYTE, (byte) 0x20};
    }

    /**
     * 取得使用者該筆資料TIME的byte[]
     */
    private void getDataTimeByteArray(byte data0Byte, byte data1Byte) {
        // 要回傳的byte值
        writeValue = new byte[]{START_BYTE, (byte) 0x25, data0Byte, data1Byte, (byte) 0x0, (byte) 0x1, STOP_BYTE, (byte) 0x0};
        byte checkSumByte = getCheckSum(writeValue);
        writeValue[7] = checkSumByte;
    }

    /**
     * 設置使用者該筆資料的TIME
     *
     * @param readValue 藍芽裝置回傳的byte[]
     */
    private void setDataTime(byte[] readValue) {
        // 解析TIME
        String minute = String.valueOf((readValue[4] & 0x3f));
        if (Integer.valueOf(minute) < 10) {
            minute = "0" + minute;
        }
        String hour = String.valueOf((readValue[5] & 0x1f));
        if (Integer.valueOf(hour) < 10) {
            hour = "0" + hour;
        }
        String day = String.valueOf((readValue[2] & 0x1f)) + "";
        if (Integer.valueOf(day) < 10) {
            day = "0" + day;
        }
        String month = String.valueOf((((readValue[2] & 0xe0) >> 5) | ((readValue[3] & 0x1) << 3)));
        if (Integer.valueOf(month) < 10) {
            month = "0" + month;
        }
        String year = String.valueOf((2000 + ((readValue[3] & 0xfe) >> 1)));
        measurementDate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";

        Log.i(TAG, "Data Time : " + year + " / " + month + " / " + day + " / " + hour + " / " + minute);
    }

    /**
     * 取得使用者該筆資料RESULT的byte[]
     */
    private void getDataResultByteArray(byte data0Byte, byte data1Byte) {
        // 要回傳的byte值
        writeValue = new byte[]{START_BYTE, (byte) 0x26, data0Byte, data1Byte, (byte) 0x0, (byte) 0x1, STOP_BYTE, (byte) 0x0};
        byte checkSumByte = getCheckSum(writeValue);
        writeValue[7] = checkSumByte;
    }

    /**
     * 設置使用者該筆資料的RESULT
     *
     * @param readValue 藍芽裝置回傳的byte[]
     */
    private void setDataResult(byte[] readValue) {
        BioData mBioData = new BioData();
        UserAdapter userAdapter = new UserAdapter(mContext);
        mBioData.setUserId(userAdapter.getUID());
        mBioData.setDeviceId(DeviceID);
        mBioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
        mBioData.setDeviceTime(measurementDate);
        Log.v("macId", macId);
        macId = macId.replaceAll(":", "");
        Log.v("macId2", macId);
        mBioData.setDeviceMac(macId);

        // 解析RESULT
        if (pOrG == 0) {
            // 血糖
            int mType = (readValue[5] & 0xc0) >> 6; //類型
            String mGlucose = String.valueOf((((int) readValue[3]) << 8) + ((int) readValue[2]));  //血糖
            String mCode = String.valueOf(readValue[5] & 0x3f);
            String mTemperature = String.valueOf(readValue[4] & 0xff);
            String pType;

            switch (mType) {
                case 1:
                    pType = "AC";    //飯前
                    mBioData.setAc(mGlucose);
                    break;
                case 2:
                    pType = "PC";    //飯後
                    mBioData.setPc(mGlucose);
                    break;
                case 0:
                    pType = "NM";   // 隨機
                    mBioData.setNm(mGlucose);
                    break;
                default:
                    pType = "Gen";
                    break;
            }
            Log.i(TAG, "Data Result Glucose : " + pType + " / " + mGlucose + " / " + mCode + " / " + mTemperature);
            //存進Sqlite
            if (!checkDataTime()) {
                mBioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                mBioDataAdapter.createGlucose(mBioData);
            }
        } else {
            // 血壓
            String systolicValue = String.valueOf((readValue[2] & 0xff));   //收縮壓
            String diastolicValue = String.valueOf((readValue[4] & 0xff));   //舒張壓
            String pulseValue = String.valueOf((readValue[5] & 0xff));   //平均心跳
            Log.i(TAG, "Data Result Pressure : " + systolicValue + " / " + diastolicValue + " / " + pulseValue);

            mBioData.setPulse(String.valueOf(pulseValue));
            mBioData.setBhp(String.valueOf(systolicValue));
            mBioData.setBlp(String.valueOf(diastolicValue));
            //存進Sqlite
            if (!checkDataTime()) {
                mBioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                mBioDataAdapter.createBloodPressure(mBioData);
            }
        }
    }

    /**
     * 組合要求藍芽裝置刪除所有資料的byte[]
     */
    private void deleteDeviceData() {
        writeValue = new byte[]{START_BYTE, (byte) 0x52, (byte) 0x1, (byte) 0x0, (byte) 0x0, (byte) 0x0, STOP_BYTE, (byte) 0x0};
        byte checkSumByte = getCheckSum(writeValue);
        writeValue[7] = checkSumByte;
        Log.i(TAG, "Device Data Deleted");
    }

    /**
     * 組合要求藍芽裝置關機的byte[]
     */
    private void deviceShutDown() {
        writeValue = new byte[]{START_BYTE, (byte) 0x50, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, STOP_BYTE, (byte) 0x44};
        Log.i(TAG, "Divice shut down : " + String.valueOf(writeValue[1]));
    }

    //利用量測時間確認本機內的資料和裝置內的資料是否有重複
    private boolean checkDataTime() {
        boolean isSameTime = false;
        ArrayList<BioData> allLocalData = mBioDataAdapter.getAll();
        for (int i = 0; i < allLocalData.size(); i++) {
            if (allLocalData.get(i).getDeviceTime().equals(measurementDate)) {
                isSameTime = true;
                break;
            }
        }
        return isSameTime;
    }

    /**
     * 自動上傳取得的資料
     */
    private void autoUpLoadData() {
        Log.i(TAG, "Start upload data");
        //查詢Update = 0，即未上傳
        ArrayList<BioData> listBioData;
        listBioData = mBioDataAdapter.getUploaded();
        Log.i(TAG, "listBioData size : " + listBioData.size());
        if (listBioData.size() > 0) {
            //UserData
            //            UserAdapter userAdapter = new UserAdapter(mContext);
            //            User user = userAdapter.getUserUIdAndPassword();
            //            Log.i(TAG, "user : " + user);

            //上傳資料
            //            NetService netService = new NetService();
            //            String response = netService.CallUploadVitalSign(user, listBioData, true);
            prf = mContext.getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
            WebServiceConnection webServiceConnection = new WebServiceConnection();
            JSONObject response = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, true);
            Log.i(TAG, response.toString());
            //  關閉讀取進度條
            loadingProgressDialog.dismiss();
            try {
                if (response != null && response.getString("message").toString().equals("Success.")) {
                    // 更新sql lite資料庫
                    mBioDataAdapter.updataUploaded(listBioData);
                    Log.i(TAG, "Data upload success");
                    // 資料上傳成功
                    Looper.getMainLooper();
                    Looper.prepare();
                    getMessageDialog(
                            mContext.getString(R.string.auto_upload_ble),
                            mContext.getString(R.string.auto_upload_success),
                            false,
                            null,
                            null
                    ).show();
                    Looper.loop();
                } else {
                    Log.i(TAG, "Data upload failed");
                    // 資料上傳失敗
                    Looper.getMainLooper();
                    Looper.prepare();
                    getMessageDialog(
                            mContext.getString(R.string.auto_upload_ble),
                            mContext.getString(R.string.auto_upload_faild),
                            true,
                            //                            user,
                            null,
                            listBioData
                    ).show();
                    Looper.loop();
                    Log.i(TAG, "Data upload success");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 產生裝置搜尋進度條物件
     *
     * @param context 要產生的Activity
     * @return 產生的進度條
     */
    public ProgressDialog postSearchingProgressDialog(Context context) {
        return mProgressDialog = ProgressDialog.show(context, context.getString(R.string.auto_upload_ble), context.getString(R.string.auto_upload_device_searching), false);
    }

    /**
     * 關閉裝置搜尋進度條
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 通用的訊息顯示AlertDialog
     *
     * @param title   標題
     * @param message 內文訊息
     * @return 實作MessageDialog
     */
    public AlertDialog getMessageDialog(String title, String message, final boolean isUploadFailed,
                                        final User user, final ArrayList<BioData> listBioData) {
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //設定Dialog的標題
        //        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                if (isUploadFailed) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // NetService netService = new NetService();
                                prf = mContext.getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
                                WebServiceConnection webServiceConnection = new WebServiceConnection();
                                while (true) {
                                    Log.i(TAG, "Re - upload data");
                                    // String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, true);
                                    JSONObject reUpdateResponse = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, true);
                                    if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                        // 更新sql lite資料庫
                                        mBioDataAdapter.updataUploaded(listBioData);
                                        Log.i(TAG, "資料重新上傳成功");
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }

    /**
     * 關閉BLE連線
     */
    private void closeBLE() {
        stopScan();
        //  初始化資料筆數
        dataIndex = 0;
        //  關閉讀取視窗
        if (mProgressDialog != null) {
            dismissProgressDialog();
        }
        //關閉GATT連接
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            Log.i(TAG, "GATT server 斷線");
        }
        //  關閉Device連接
        if (bleDevice != null) {
            bleDevice = null;
        }
        //  關閉BT3.0相關的所有Service
        closeBT30Service();
        //  釋放BluetoothAdapter連接
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
            Log.i(TAG, "mBluetoothAdapter = null");
        }
    }

    /**
     * 關閉BT3.0相關的所有Service
     */
    public void closeBT30Service() {
        //  關閉BT3.0的Service
        Intent btServiceIntent = new Intent(mContext, GetBlueToothDeviceDataService.class);
        mContext.stopService(btServiceIntent);
        //  關閉解析BT3.0的Service
        Intent MainServiceIntent = new Intent(mContext, MainService.class);
        mContext.stopService(MainServiceIntent);
    }
}
