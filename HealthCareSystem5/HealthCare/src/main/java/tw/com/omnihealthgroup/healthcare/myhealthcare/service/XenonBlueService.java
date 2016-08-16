package tw.com.omnihealthgroup.healthcare.myhealthcare.service;

/*
 *  KY LAB program source for development
 *
 *	Ver: EM202.1.0
 *	Data : 2011/08/22
 *	Designer : Weiting Lin
 *
 *	function description:
 *	Module for decode XenonBlue data. 	
 */


import android.content.Context;
import android.os.Handler;
import android.util.Log;


public class XenonBlueService {
    // Debugging
    private static final String TAG = "XenonBlueService";
    private static final boolean D = true;

    //Constant Xenon Length Define
    private static final int XENON_HEADER_LENGTH = 0;
    private static final int XENON_INFO_LENGTH = 4;
    private static final int XENON_LENGTH = XENON_HEADER_LENGTH + XENON_INFO_LENGTH;
    private static final int XENON_MAX_LENGTH = 32;

    //Handler Object Claim
    private final Handler mHandler;

    //Data Buffer Variables
    private static byte[] XenonData;

    public XenonBlueService(Context context, Handler handler) {
        mHandler = handler;
        XenonData = new byte[0];
    }

    public void RcvDataHandler(byte[] RcvData) {
        XenonData = Combine(XenonData, RcvData);
        XenonDataHandler();
    }

    private void XenonDataHandler() {
        Log.i(TAG, "XenonDataHandler()");
        while (XenonData.length > XENON_LENGTH) {
            //取得Xenon Info
            //Java not support unsigned value, so byte's value is -128 ~ 127
            int ProductID = (XenonData[0] & 0xFF) + ((XenonData[1] & 0xFF) << 8);
            short iXenon = (short) (XenonData[2] & 0xFF);
            int iDeviceIDLength = (XenonData[3] & 0xE0);
            iDeviceIDLength = iDeviceIDLength >>> 5;
            byte iDataLength = (byte) (XenonData[3] & 0x1F);
            byte iLength = (byte) (iDeviceIDLength + iDataLength);

            if (D) {
                Log.d(TAG, "Pid:" + Integer.toString(ProductID)
                        + ",iXenon" + Integer.toString(iXenon));
            }
            if (D) {
                Log.d(TAG, "Did_Ln:" + Integer.toString(iDeviceIDLength)
                        + ",Data_Ln" + Integer.toString(iDataLength)
                        + ",iLength" + Integer.toString(iLength));
            }

            if (iLength > XENON_MAX_LENGTH) {
                if (D) Log.d(TAG, "iLenth Error:" + Integer.toString(iLength));
                //do something here
                XenonData = new byte[0];
                //接到的資料長度>=完整Xenon Package長度
            } else if (XenonData.length >= XENON_INFO_LENGTH + iLength) {
                byte[] XenonInfo = new byte[XENON_INFO_LENGTH];
//                System.arraycopy(XenonData, 0, XenonInfo, 0, XENON_INFO_LENGTH);
//                XenonInfoHandler(XenonInfo);
//                byte[] DeviceID = new byte[iDeviceIDLength];
//                System.arraycopy(XenonData, XENON_INFO_LENGTH, DeviceID, 0, iDeviceIDLength);
//                XenonDeviceIDHandler(DeviceID);

                byte[] DeviceData = new byte[iDataLength];
                System.arraycopy(XenonData, XENON_INFO_LENGTH + iDeviceIDLength, DeviceData, 0, iDataLength);
                DeviceDataHandler(DeviceData);

                //完整Xenon Package
                int XenonPackageLength = XENON_INFO_LENGTH + iLength;
                byte[] XenonPackage = new byte[XenonPackageLength];
                System.arraycopy(XenonData, 0, XenonPackage, 0, XenonPackageLength);
                XenonPackageHandler(XenonPackage);

                //delete the XenonPackage in XenonData
                int iLeft = XenonData.length - (XENON_INFO_LENGTH + iLength);
                if (iLeft > 0) {
                    byte[] TempBuffer = new byte[iLeft];
                    System.arraycopy(XenonData, XENON_INFO_LENGTH + iLength, TempBuffer, 0, iLeft);
                    XenonData = TempBuffer;
                } else {
                    XenonData = new byte[0];
                }
            }// of [if(XenonData.length >= XENON_INFO_LENGTH+iLength)]
            else { //接到的資料長度 < 完整Xenon Package長度
                break;
            }
        }// of [while(XenonData.length > XENON_LENGTH )]

    }


    private void XenonInfoHandler(byte[] XenonInfo) {
        if (D) {
            Log.d(TAG, "XenonInfoHandler" + BytesToString(XenonInfo));
        }
        if (D) {
            Log.d(TAG, "XenonInfoHandler" + Integer.toString(XenonInfo.length));
        }
        mHandler.obtainMessage(MainService.MESSAGE_XENON_INFO, XenonInfo.length, -1, XenonInfo)
                .sendToTarget();
    }

    private void XenonDeviceIDHandler(byte[] XenonDeviceID) {
        if (D) {
            Log.d(TAG, "XenonDeviceIDHandler" + BytesToString(XenonDeviceID));
        }
        mHandler.obtainMessage(MainService.MESSAGE_DEVICE_ID, XenonDeviceID.length, -1, XenonDeviceID)
                .sendToTarget();
    }

    private void DeviceDataHandler(byte[] DeviceData) {
        if (D) {
            Log.d(TAG, "DeviceDataHandler" + BytesToString(DeviceData));
        }
        mHandler.obtainMessage(MainService.MESSAGE_DEVICE_DATA, DeviceData.length, -1, DeviceData)
                .sendToTarget();
    }

    private void XenonPackageHandler(byte[] XenonPackage) {
        if (D) {
            Log.d(TAG, "XenonDataHandler" + BytesToString(XenonPackage));
        }
        mHandler.obtainMessage(MainService.MESSAGE_XENON_PACKAGE, XenonPackage.length, -1, XenonPackage)
                .sendToTarget();
    }

    //Combine and Return ByteData(A1+A2)
    private byte[] Combine(byte[] A1, byte[] A2) {
        byte[] R = new byte[A1.length + A2.length];
        System.arraycopy(A1, 0, R, 0, A1.length);
        System.arraycopy(A2, 0, R, A1.length, A2.length);
        return R;
    }

    private String BytesToString(byte[] ByteData) {
        String ByteString = "";

        for (int i = 0; i < ByteData.length; i++) {
            ByteString = ByteString + '[' + Integer.toString(ByteData[i] & 0xff) + ']';
        }
        return ByteString;
    }
}
