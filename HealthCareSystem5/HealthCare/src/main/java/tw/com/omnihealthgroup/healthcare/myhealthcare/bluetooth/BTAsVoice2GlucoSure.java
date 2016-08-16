package tw.com.omnihealthgroup.healthcare.myhealthcare.bluetooth;

import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.GetBlueToothDeviceDataService;

public class BTAsVoice2GlucoSure {

    private static final String TAG = "BTAsVoice2GlucoSure";
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

    final byte CMD_CODE_GET_PROJECT_CODE = 0x22;
    final byte CMD_CODE_GET_SYSTEM_TIME = 0x23;
    final byte CMD_CODE_GET_INDEXED_STORAGE_DATA1 = 0x25;
    final byte CMD_CODE_GET_INDEXED_STORAGE_DATA2 = 0x26;
    final byte CMD_CODE_GET_SERIAL_NUMBER1 = 0x27;
    final byte CMD_CODE_GET_SERIAL_NUMBER2 = 0x28;
    final byte CMD_CODE_STORED_REC_SZ = 0x2b;
    final byte CMD_CODE_UPDATE_SYSTEM_CLOCK = 0x33;
    final byte CMD_CODE_SHUT_DOWN = 0x50;
    final byte CMD_CODE_CLEAR_STORAGE_ALL = 0x52;
    final byte CMD_CODE_BWS_READ_DATA = 0x71;
    final byte CMD_CODE_SET_USER_PROFILE = 0x72;
    final byte CMD_CODE_GET_ACTIVE_RESPONSE = 0x54;

    //==================五鼎=================================
    final byte OP_CODE = (byte) 0xAA;

    final byte CMD_CODE_METER_RECOGNIZE = 0x0;
    final byte CMD_CODE_READ_METER_VERSION = (byte) 0xE1;
    final byte CMD_CODE_WRITING_METER_DATE_TIME = 0x16;
    final byte CMD_CODE_READ_NONUPLOAD_COUNT_AND_SID = (byte) 0xE3;
    final byte CMD_CODE_READING_ONE_RECORD = (byte) 0x0B;
    final byte CMD_CODE_DISABLE_BT = (byte) 0xEE;
    //=======================================================

    //================================五鼎============================================================
    final byte CMD_REQUEST_METER_RECOGNIZE[] = {OP_CODE, CMD_CODE_METER_RECOGNIZE, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    final byte CMD_REQUEST_METER_VERSION[] = {OP_CODE, CMD_CODE_READ_METER_VERSION, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    final byte CMD_REQUEST_WRITING_METER_DATE_TIME[] = {OP_CODE, CMD_CODE_WRITING_METER_DATE_TIME, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    final byte CMD_REQUEST_READ_NONUPLOAD_COUNT_AND_SID[] = {OP_CODE, CMD_CODE_READ_NONUPLOAD_COUNT_AND_SID, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    final byte CMD_REQUEST_READING_ONE_RECORD[] = {OP_CODE, CMD_CODE_READING_ONE_RECORD, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    final byte CMD_REQUEST_DISABLE_BT[] = {OP_CODE, CMD_CODE_DISABLE_BT, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, CMD_CODE_DISABLE_BT};

    //================================================================================================

    private final static int STATE_REQUEST_ISSUED = 1;
    private final static int STATE_REQUEST_FINISHED = 0;

    // Name of the connected device
    private String mConnectedDevName = null;
    // Local Bluetooth adapter
    // Member object for the chat services
    private BluetoothChatService mBTAV2GSService = null;

    // mb-ebti 1225-2011
    private String mDevAddress;

    //final BloodGlucoseActivity mMainUi;
    final GetBlueToothDeviceDataService mMainUi;
    private Handler mHandler;
    public TaidocCmdThread mCmdThread;


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

        public void setType(int t) {
            mType = t;
        }

        public String getName() {
            return mName;
        }

        public String getModel() {
            return mModel;
        }

        public int getType() {
            return mType;
        }
    }

    //public BTAsVoice2GlucoSure(BloodGlucoseActivity mainui,  Handler handler, BTAsVoice2GSConnService service)
    public BTAsVoice2GlucoSure(GetBlueToothDeviceDataService mainui, Handler handler, BluetoothChatService service) {
        mMainUi = mainui;
        mBTAV2GSService = service;
        mHandler = handler;
        mDevType = DEVICE_TYPE_AUTOCODE;
    }

    public void startCmdThread(int cmd) {
        mCmdThread = null;
        mCmdThread = new TaidocCmdThread(mDevType);
        mCmdThread.setCmd(cmd);
        mCmdThread.start();
    }

    //五鼎
    public void setDevName(String devName) {
        Log.i(TAG, "setDevName, devName:" + devName);
        mConnectedDevName = devName;
    }

    public int startBHDCmdService(String addr) {
        mDevType = checkDevType();
        mDevAddress = addr;
        if (mDevType == DEVICE_TYPE_AUTOCODE) {
            Log.i(TAG, "startCmdThread (DEVICE_TYPE_AUTOCODE)");
            startCmdThread(CMD_GET_METER_RECOGNIZE);
        }
        return mDevType;
    }

    public int checkDevType() {
        return DEVICE_TYPE_AUTOCODE;
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
        if (mBTAV2GSService.getState() != BTAsVoice2GSConnService.STATE_CONNECTED) {
            ebti_toast_msg("藍芽尚未連線");
            return false;
        }
        int cmdsz1 = message.length - 1;
        Log.i(TAG, "cmdsz1:" + cmdsz1 + ", message.length:" + message.length);
        message[cmdsz1] = 0;
        int i;
        for (i = 1; i < cmdsz1; i++) {
            message[cmdsz1] += message[i];
            Log.i(TAG, "check sum =" + message[cmdsz1]);
        }
        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BHDCService to write
            mBTAV2GSService.write(message);
        }
        return true;
    }


    private byte[] ebti_read_bcd_dev_blocked(int recsz) {
        Log.i(TAG, "ebti_read_bcd_dev_blocked () ");

        byte[] inbuf;
        int inbuflen;
        byte[] recbuf = new byte[128];
        int bufctr = 0;
        int i;
        int nullctr = 0;
        while (nullctr < READ_RETRY_LIMIT) {
            while (bufctr < recsz) {
                try {
                    Log.i(TAG, "start to wait for mSynchMsgQueue");
                    inbuf = mMainUi.mSynchMsgQueue.poll(READ_MESSAGE_TIMEOUT_SEC, TimeUnit.SECONDS);
                    Log.i(TAG, "mSynchMsgQueue is woken up!!!!");
                } catch (InterruptedException ex) {
                    ebti_toast_msg(" 中斷讀取資料  ");
                    Log.i(TAG, "TaiDoc read-dev interrupted ");
                    return null;
                }
                if (inbuf == null) {
                    ebti_toast_msg(" 讀取資料  回覆逾時  ");
                    Log.i(TAG, "AutoCodeBTVoice dev reply time out");
                    break;
                }
                String returnByteStr = "";
                for (int j = 0; j < inbuf.length; j++) {
                    returnByteStr += ", " + Integer.toString(inbuf[j], 16);
                }
                Log.i(TAG, "returnByteStr :" + returnByteStr);

                inbuflen = inbuf[0];
                Log.i(TAG, "inbuflen =" + inbuflen);
                for (i = 0; i < inbuflen; i++)
                    recbuf[bufctr + i] = inbuf[i + 1];
                bufctr += inbuflen;
                if (bufctr >= recsz) {
                    // construct a string from the valid bytes in the buffer
                    return recbuf;
                }
            }
            nullctr++;
        }
        return null;
    }

    private boolean ebti_chk_return_code(byte[] request, byte[] response) {
        Log.i(TAG, "ebti_request_dev_rec() : request=" + Arrays.toString(request) + "   response=" + Arrays.toString(response));
        if (request == null || response == null)
            return false;

        Log.i(TAG, "ebti_request_dev_rec() : CMD=" + request[1] + "   ACK=" + response[1]);
        return (request[1] == response[1]);
    }

    private static short ebti_bytes_2_short(byte lob, byte hib) {
        Log.i(TAG, "ebti_bytes_2_short(): [hib,lob]= [" + hib + "<<8, " + lob + "]");
        // assume Big Endian is used
        short his = (short) hib;
        short los = (short) lob;
        his = (short) (his & 0x00ff);
        los = (short) (los & 0x00ff);
        return (short) ((his << 8) + los);
        //return (short) ( ((hib & 0xff) << 8) | (lob & 0xFF) );
    }

    final static int READ_RETRY_LIMIT = 3;

    private byte[] ebti_request_dev_rec(byte[] cmd, int recsz) {
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
            recbuf = ebti_read_bcd_dev_blocked(recsz);
        }
        if (cmd != null) {
            if (!ebti_chk_return_code(cmd, recbuf)) {
                Log.e(TAG, "CMD and ACK does NOT match");
                ebti_toast_msg("CMD and ACK does NOT match");
                return null;
            }
        }
        return recbuf;
    }

    private Date ebti_get_date_in_short_int(byte[] recbuf) {
        short data = ebti_bytes_2_short(recbuf[2], recbuf[3]);
        int year = ((data & 0xfe00) >> 9) + 2000;
        int month = (data & 0x01e0) >> 5;
        int day = data & 0x1f;
        int hour = recbuf[5] & 0xff;
        int min = recbuf[4] & 0xff;

        Date date = new Date(year, month, day, hour, min, 0);

        Log.i(TAG, "ebti_request_dev_system_time():  year= " + year);
        Log.i(TAG, "ebti_request_dev_system_time(): month= " + month);
        Log.i(TAG, "ebti_request_dev_system_time():   day= " + day);
        Log.i(TAG, "ebti_request_dev_system_time():  hour= " + hour);
        Log.i(TAG, "ebti_request_dev_system_time():   min= " + min);
        return date;
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

    //五鼎
    public String ebti_request_dev_recognize() {
        String snstr = "";
        byte[] recbuf;
        int i;

        if (D)
            Log.i(TAG, "ebti_request_dev_recognize():");
        // the read the second Doubleword first
        recbuf = ebti_request_dev_rec(CMD_REQUEST_METER_RECOGNIZE, 11);
        if (recbuf == null)
            return snstr;

        if (D)
            Log.i(TAG, "ebti_request_dev_recognize():recbuf=" + Arrays.toString(recbuf));

        Log.i(TAG, "ebti_request_dev_recognize():recbuf[1](OP Code)=" + recbuf[1]);
        snstr = "";

        for (i = 2; i < 10; i++)//為 data[0]~data[7] 資料部份
            snstr += ebti_bcd_byte_to_str(recbuf[i]);
        if (D) {
            Log.i(TAG, "  2nd word: = " + recbuf[2] + " " + recbuf[3] + " " + recbuf[4] + " " + recbuf[5] + " " + recbuf[6] + " " + recbuf[7] + " " + recbuf[8] + " " + recbuf[9]);
        }
        Log.i(TAG, "  snstr: = " + snstr);
        return snstr;
    }

    public String ebti_reuest_dev_version() {
        Log.i(TAG, "ebti_reuest_dev_version():");
        String strVersion = "";
        byte[] recbuf;
        //CMD and Response
        recbuf = ebti_request_dev_rec(CMD_REQUEST_METER_VERSION, 11);
        if (recbuf == null) {
            return null;
        }
        Log.i(TAG, "ebti_request_dev_recognize():recbuf[1](OP Code)=" + recbuf[1]);

        Log.i(TAG, "  2nd word: = " + recbuf[2] + " " + recbuf[3] + " " + recbuf[4] + " " + recbuf[5] + " " + recbuf[6] + " " + recbuf[7] + " " + recbuf[8] + " " + recbuf[9]);
        for (int i = 2; i < 10; i++)//為 data[0]~data[7] 資料部份
            strVersion += ebti_bcd_byte_to_str(recbuf[i]);
        Log.i(TAG, "ebti_reuest_dev_version() return :" + strVersion);

        strVersion = "";
        for (int i = 2; i < 8; i++) {//為 data[0]~data[7] 版本部份
            if (i == 4) {
                strVersion += ", ";
            } else if (i == 7) {
                strVersion += recbuf[i];
            } else {
                strVersion += ebti_bcd_byte_get_version(recbuf[i]);
            }
        }
        return strVersion;
    }

    //版本資訊 ASCII 16位元 轉字串
    public String ebti_bcd_byte_get_version(byte b) {
        return Character.valueOf((char) b).toString();
    }

    public String ebti_reuest_write_dev_date_time() {
        byte[] recbuf;
        //取得現在時間
        Calendar c = Calendar.getInstance();

        Log.i(TAG, "year=" + (c.get(Calendar.YEAR) - 2000));
        Log.i(TAG, "month=" + (c.get(Calendar.MONTH) + 1));
        Log.i(TAG, "day=" + (c.get(Calendar.DAY_OF_MONTH)));
        Log.i(TAG, "hour=" + (c.get(Calendar.HOUR_OF_DAY)));
        Log.i(TAG, "minute=" + (c.get(Calendar.MINUTE)));

        CMD_REQUEST_WRITING_METER_DATE_TIME[2] = (byte) (c.get(Calendar.YEAR) - 2000);
        CMD_REQUEST_WRITING_METER_DATE_TIME[3] = (byte) (c.get(Calendar.MONTH) + 1);
        CMD_REQUEST_WRITING_METER_DATE_TIME[4] = (byte) (c.get(Calendar.DAY_OF_MONTH));
        CMD_REQUEST_WRITING_METER_DATE_TIME[5] = (byte) (c.get(Calendar.HOUR_OF_DAY));
        CMD_REQUEST_WRITING_METER_DATE_TIME[6] = (byte) (c.get(Calendar.MINUTE));

        for (int i = 0; i < CMD_REQUEST_WRITING_METER_DATE_TIME.length; i++) {
            Log.i(TAG, "CMD_REQUEST_WRITING_METER_DATE_TIME[" + i + "]:" + CMD_REQUEST_WRITING_METER_DATE_TIME[i]);
        }
        recbuf = ebti_request_dev_rec(CMD_REQUEST_WRITING_METER_DATE_TIME, 11);
        if (recbuf == null) {
            return "Flase";
        }
        Log.i(TAG, "ebti_request_dev_recognize():recbuf[1](OP Code)=" + recbuf[1]);
        return "OK";
    }

    public String ebti_read_nonupload_count_and_sid() {
        Log.i(TAG, "ebti_read_nonupload_count_and_sid()");
        byte[] recbuf;
        recbuf = ebti_request_dev_rec(CMD_REQUEST_READ_NONUPLOAD_COUNT_AND_SID, 11);
        return ebti_get_count_and_sid(recbuf);
    }

    //關閉BT
    public String ebti_disable_bt() {
        Log.i(TAG, "ebti_disable_bt()");
        return "OK";
    }

    public String ebti_get_count_and_sid(byte[] recbuf) {
        try {
            //Count & SID
            int getCount = ebti_bytes_2_short(recbuf[2], recbuf[3]);
            int getSid = ebti_bytes_2_short(recbuf[4], recbuf[5]);
            Log.i(TAG, "getCount:" + getCount + ", getSid:" + getSid);
            //取得紀錄資料
            for (int i = 0; i < getCount; i++) {
                Log.i(TAG, "getSid:" + getSid);
                //設定SID byte
                byte[] byteSID = ebti_int_to_byte_array(getSid);

                CMD_REQUEST_READING_ONE_RECORD[2] = byteSID[1];
                CMD_REQUEST_READING_ONE_RECORD[3] = byteSID[0];
                recbuf = ebti_request_dev_rec(CMD_REQUEST_READING_ONE_RECORD, 11);
                ebti_get_record_to_sqlite(recbuf);
                getSid++;
            }
            return "OK";
        } catch (Exception e) {
            Log.e(TAG, "ebti_get_count_and_sid Error:" + e.toString());
            return null;
        }
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

    public void ebti_get_record_to_sqlite(byte[] recbuf) {
        int recbufUnit = (recbuf[2] & 0x80) >> 7; // 0 mean mmol/L, 1 means mg/dL
        int year = (recbuf[2] & 0x7F) + 2000; //裝置的起始年為2000年
        int month = (recbuf[3] & 0xF0) >> 4;
        int CTL_E_F = (recbuf[3] & 0x8) >> 3; //CTL_ERR_Flag, 假設品管液超過range, 則CTL_E_F=1，範圍內=0，血液則無意義(欣聲2無意義)
        int CTL = (recbuf[3] & 0x6) >> 1; //Blood=0, Control L1=1, Control L2=2(for 欣聲、欣佳、欣聲2)
        int RTU = (recbuf[3] & 0x1); //已即時上傳的Flg，在批次上傳時才有意義，在即時上傳則永遠為0。
        short time = ebti_bytes_2_short(recbuf[4], recbuf[5]);
        int hour = (time & 0x03E0) >> 5;
        int minute = (time & 0xFC00) >> 10;
        int day = time & 0x1F;
        int data = ebti_bytes_2_short(recbuf[6], recbuf[7]);
        Log.i(TAG, "recbufUnit:" + recbufUnit + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", minute=" + minute
                + ", data:" + data + ", CTL_E_F:" + CTL_E_F + ", CTL:" + CTL + ", RTU:" + RTU);

        //User
        UserAdapter userAdapter = new UserAdapter(mMainUi);
        String userId = userAdapter.getUID();

        //Sqlite
        Log.i(TAG, "Create BioData!");
        BioData bioData = new BioData();
        bioData.setInputType(WebServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
        bioData.setUserId(userId);
        String strSystemTime = year + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day) + " " + (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute) + ":00";
        bioData.setDeviceTime(strSystemTime);
        bioData.setDeviceId("GlucoSureVoice2");
        bioData.set_id(userId + mDevAddress + mConnectedDevName + strSystemTime);
        Log.i(TAG, "BioDataID:" + bioData.get_id());
        bioData.setNm(String.valueOf(data));
        BioDataAdapter bioDataAdapter = new BioDataAdapter(mMainUi);
        try {
            if (data > 0) {
                bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createGlucose(bioData);
                Log.i(TAG, "Create BioData Finish, Nm:" + bioData.getNm());
            }
        } catch (SQLException e) {
            Log.e(TAG, "ebti_get_record_to_sqlite ERROR e:" + e.toString());
        }
    }
    //==============================================================

    //五鼎
    public final static int CMD_GET_METER_RECOGNIZE = 10;
    public final static int CMD_GET_METER_VERSION = 11;
    public final static int CMD_WRITE_DATE_TIME = 12;
    public final static int CMD_READ_NONUPLOAD_COUNT_AND_SID = 13;
    public final static int CMD_DISABLE_BT = 14;
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
                //五鼎
                case CMD_GET_METER_RECOGNIZE:
                    Log.i(TAG, "case CMD_CODE_METER_RECOGNIZE:" + CMD_GET_METER_RECOGNIZE);
                    String strrecognize = ebti_request_dev_recognize();
                    Log.i(TAG, "case CMD_CODE_METER_RECOGNIZE: ,strrecognize:" + strrecognize);
                    //返回
                    mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_RECOGNIZE, strrecognize).sendToTarget();
                    break;
                case CMD_GET_METER_VERSION:
                    Log.i(TAG, "case CMD_GET_METER_VERSION:" + CMD_GET_METER_VERSION);
                    String strVersion = ebti_reuest_dev_version();
                    if (strVersion != null) {
                        mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_VERSION, strVersion).sendToTarget();
                    } else {
                        ebti_toast_msg("連結失敗，請重試！");
                    }
                    break;
                case CMD_WRITE_DATE_TIME:
                    Log.i(TAG, "case CMD_WRITE_DATE_TIME:" + CMD_WRITE_DATE_TIME);
                    String srtReturn = ebti_reuest_write_dev_date_time();
                    mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_WRITE_DATE_TIME, srtReturn).sendToTarget();
                    break;
                case CMD_READ_NONUPLOAD_COUNT_AND_SID:
                    Log.i(TAG, "case CMD_READ_NONUPLOAD_COUNT_AND_SID:" + CMD_READ_NONUPLOAD_COUNT_AND_SID);
                    String strCountSidReturn = ebti_read_nonupload_count_and_sid();
                    if (strCountSidReturn != null) {
                        mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_READ_NONUPLOAD_COUNT_AND_SID, strCountSidReturn).sendToTarget();
                    } else {
                        ebti_toast_msg("資料讀取失敗，請重試！");
                    }
                    break;
                case CMD_DISABLE_BT:
                    Log.i(TAG, "case CMD_DISABLE_BT:" + CMD_DISABLE_BT);
                    String strDisableBTReturn = ebti_disable_bt();
                    if (strDisableBTReturn != null) {
                        mHandler.obtainMessage(GetBlueToothDeviceDataService.MESSAGE_DISABLE_BT, strDisableBTReturn).sendToTarget();
                    }
                    break;
                //=================================================================
            }
            if (D) {
                Log.i(TAG, "TaidocCmdThread.run(): OVER OVER OVER");
            }
        }
    }
}
