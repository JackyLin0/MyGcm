package tw.com.omnihealthgroup.healthcare.myhealthcare.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Date;

public class XenonBPParser {
    //-----------------Constants--------------------
    // Debugging
    private static final String TAG = "XenonBPParser";
    private static final boolean D = true;

    private static final int DEFAULT_BPDATA_LENGTH = 17;
    private static final byte[] BPHEADER = {'K', 15};

    //------------------Variables------------------------
    private final Handler mHandler;        //Handler Object
    private static byte[] BPDataBuffer;

    //Constructor
    public XenonBPParser(Context context, Handler handler) {
        mHandler = handler;
        BPDataBuffer = new byte[0];
    }

    public int getBufferLength() {
        if (BPDataBuffer != null) {
            return BPDataBuffer.length;
        }
        else {
            return 0;
        }
    }

    public byte[] getBPDataBuffer() {
        return BPDataBuffer;
    }

    public void AddBPData(byte[] BPData) {
        BPDataBuffer = Combine(BPDataBuffer, BPData);
        if (D) Log.d(TAG, "addBPData:" + Integer.toString(BPData.length));
        ParseBP(1);
    }

    //Combine and Return ByteData(A1+A2)
    private byte[] Combine(byte[] A1, byte[] A2) {
        byte[] R = new byte[A1.length + A2.length];
        System.arraycopy(A1, 0, R, 0, A1.length);
        System.arraycopy(A2, 0, R, A1.length, A2.length);
        return R;
    }

    public void ParseBP(int retry) {
        if (D) Log.d(TAG, "BPDataBufferLength:" + Integer.toString(BPDataBuffer.length));
        if (BPDataBuffer.length >= DEFAULT_BPDATA_LENGTH) {
            int i = getiHeader();
            if (D) Log.d(TAG, "iHead:" + Integer.toString(i));
            if (i >= 0) {
                int iEnd = i + DEFAULT_BPDATA_LENGTH - 1;
                if (D)
                    Log.d(TAG, "BufferLen:" + Integer.toString(BPDataBuffer.length) + "iEnd:" + Integer.toString(iEnd));
                //check length
                if (BPDataBuffer.length >= iEnd + 1) {
                    //check ending char
                    if (BPDataBuffer[iEnd] == 0) {
                        int sys = (BPDataBuffer[i + 5] & 0xFF);
                        int dia = (BPDataBuffer[i + 6] & 0xFF);
                        int hr = (BPDataBuffer[i + 7] & 0xFF);
                        Date BPdate = DosTimeDecode(
                                (BPDataBuffer[i + 9] & 0xFF),
                                (BPDataBuffer[i + 10] & 0xFF),
                                (BPDataBuffer[i + 11] & 0xFF),
                                (BPDataBuffer[i + 12] & 0xFF));

                        if (D) Log.d(TAG, "sys:" + Integer.toString(sys) +
                                ",dia:" + Integer.toString(dia) +
                                ",hr:" + Integer.toString(hr) +
                                ",time:" + BPdate.toString());

                        Bundle BPBundle = new Bundle();
                        BPBundle.putInt("SYS", sys);
                        BPBundle.putInt("DIA", dia);
                        BPBundle.putInt("HR", hr);
                        BPBundle.putLong("TIME", BPdate.getTime());

                        mHandler.obtainMessage(MainService.MESSAGE_GOT_BP, BPBundle).sendToTarget();

                        int LeftLen = BPDataBuffer.length - (iEnd + 1);
                        if (LeftLen > 0) {
                            byte[] T = new byte[LeftLen];
                            System.arraycopy(BPDataBuffer, iEnd + 1, T, 0, LeftLen);
                            BPDataBuffer = T;
                        } else {
                            BPDataBuffer = new byte[0];
                        }
                        if (D) Log.d(TAG, "ileft:" + Integer.toString(LeftLen));
                    }
                }
            } else {
                BPDataBuffer = new byte[0];
                mHandler.obtainMessage(MainService.MESSAGE_GOT_BP_ERROR, 1, -1).sendToTarget();
            }
        }//of [if(XenonPackageBuffer.length > 0)]
    }//of [public void ConnectAndUpload()]

    private Date DosTimeDecode(int B1, int B2, int B3, int B4) {
        if (B1 == 0 && B2 == 0 && B3 == 0 && B4 == 0) {
            return new Date();
        } else {
            int y = (B1 >>> 1);
            if (y > 0) {
                y = y + 1980;
            }
            int m = (B1 % 2) * 8 + (B2 >>> 5);
            int d = B2 % 32;
            int hour = B3 >>> 3;
            int min = (B3 % 8) * 8 + (B4 >>> 5);
            int sec = (B4 % 32) * 2;

            if (D) Log.d(TAG, "y:" + Integer.toString(y)
                    + "m:" + Integer.toString(m)
                    + "d:" + Integer.toString(d)
                    + "hour:" + Integer.toString(hour)
                    + "min:" + Integer.toString(min)
                    + "sec:" + Integer.toString(sec));
            return new Date(y - 1900, m - 1, d, hour, min, sec);
        }
    }

    private int getiHeader() {
        for (int i = 0; i <= BPDataBuffer.length - 2; i++) {
            if (BPDataBuffer[i] == BPHEADER[0] && BPDataBuffer[i + 1] == BPHEADER[1]) {
                return i;
            }
        }
        return -1;
    }
}
