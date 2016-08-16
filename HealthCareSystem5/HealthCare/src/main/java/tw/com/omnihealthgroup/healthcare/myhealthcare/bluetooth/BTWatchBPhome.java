package tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.GetBlueToothDeviceDataService;


public class BTWatchBPhome extends Object {

    private static final String TAG = "BTWatchBPhome";
    private static final boolean D = true;

    public final static int DEVICE_TYPE_TAIDOC_UNKNOWN = 0;
    public final static int DEVICE_TYPE_TAIDOC_BPG = 1;
    public final static int DEVICE_TYPE_TAIDOC_BWS = 2;
    public final static int DEVICE_TYPE_AUTOCODE = 3;

    private int mDevType = DEVICE_TYPE_TAIDOC_UNKNOWN;

    private static final int REQUEST_READ_DEVICE_DATA = 4;

    private static final int TAIDOC_MAX_DATA_SZ = 360;

    public static final int READ_MESSAGE_TIMEOUT_SEC = 10;
    public static final int BHDC_REC_SZ = 1;

    //final byte CMD_CODE_DEV_NOTIFY = 0x54;
    //==================百略=================================
    final byte COMMAND_FIRST_CODE = (byte) 0x12;
    final byte COMMAND_SECOND_CODE = (byte) 0x16;
    final byte COMMAND_THIRD_CODE = (byte) 0x18;

    final byte DEVICE_START_CODE = (byte) 0xFF; //WatchBP Home
    final byte DEVICE_BGP_CODE = (byte) 0x4A; //WatchBP Home
    final byte DEVICE_BP_CODE = (byte) 0x4C; //WatchBP Home

    final byte CMD_CODE_READ_FOR_TIME_OF_DEVICE = 0x26;
    final byte CMD_CODE_SET_THE_TIME_OF_DEVICE = 0x27;
    final byte CMD_CODE_SENT_USUAL_MODE_MEMORY = 0x28;
    final byte CMD_CODE_SENT_DIAGNOSTIC_MODE_MEMORY = 0x29;
    final byte CMD_CODE_DISCONNECT = 0x20;

    //=======================================================
     /*
      * Command Format
 	 * Header, Device, Length_Low, Length_High, CMD, Checksum
 	 * 
 	 */
    //================================百略============================================================
    final byte CMD_REQUEST_READ_FOR_TIME_OF_DEVICE[] = {COMMAND_FIRST_CODE, COMMAND_SECOND_CODE, COMMAND_THIRD_CODE, CMD_CODE_READ_FOR_TIME_OF_DEVICE};
    final byte CMD_REQUEST_SET_THE_TIME_OF_DEVICE[] = {COMMAND_FIRST_CODE, COMMAND_SECOND_CODE, COMMAND_THIRD_CODE, CMD_CODE_SET_THE_TIME_OF_DEVICE};
    final byte CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final byte CMD_REQUEST_SENT_USUAL_MODE_MEMORY[] = {COMMAND_FIRST_CODE, COMMAND_SECOND_CODE, COMMAND_THIRD_CODE, CMD_CODE_SENT_USUAL_MODE_MEMORY};
    final byte CMD_REQUEST_SENT_DIAGNOSTIC_MODE_MEMORY[] = {COMMAND_FIRST_CODE, COMMAND_SECOND_CODE, COMMAND_THIRD_CODE, CMD_CODE_SENT_DIAGNOSTIC_MODE_MEMORY};
    final byte CMD_REQUEST_DISCONNECT[] = {COMMAND_FIRST_CODE, COMMAND_SECOND_CODE, COMMAND_THIRD_CODE, CMD_CODE_DISCONNECT};
    final byte CMD_RESPONSE_GET_DATA[] = {0x4D, (byte) 0xFF, 0x02, 0x00, 0x04, 0x52};


    private final static String recordUnit = null;
    //================================================================================================

    private final static int STATE_REQUEST_ISSUED = 1;
    private final static int STATE_REQUEST_FINISHED = 0;

    private int mRequestState = STATE_REQUEST_FINISHED;

    // Local Bluetooth adapter
    // Member object for the chat services
    private BluetoothChatService mBTWBPHService = null;
    private int mInitResponse = 0;

    //final BloodGlucoseActivity mMainUi;
    final GetBlueToothDeviceDataService mMainUi;
    private Handler mHandler;
    public TaidocCmdThread mCmdThread;

    private BioDataAdapter bioDataAdapter;


    public class BHDevInfo {
        private String mName;
        private String mModel;
        private String mBtMac;
        private String mBaudRate;
        private int mType;

        public void setName(String nm) {
            mName = nm;
        }

        public void setModel(String md) {
            mModel = md;
        }

        public void setBtMac(String mac) {
            mBtMac = mac;
        }

        public void setBaudRate(String br) {
            mBaudRate = br;
        }

        public void setType(int t) {
            mType = t;
        }

        public String getName() {
            return mName;
        }

        public String getModel() {
            return mModel;
        }

        public String getBtMac() {
            return mBtMac;
        }

        public String getBaudRate() {
            return mBaudRate;
        }

        public int getType() {
            return mType;
        }
    }

    //public BTAsVoice2GlucoSure(BloodGlucoseActivity mainui,  Handler handler, BTAsVoice2GSConnService service)
    public BTWatchBPhome(GetBlueToothDeviceDataService mainui, Handler handler, BluetoothChatService service) {
        mMainUi = mainui;
        mBTWBPHService = service;
        mHandler = handler;
        mDevType = DEVICE_TYPE_AUTOCODE;
    }

    public void startCmdThread(int cmd) {
        mCmdThread = null;
        mCmdThread = new TaidocCmdThread(mDevType);
        mCmdThread.setCmd(cmd);
        mCmdThread.start();
    }

    //百略
    public void setDevName(String devName) {
        Log.i(TAG, "setDevName, devName:" + devName);
    }

    public int startBHDCmdService(String addr) {
        mDevType = checkDevType(addr);
        if (mDevType == DEVICE_TYPE_AUTOCODE) {
            Log.i(TAG, "startCmdThread WAIT_PUSH_DATA");
            startCmdThread(WAIT_PUSH_DATA);
        }
        return mDevType;
    }

    public int checkDevType(String addr) {
        return DEVICE_TYPE_AUTOCODE;
    }

    public void setDeviceType(int devtype) {
        mDevType = devtype;
    }

    /***********************************************************
     * Device Proprietary Command Processing Section
     ***********************************************************/
    void ebti_toast_msg(String inmsg) {
        Message msg = mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(GetBlueToothDeviceDataService.TOAST, inmsg);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private boolean ebti_write_bdc_dev(byte[] message) {
        Log.i(TAG, "ebti_write_bdc_dev () ");
        // Check that we're actually connected before trying anything
        if (mBTWBPHService.getState() != BluetoothChatService.STATE_CONNECTED) {
            ebti_toast_msg("藍芽尚未連線");
            return false;
        }
        String printMessage = "";
        for (int i = 0; i < message.length; i++) {
            printMessage += Integer.toHexString(message[i]) + " ";
        }
        Log.i(TAG, " OUT message: " + printMessage);
        //==============Check sum==============================
        /*
        int cmdsz1 = message.length - 1;
		Log.i(TAG, "cmdsz1:"+cmdsz1+", message.length:"+message.length);
        message[cmdsz1] = 0;
        int i;
        for (i=1;i<cmdsz1;i++){
        	message[cmdsz1] += message[i];
        }
        Log.i(TAG, "check sum ="+message[cmdsz1]);
        */
        //===================Check sum end =====================
        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BHDCService to write
            mBTWBPHService.write(message);
        }
        return true;
    }

    private byte[] ebti_read_bcd_dev_blocked(int recsz, int isDiag) {
        Log.i(TAG, "ebti_read_bcd_dev_blocked () ");
        byte[] inbuf;
        int inbuflen;
        byte[] recbuf;//new byte[1785];
        int bufctr = 0;
        int i;
        int nullctr = 0;
        String recbufString = "";
        while (nullctr < READ_RETRY_LIMIT) {
            while (bufctr < recsz) {
                try {
                    Log.i(TAG, "start to wait for mSynchMsgQueue");
                    inbuf = mMainUi.mSynchMsgQueue.poll(READ_MESSAGE_TIMEOUT_SEC, TimeUnit.SECONDS);
                    Log.i(TAG, "mSynchMsgQueue is woken up!!!!");
                } catch (InterruptedException ex) {
                    ebti_toast_msg(" 中斷讀取資料  ");
                    Log.i(TAG, "WatchBPhome read-dev interrupted ");
                    return null;
                }

                String returnByteStr = "";
                if (inbuf == null) {
                    ebti_toast_msg(" 讀取資料  回覆逾時  ");
                    Log.i(TAG, "WatchBPhome dev reply time out");
                    break;
                }

                for (int j = 0; j < inbuf.length; j++) {
                    returnByteStr += ", " + Integer.toHexString(inbuf[j]);
                }
                recbuf = new byte[inbuf.length - 1];
                Log.i(TAG, "length : " + inbuf.length + ", returnByteStr :" + returnByteStr);

                //========================
                Log.i(TAG, "isDiag : " + Integer.toHexString(inbuf[0]));
                //收到的資料長度
                inbuflen = inbuf.length - 1;
                Log.i(TAG, "inbuflen =" + inbuflen);

                for (i = 0; i < inbuflen; i++) {
                    recbuf[bufctr + i] = inbuf[i + 1];
                    recbufString += Integer.toHexString(inbuf[i + 1]) + ", ";
                }
                Log.i(TAG, " before bufctr += , bufctr : " + bufctr);
                bufctr += inbuflen;
                Log.i(TAG, " after bufctr += , bufctr : " + bufctr);
                Log.i(TAG, "recsz : " + recsz + ", bufctr : " + bufctr);
                //recsz 預估收到的資料長度
                if (bufctr >= recsz) {
                    // construct a string from the valid bytes in the buffer
                    Log.i(TAG, "bufctr>=recsz, recbufString : " + recbufString);
                    return recbuf;
                }
            }
            nullctr++;
        }
        return null;
    }

    private byte[] ebti_read_bcd_dev_blocked_push_data(int recsz, int isDiag) {
        Log.i(TAG, "ebti_read_bcd_dev_blocked_push_data () ");

        byte[] inbuf;
        int inbuflen;
        byte[] recbuf;//new byte[1785];
        int bufctr = 0;
        int i;
        int nullctr = 0;
        String recbufString = "";
        while (nullctr < READ_RETRY_LIMIT) {
            while (bufctr < recsz) {
                try {
                    Log.i(TAG, "start to wait for mSynchMsgQueue");
                    inbuf = mMainUi.mSynchMsgQueue.poll(READ_MESSAGE_TIMEOUT_SEC, TimeUnit.SECONDS);
                    Log.i(TAG, "mSynchMsgQueue is woken up!!!!");
                } catch (InterruptedException ex) {
                    ebti_toast_msg(" 中斷讀取資料  ");
                    Log.i(TAG, "WatchBPhome read-dev interrupted ");
                    return null;
                }
                String returnByteStr = "";
                if (inbuf == null) {
                    ebti_toast_msg(" 讀取資料  回覆逾時  ");
                    Log.i(TAG, "WatchBPhome dev reply time out");
                    break;
                }

                for (int j = 0; j < inbuf.length; j++) {
                    returnByteStr += ", " + Integer.toHexString(inbuf[j]);
                }
                recbuf = new byte[inbuf.length];
                Log.i(TAG, "length : " + inbuf.length + ", returnByteStr :" + returnByteStr);

                //========================
                //收到的資料長度
                inbuflen = inbuf.length;
                Log.i(TAG, "inbuflen =" + inbuflen);
                for (i = 0; i < inbuflen; i++) {
                    recbuf[bufctr + i] = inbuf[i];
                    recbufString += Integer.toHexString(inbuf[i]) + ", ";
                }
                Log.i(TAG, " before bufctr += , bufctr : " + bufctr);
                bufctr += inbuflen;
                Log.i(TAG, " after bufctr += , bufctr : " + bufctr);
                Log.i(TAG, "recsz : " + recsz + ", bufctr : " + bufctr);
                //recsz 預估收到的資料長度
                //recsz = 3 + (inbuf[3] & 0xFF) + ((inbuf[4] & 0xFF) << 2);
                Log.i(TAG, "LowData length : " + recsz);
                if (bufctr >= recsz) {
                    // construct a string from the valid bytes in the buffer
                    Log.i(TAG, "bufctr>=recsz, recbufString : " + recbufString);
                    return recbuf;
                }
            }
            nullctr++;
        }
        return null;
    }

    private static short ebti_bytes_2_short(byte lob, byte hib) {
        Log.i(TAG, "ebti_bytes_2_short(): [hib,lob]= [" + hib + "<<8, " + lob + "]");
        // assume Big Endian is used
        short his = (short) hib;
        short los = (short) lob;
        his = (short) (his & 0x00ff);
        los = (short) (los & 0x00ff);
        return (short) ((his << 8) + los);
    }

    final static int READ_RETRY_LIMIT = 3;

    //有送cmd
    private byte[] ebti_request_dev_rec(byte[] cmd, int recsz, int isDiag) {
        byte[] recbuf = null;

        Log.i(TAG, "ebti_request_dev_rec() ");

        if (cmd != null) {
            if (!ebti_write_bdc_dev(cmd)) {
                Log.e(TAG, "writing cmd to BT dev failed");
                return null;
            }
        }
        if (recsz > 0) {
            Log.i(TAG, "recsz > 0, recbuf = ebti_read_bcd_dev_blocked (recsz) , recsz:" + recsz);
            recbuf = ebti_read_bcd_dev_blocked(recsz, isDiag);
        }
        return recbuf;
    }

    //無送cmd
    private byte[] ebti_wait_device_push_data(int recsz, int isDiag) {
        byte[] recbuf = null;
        Log.i(TAG, "ebti_wait_device_push_data() ");

        if (recsz > 0) {
            Log.i(TAG, "recsz > 0, recbuf = ebti_wait_device_push_data (recsz) , recsz:" + recsz);
            recbuf = ebti_read_bcd_dev_blocked_push_data(recsz, isDiag);
        }
        return recbuf;
    }

    public boolean ebti_watchbphome_measuremode_checksum(byte[] data) {
        Log.i(TAG, "ebti_watchbphome_measuremode_checksum()");
        int dataLength = data.length;
        Log.i(TAG, "dataLength : " + dataLength);
        int checksum = 0;
        //扣除ACK
        for (int i = 4; i < dataLength; i++) {
            checksum += Integer.valueOf(String.valueOf(data[i]));
        }
        Log.i(TAG, "checksum : " + Integer.toHexString(checksum) + ", datacheckSum : " + Integer.toHexString(data[(dataLength - 1)]));
        return true;
    }

    //百略的Measurement flow Usual mode
    private void measurement_usual_flow(byte[] requestFromBP) {
        Log.i(TAG, "measurement_usual_flow : " + requestFromBP.length);
        String getLowData = "";
        //Check sum
        byte checkSum = 0;
        for (int i = 0; i < requestFromBP.length - 1; i++) {
            if (i < (requestFromBP.length - 2)) {
                checkSum += requestFromBP[i];
            }
            getLowData += Integer.toHexString(requestFromBP[i]) + ",";
        }
        int dataLength = requestFromBP.length - 2;
        Log.i(TAG, "getLowData : " + getLowData + ", dataLength : " + dataLength + ", requestFromBP[" + dataLength + "] : " + Integer.toHexString(requestFromBP[dataLength]));
        bioDataAdapter = new BioDataAdapter(mMainUi);
        if (checkSumMethod(checkSum, requestFromBP[dataLength])) {
            if (requestFromBP[13] == 0x01) {
                Log.e(TAG, "DATA ERROR 1");
                return;
            } else if (requestFromBP[13] == 0x02) {
                Log.e(TAG, "DATA ERROR 2");
                return;
            } else if (requestFromBP[13] == 0x03) {
                Log.e(TAG, "DATA ERROR 3");
                return;
            } else if (requestFromBP[13] == 0x05) {
                Log.e(TAG, "DATA ERROR 5");
                return;
            } else if (requestFromBP[13] == 0x07) {
                Log.e(TAG, "DATA ERROR 7");
                return;
            } else if (requestFromBP[13] == 0x09) {
                Log.e(TAG, "DATA ERROR 9");
                return;
            } else if (requestFromBP[13] == 0x42) {
                Log.e(TAG, "DATA ERROR 42");
                return;
            }
            mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_CHANGE_TEXT, "").sendToTarget();
            String bpHigh = Integer.toString((requestFromBP[11] & 0xFF));
            String bpLow = Integer.toString((requestFromBP[12] & 0xFF));
            String bpPulse = Integer.toString((requestFromBP[13] & 0xFF));
            String year = Integer.toString(((requestFromBP[14] >> 4) & 0x0F) + 2000);//西元年yy
            String month = Integer.toString(requestFromBP[14] & 0x0F);//月
            String day = Integer.toString((requestFromBP[15] >> 3) & 0x1F);//日
            String hour = Integer.toString(((requestFromBP[15] << 2) & 0x1C) + ((requestFromBP[16] >> 6) & 0x03));//時
            String minute = Integer.toString(requestFromBP[16] & 0x3F);//分
            String recordDate = year + "-" + (month.length() > 1 ? month : "0" + month) + "-" + (day.length() > 1 ? day : "0" + day) + " " + (hour.length() > 1 ? hour : "0" + hour) + ":" + (minute.length() > 1 ? minute : "0" + minute) + ":00";
            Log.i(TAG, recordDate + ", 收縮壓:" + bpHigh + ", 舒張壓:" + bpLow + ", 脈搏:" + bpPulse);
            //將資料存進SQLite
            UserAdapter userAdapter = new UserAdapter(mMainUi);
            String uID = userAdapter.getUID();
            BioData bioData = new BioData();
            bioData.set_id(uID + recordDate + "WatchBPHome");
            bioData.setUserId(uID);
            bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
            bioData.setDeviceId("WatchBPHome");
            bioData.setDeviceTime(recordDate);
            bioData.setBhp(bpHigh);
            bioData.setBlp(bpLow);
            bioData.setPulse(bpPulse);
            bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
            bioDataAdapter.createBloodPressure(bioData);
            //歷史資料
            int historyDataLength = (requestFromBP.length - 32) / 7;
            Log.i(TAG, "historyDataLength : " + historyDataLength);
            for (int i = 0; i < historyDataLength; i++) {
                String bpHighHis = Integer.toString((requestFromBP[32 + i * 7] & 0xFF));
                String bpLowHis = Integer.toString((requestFromBP[32 + i * 7 + 1] & 0xFF));
                String bpPulseHis = Integer.toString((requestFromBP[32 + i * 7 + 2] & 0xFF));
                String yearHis = Integer.toString(((requestFromBP[32 + i * 7 + 3] >> 4) & 0x0F) + 2000);//西元年yy
                String monthHis = Integer.toString(requestFromBP[32 + i * 7 + 3] & 0x0F);//月
                String dayHis = Integer.toString((requestFromBP[32 + i * 7 + 4] >> 3) & 0x1F);//日
                String hourHis = Integer.toString(((requestFromBP[32 + i * 7 + 4] << 2) & 0x1C) + ((requestFromBP[32 + i * 7 + 5] >> 6) & 0x03));//時
                String minuteHis = Integer.toString(requestFromBP[32 + i * 7 + 5] & 0x3F);//分
                String recordDateHis = yearHis + "-" + (monthHis.length() > 1 ? monthHis : "0" + monthHis) + "-" + (dayHis.length() > 1 ? dayHis : "0" + dayHis) + " " + (hourHis.length() > 1 ? hourHis : "0" + hourHis) + ":" + (minuteHis.length() > 1 ? minuteHis : "0" + minuteHis) + ":00";
                Log.i(TAG, recordDateHis + ", 收縮壓:" + bpHighHis + ", 舒張壓:" + bpLowHis + ", 脈搏:" + bpPulseHis);

                //將資料存進SQLite
                BioData bioDataHis = new BioData();
                bioDataHis.set_id(uID + recordDateHis + "WatchBPHome");
                bioDataHis.setUserId(uID);
                bioDataHis.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                bioDataHis.setDeviceId("WatchBPHome");
                bioDataHis.setDeviceTime(recordDateHis);
                bioDataHis.setBhp(bpHighHis);
                bioDataHis.setBlp(bpLowHis);
                bioDataHis.setPulse(bpPulseHis);
                bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBloodPressure(bioDataHis);
            }
        }
    }

    //百略的Measurement flow diag mode
    private void measurement_diag_flow(byte[] requestFromBP) {
        Log.i(TAG, "measurement_diag_flow : " + requestFromBP.length);
        //Check sum
        byte checkSum = 0;
        for (int i = 0; i < requestFromBP.length - 1; i++) {
            checkSum += requestFromBP[i];
        }
        if (checkSumMethod(checkSum, requestFromBP[requestFromBP.length - 2])) {
            mBTWBPHService.write(CMD_RESPONSE_GET_DATA);
            mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_CHANGE_TEXT, "").sendToTarget();
            UserAdapter userAdapter = new UserAdapter(mMainUi);
            String uID = userAdapter.getUID();
            bioDataAdapter = new BioDataAdapter(mMainUi);
            for (int i = 0; i < 2; i++) {
                if (requestFromBP[13 + i * 7] == 0x01) {
                    Log.e(TAG, "DATA ERROR 1");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x02) {
                    Log.e(TAG, "DATA ERROR 2");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x03) {
                    Log.e(TAG, "DATA ERROR 3");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x05) {
                    Log.e(TAG, "DATA ERROR 5");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x07) {
                    Log.e(TAG, "DATA ERROR 7");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x09) {
                    Log.e(TAG, "DATA ERROR 9");
                    continue;
                } else if (requestFromBP[13 + i * 7] == 0x42) {
                    Log.e(TAG, "DATA ERROR 42");
                    continue;
                }
                String bpHigh = Integer.toString((requestFromBP[11 + i * 7] & 0xFF));
                String bpLow = Integer.toString((requestFromBP[12 + i * 7] & 0xFF));
                String bpPulse = Integer.toString((requestFromBP[13 + i * 7] & 0xFF));
                String year = Integer.toString(((requestFromBP[14 + i * 7] >> 4) & 0x0F) + 2000);//西元年yy
                String month = Integer.toString(requestFromBP[14 + i * 7] & 0x0F);//月
                String day = Integer.toString((requestFromBP[15 + i * 7] >> 3) & 0x1F);//日
                String hour = Integer.toString(((requestFromBP[15 + i * 7] << 2) & 0x1C) + ((requestFromBP[16 + i * 7] >> 6) & 0x03));//時
                String minute = Integer.toString(requestFromBP[16 + i * 7] & 0x3F);//分
                String recordDate = year + "-" + (month.length() > 1 ? month : "0" + month) + "-" + (day.length() > 1 ? day : "0" + day) + " " + (hour.length() > 1 ? hour : "0" + hour) + ":" + (minute.length() > 1 ? minute : "0" + minute) + ":00";
                Log.i(TAG, recordDate + ", 收縮壓:" + bpHigh + ", 舒張壓:" + bpLow + ", 脈搏:" + bpPulse);

                //將資料存進SQLite
                BioData bioData = new BioData();
                bioData.set_id(uID + recordDate + "WatchBPHome");
                bioData.setUserId(uID);
                bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                bioData.setDeviceId("WatchBPHome");
                bioData.setDeviceTime(recordDate);
                bioData.setBhp(bpHigh);
                bioData.setBlp(bpLow);
                bioData.setPulse(bpPulse);
                bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBloodPressure(bioData);
            }
        }
    }

    private String ebti_bcd_byte_to_str(byte b) {
        Log.i(TAG, "ebti_bcd_byte_to_str(byte b)");
        String str = "";
        int i;

        i = ((b >> 4) & 0x0f);
        if (i < 10)
            str = str + i;
        else
            str = str + ((char) (i + 55));
        i = (b & 0x0f);
        if (i < 10)
            str = str + i;
        else
            str = str + ((char) (i + 55));
        Log.i(TAG, "b=" + b + "   str=" + str);
        return str;
    }

    //百略, 讀取設備時間
    public String ebti_request_read_the_device_time() {
        String snstr = "";
        byte[] recbuf;

        Log.i(TAG, "ebti_request_read_the_device_time()");
        // the read the second Doubleword first
        recbuf = ebti_request_dev_rec(CMD_REQUEST_READ_FOR_TIME_OF_DEVICE, 16, 0);
        if (recbuf == null) {
            return snstr;
        }

        Log.i(TAG, "ebti_request_read_the_device_time():recbuf[1](OP Code)=" + Integer.toString(recbuf[0], 16));

        if (recbuf[0] != 6) {
            Log.i(TAG, "ACK is not 6");
        }
        byte[] returnData = new byte[17];
        for (int j = 0; j < 17; j++) {
            returnData[j] = recbuf[j];
        }
        ebti_watchbphome_timedata_checksum(returnData);
        //取得日期資料
        snstr = ebti_byte_to_ascii(recbuf[7]) + ebti_byte_to_ascii(recbuf[8]) + ebti_byte_to_ascii(recbuf[5]) + ebti_byte_to_ascii(recbuf[6]) + "-"
                + ebti_byte_to_ascii(recbuf[1]) + ebti_byte_to_ascii(recbuf[2]) + "-"
                + ebti_byte_to_ascii(recbuf[3]) + ebti_byte_to_ascii(recbuf[4]) + " "
                + ebti_byte_to_ascii(recbuf[9]) + ebti_byte_to_ascii(recbuf[10]) + ":"
                + ebti_byte_to_ascii(recbuf[11]) + ebti_byte_to_ascii(recbuf[12]) + ":"
                + ebti_byte_to_ascii(recbuf[13]) + ebti_byte_to_ascii(recbuf[14]);
        Log.i(TAG, "  snstr: = " + snstr);
        return snstr;
    }

    //百略, 等待Push資料
    public String ebti_wait_to_read_datas() {
        String snstr = "";
        byte[] recbuf;

        Log.i(TAG, "ebti_wait_to_read_datas()");
        // the read the second Doubleword first
        recbuf = ebti_wait_device_push_data(32, 0);
        if (recbuf == null) {
            Log.i(TAG, "ebti_wait_to_read_datas responce == null");
            return snstr;
        }

        Log.i(TAG, "ebti_wait_to_read_datas():recbuf[1](OP Code)=" + Integer.toString(recbuf[0], 16));
        snstr = "";

        //百略的measure Mode
        if (recbuf[0] == 0x4d && recbuf.length > 18) {
            Log.i(TAG, "百略的Measurement flow");
            if (recbuf[4] == 0) {
                measurement_usual_flow(recbuf);
            } else if (recbuf[4] == 1) {
                measurement_diag_flow(recbuf);
            }

        }
        return snstr;
    }

    public boolean ebti_watchbphome_timedata_checksum(byte[] data) {
        Log.i(TAG, "ebti_watchbphome_timedata_checksum()");
        int dataLength = data.length;
        Log.i(TAG, "dataLength : " + dataLength);
        int checksum = 0;
        //扣除ACK
        for (int i = 1; i < (dataLength - 2); i++) {
            checksum += Integer.valueOf(String.valueOf(data[i]));
        }
        Log.i(TAG, "checksum : " + Integer.toString(checksum, 16) + ", datacheckSum : " + Integer.toString(data[(dataLength - 1)], 16) + " " + Integer.toString(data[(dataLength - 2)], 16));
        return true;
    }

    //百略 設定時間起始
    public String ebti_set_the_time_of_device() {
        String snstr = "";
        byte[] recbuf;

        Log.i(TAG, "ebti_set_the_time_of_device()");
        // the read the second Doubleword first
        //因回應只有ACK(0x06) 故ebti_request_dev_rec(cmd, recsz)的recsz給1即可
        //recsz 為預估收到的資料長度
        recbuf = ebti_request_dev_rec(CMD_REQUEST_SET_THE_TIME_OF_DEVICE, 1, 0);
        if (recbuf == null) {
            return snstr;
        }
        snstr = String.valueOf(recbuf[0]);
        Log.i(TAG, "snstr : " + snstr);

        return snstr;
    }

    //百略 傳送時間資料
    public String ebti_set_the_time_of_device_sent_data() {
        String snstr = "";
        byte[] recbuf;

        Log.i(TAG, "ebti_set_the_time_of_device_sent_data()");
        // the read the second Doubleword first
        //資料設定
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datetimeString = sdf.format(new Date());
        Log.i(TAG, "datetimeString : " + datetimeString);
        byte[] byteTime = datetimeString.getBytes();
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[0] = byteTime[4];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[1] = byteTime[5];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[2] = byteTime[6];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[3] = byteTime[7];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[4] = byteTime[2];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[5] = byteTime[3];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[6] = byteTime[0];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[7] = byteTime[1];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[8] = byteTime[8];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[9] = byteTime[9];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[10] = byteTime[10];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[11] = byteTime[11];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[12] = byteTime[12];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[13] = byteTime[13];

        String str = "";
        int checksum = 0;
        for (int i = 0; i < 14; i++) {
            str += Integer.toHexString(CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[i]) + " ";
            checksum += CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[i];
        }
        Log.i(TAG, "str : " + str);
        Log.i(TAG, "checksum : " + Integer.toHexString(checksum));
        String timeSumString = Integer.toHexString(checksum);
        timeSumString = timeSumString.toUpperCase();
        byte[] timeSumByte = timeSumString.getBytes();
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[14] = timeSumByte[timeSumByte.length - 1];
        CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[15] = timeSumByte[timeSumByte.length - 2];

        String strCheck = "";
        for (int i = 0; i < 16; i++) {
            strCheck += Integer.toHexString(CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA[i]) + " ";
        }
        Log.i(TAG, "strCheck : " + strCheck);
        //因回應只有ACK(0x06) 故ebti_request_dev_rec(cmd, recsz)的recsz給1即可
        //recsz 為預估收到的資料長度
        recbuf = ebti_request_dev_rec(CMD_REQUEST_SET_THE_TIME_OF_DEVICE_TIME_DATA, 1, 0);
        if (recbuf == null) {
            Log.i(TAG, "recbuf == null");
            return snstr;
        }
        snstr = String.valueOf(recbuf[0]);
        Log.i(TAG, "ebti_set_the_time_of_device_sent_data(), snstr : " + snstr);
        return snstr;
    }

    /*
     *  百略 讀取資料
     */
    public synchronized String ebti_sent_usual_mode_memory() {
        Log.i(TAG, "ebti_sent_usual_mode_memory()");
        byte[] recbuf;

        try {
            // the read the second Doubleword first
            //因回應只有ACK(0x06) 故ebti_request_dev_rec(cmd, recsz)的recsz給1即可
            //recsz 為預估收到的資料長度
            recbuf = ebti_request_dev_rec(CMD_REQUEST_SENT_USUAL_MODE_MEMORY, 23, 0);
            if (recbuf == null) {
                Log.i(TAG, "recbuf == null");
                return "false";
            }
            //CheckSum
            Log.i(TAG, "recbuf : " + recbuf.length);
            if (Integer.toHexString(recbuf[0]).equals("6")) {
                Log.i(TAG, "ACT = 6");
                int doCheckSum = 0;
                //recbuf[0]為Ack 0x06
                byte[] data = new byte[recbuf.length - 2];//無ACK & checksum data的資料
                for (int i = 1; i < recbuf.length - 1; i++) {
                    doCheckSum += recbuf[i];
                    data[i - 1] = recbuf[i];
                }
                Log.i(TAG, "data length : " + data.length);
                byte checkSum = (byte) doCheckSum;
                boolean checksumIsOK = checkSumMethod(checkSum, recbuf[recbuf.length - 1]);
                if (checksumIsOK) {
                    int dataN = 0;
                    if (Integer.toHexString(data[6]).equals("0")) {
                        Log.i(TAG, "data[0] : " + Integer.toString(data[0]));
                        dataN = data[0] - 2;
                        Log.i(TAG, "data[6] : " + data[6] + ", dataN : " + dataN);
                    } else {
                        dataN = 249;
                        Log.i(TAG, "data[6] : " + data[6] + ", dataN : " + dataN);
                    }
                    Log.i(TAG, "before for");
                    UserAdapter userAdapter = new UserAdapter(mMainUi);
                    String uID = userAdapter.getUID();
                    bioDataAdapter = new BioDataAdapter(mMainUi);
                    for (int i = 1; i <= (dataN + 1); i++) {
                        Log.i(TAG, Integer.toString((data[i * 7] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 1] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 2] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 3] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 4] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 5] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 6] & 0xFF)));
                        String bpHigh = Integer.toString((data[i * 7] & 0xFF));
                        String bpLow = Integer.toString((data[(i * 7) + 1] & 0xFF));
                        String bpPulse = Integer.toString((data[(i * 7) + 2] & 0xFF));
                        String year = Integer.toString((((data[(i * 7) + 3]) >> 4) & 0x0F) + 2000);//西元年yy
                        String month = Integer.toString(data[(i * 7) + 3] & 0x0F);//月
                        String day = Integer.toString(((data[(i * 7) + 4]) >> 3) & 0x1F);//日
                        String hour = Integer.toString(((((data[(i * 7) + 4]) << 2) & 0x1C) + ((data[(i * 7) + 5] >> 6) & 0x03)));//時
                        String minute = Integer.toString(data[(i * 7) + 5] & 0x3F);//分
                        String recordDate = year + "-" + (month.length() > 1 ? month : "0" + month) + "-" + (day.length() > 1 ? day : "0" + day) + " " + (hour.length() > 1 ? hour : "0" + hour) + ":" + (minute.length() > 1 ? minute : "0" + minute) + ":00";
                        Log.i(TAG, recordDate + ", 收縮壓:" + bpHigh + ", 舒張壓:" + bpLow + ", 脈搏:" + bpPulse);

                        //將資料存進SQLite
                        BioData bioData = new BioData();
                        bioData.set_id(uID + recordDate + "WatchBPHome");
                        bioData.setUserId(uID);
                        bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                        bioData.setDeviceId("WatchBPHome");
                        bioData.setDeviceTime(recordDate);
                        bioData.setBhp(bpHigh);
                        bioData.setBlp(bpLow);
                        bioData.setPulse(bpPulse);
                        bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                        bioDataAdapter.createBloodPressure(bioData);
                    }
                }
            }
            String responseStr = "";
            for (int i = 0; i < recbuf.length; i++) {
                responseStr += Integer.toString(recbuf[i], 16) + ", ";
            }
            Log.i(TAG, "responseStr : " + responseStr);
        } catch (Exception e) {
            Log.i(TAG, "Exception : " + e);
            return "false";
        }
        return "true";
    }

    //診斷模式
    public synchronized String ebti_sent_diagnostic_mode_memory() {
        Log.i(TAG, "ebti_sent_diagnostic_mode_memory()");
        byte[] recbuf;

        try {
            // the read the second Doubleword first
            //因回應只有ACK(0x06) 故ebti_request_dev_rec(cmd, recsz)的recsz給1即可
            //recsz 為預估收到的資料長度
            recbuf = ebti_request_dev_rec(CMD_REQUEST_SENT_DIAGNOSTIC_MODE_MEMORY, 421, 1);
            if (recbuf == null) {
                Log.i(TAG, "recbuf == null");
                return "false";
            }
            String responseStr = "";
            for (int i = 0; i < recbuf.length; i++) {
                responseStr += Integer.toHexString(recbuf[i]) + ", ";
            }
            Log.i(TAG, "responseStr : " + responseStr);
            //CheckSum
            Log.i(TAG, "recbuf : " + recbuf.length);
            if (Integer.toHexString(recbuf[0]).equals("6") && recbuf.length > 1) {
                Log.i(TAG, "ACT = 6");
                int doCheckSum = 0;
                //recbuf[0]為Ack 0x06
                byte[] data = new byte[recbuf.length - 2];//無ACK & checksum data的資料
                for (int i = 1; i < recbuf.length - 1; i++) {
                    doCheckSum += recbuf[i];
                    data[i - 1] = recbuf[i];
                }
                Log.i(TAG, "data length : " + data.length);
                byte checkSum = (byte) doCheckSum;
                boolean checksumIsOK = checkSumMethod(checkSum, recbuf[recbuf.length - 1]);
                if (checksumIsOK) {
                    int dataN = 56;
                    Log.i(TAG, "before for");
                    UserAdapter userAdapter = new UserAdapter(mMainUi);
                    String uID = userAdapter.getUID();
                    bioDataAdapter = new BioDataAdapter(mMainUi);
                    for (int i = 1; i <= dataN; i++) {
                        Log.i(TAG, Integer.toString((data[i * 7] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 1] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 2] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 3] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 4] & 0xFF)) + ", " + Integer.toString((data[(i * 7) + 5] & 0xFF)) + ", "
                                + Integer.toString((data[(i * 7) + 6] & 0xFF)));
                        if (!Integer.toString(data[i * 7]).equals("0")) {
                            String bpHigh = Integer.toString((data[i * 7] & 0xFF));
                            String bpLow = Integer.toString((data[(i * 7) + 1] & 0xFF));
                            String bpPulse = Integer.toString((data[(i * 7) + 2] & 0xFF));
                            String year = Integer.toString((((data[(i * 7) + 3]) >> 4) & 0x0F) + 2000);//西元年yy
                            String month = Integer.toString(data[(i * 7) + 3] & 0x0F);//月
                            String day = Integer.toString(((data[(i * 7) + 4]) >> 3) & 0x1F);//日
                            String hour = Integer.toString(((((data[(i * 7) + 4]) << 2) & 0x1C) + ((data[(i * 7) + 5] >> 6) & 0x03)));//時
                            String minute = Integer.toString(data[(i * 7) + 5] & 0x3F);//分
                            String recordDate = year + "-" + (month.length() > 1 ? month : "0" + month) + "-" + (day.length() > 1 ? day : "0" + day) + " " + (hour.length() > 1 ? hour : "0" + hour) + ":" + (minute.length() > 1 ? minute : "0" + minute) + ":00";
                            Log.i(TAG, recordDate + ", 收縮壓:" + bpHigh + ", 舒張壓:" + bpLow + ", 脈搏:" + bpPulse);

                            //將資料存進SQLite
                            BioData bioData = new BioData();
                            bioData.set_id(uID + recordDate + "WatchBPHome");
                            bioData.setUserId(uID);
                            bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                            bioData.setDeviceId("WatchBPHome");
                            bioData.setDeviceTime(recordDate);
                            bioData.setBhp(bpHigh);
                            bioData.setBlp(bpLow);
                            bioData.setPulse(bpPulse);
                            bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                            bioDataAdapter.createBloodPressure(bioData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception : " + e);
            return "false";
        }
        return "true";
    }

    public String ebti_disconnect() {
        Log.i(TAG, "ebti_disconnect()");
        String snstr = "";
        byte[] recbuf;

        // the read the second Doubleword first
        //因回應只有ACK(0x06) 故ebti_request_dev_rec(cmd, recsz)的recsz給1即可
        //recsz 為預估收到的資料長度
        recbuf = ebti_request_dev_rec(CMD_REQUEST_DISCONNECT, 1, 0);
        if (recbuf == null) {
            return snstr;
        }
        snstr = String.valueOf(recbuf[0]);
        Log.i(TAG, "snstr : " + snstr);

        return snstr;
    }

    public int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public boolean checkSumMethod(byte sum, byte checksum) {
        String strSum = Integer.toHexString(sum);
        strSum = strSum.toUpperCase();
        String strChecksum = Integer.toHexString(checksum);
        strChecksum = strChecksum.toUpperCase();
        Log.i(TAG, "checkSumMethod(): sum : " + strSum + ", checksum : " + strChecksum);
        if (strSum.equals(strChecksum)) {
            Log.i(TAG, "CheckSum true");
            return true;
        } else {
            Log.i(TAG, "CheckSum false");
            return false;
        }
    }

    //版本資訊 ASCII 16位元 轉字串
    public String ebti_byte_to_ascii(byte b) {
        return Character.valueOf((char) b).toString();
    }

    //int to byte array
    public byte[] ebti_int_to_byte_array(int sid) {
        byte[] byteSID = new byte[2];
        for (int j = 0; j < 2; j++) {
            int offset = (byteSID.length - 1 - j) * 8;
            byteSID[j] = (byte) ((sid >>> offset) & 0xFF);
            Log.i(TAG, "byteSID[" + j + "]" + Integer.toHexString(byteSID[j]));
        }
        return byteSID;
    }

    //==============================================================

    //百略
    public final static int CMD_READ_FOR_TIME_OF_DEVICE = 10;
    public final static int CMD_SET_THE_TIME_OF_DEVICE = 11;
    public final static int CMD_SET_TIME = 12;
    public final static int CMD_SENT_USUAL_MODE_MEMORY = 13;
    public final static int CMD_SENT_DIAGNOSTIC_MODE_MEMORY = 14;
    public final static int CMD_DISCONNECT = 15;
    public final static int WAIT_PUSH_DATA = 16;

    //=====================================================================================

    public class TaidocCmdThread extends Thread {
        private int mCmd;

        public TaidocCmdThread(int devtype) {
            mDevType = devtype;
        }

        public void setCmd(int cmd) {
            mCmd = cmd;
        }

        @Override
        public void run() {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                Log.e(TAG, "sleep interrupted: should not happen", e);
            }

            switch (mCmd) {
                //百略
                case CMD_READ_FOR_TIME_OF_DEVICE:
                    Log.i(TAG, "case CMD_READ_FOR_TIME_OF_DEVICE:" + CMD_READ_FOR_TIME_OF_DEVICE);
                    String strtime = ebti_request_read_the_device_time();
                    Log.i(TAG, "case CMD_READ_FOR_TIME_OF_DEVICE: ,strtime:" + strtime);
                    try {
                        mHandler.obtainMessage(
                                GetBlueToothDeviceDataService.MESSAGE_READ_FOR_TIME_OF_DEVICE,
                                strtime
                        ).sendToTarget();
                    } catch (Exception e) {
                        Log.e(TAG, "CMD_READ_FOR_TIME_OF_DEVICE", e);
                    }
                    //返回
                    break;

                case CMD_SET_THE_TIME_OF_DEVICE:
                    Log.i(TAG, "case CMD_SET_THE_TIME_OF_DEVICE:" + CMD_SET_THE_TIME_OF_DEVICE);
                    String strACK = ebti_set_the_time_of_device();
                    if (strACK.equals("6")) {
                        mHandler.obtainMessage(
                                GetBlueToothDeviceDataService.MESSAGE_SET_THE_TIME_OF_DEVICE,
                                strACK
                        ).sendToTarget();
                    }
                    break;

                case CMD_SET_TIME:
                    Log.i(TAG, "case CMD_SET_TIME:" + CMD_SET_TIME);
                    String strACKBack = ebti_set_the_time_of_device_sent_data();
                    if (strACKBack.equals("6")) {
                        mHandler.obtainMessage(
                                GetBlueToothDeviceDataService.MESSAGE_SET_THE_TIME_OF_DEVICE_END,
                                strACKBack
                        ).sendToTarget();
                    }
                    break;

                case CMD_SENT_USUAL_MODE_MEMORY:
                    Log.i(TAG, "case CMD_SENT_USUAL_MODE_MEMORY:" + CMD_SENT_USUAL_MODE_MEMORY);
                    String strSentUsualModeMemoryReturn = ebti_sent_usual_mode_memory();
                    mHandler.obtainMessage(
                            GetBlueToothDeviceDataService.MESSAGE_SENT_USUAL_MODE_MEMORY,
                            strSentUsualModeMemoryReturn
                    ).sendToTarget();
                    break;

                case CMD_SENT_DIAGNOSTIC_MODE_MEMORY:
                    Log.i(TAG, "case CMD_SENT_DIAGNOSTIC_MODE_MEMORY:" + CMD_SENT_DIAGNOSTIC_MODE_MEMORY);
                    String strSentDiagnosticModeMemoryReturn = ebti_sent_diagnostic_mode_memory();
                    mHandler.obtainMessage(
                            GetBlueToothDeviceDataService.MESSAGE_SENT_DIAGNOSTIC_MODE_MEMORY,
                            strSentDiagnosticModeMemoryReturn
                    ).sendToTarget();
                    break;

                case CMD_DISCONNECT:

                    Log.i(TAG, "case CMD_DISCONNECT:" + CMD_DISCONNECT);
                    String strDisconnectReturn = ebti_disconnect();
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "WatchBPhome CMD_DISCONNECT Error");
                    }
                    mHandler.obtainMessage(
                            GetBlueToothDeviceDataService.MESSAGE_DISCONNECT,
                            strDisconnectReturn
                    ).sendToTarget();
                    break;

                case WAIT_PUSH_DATA:
                    Log.i(TAG, "WAIT_PUSH_DATA");
                    ebti_wait_to_read_datas();
                    mHandler.obtainMessage(
                            GetBlueToothDeviceDataService.MESSAGE_DISCONNECT,
                            ""
                    ).sendToTarget();
                    break;
            }
            if (D) {
                Log.i(TAG, "BTWatchBPhome.run(): OVER OVER OVER");
            }
        }
    }
}
