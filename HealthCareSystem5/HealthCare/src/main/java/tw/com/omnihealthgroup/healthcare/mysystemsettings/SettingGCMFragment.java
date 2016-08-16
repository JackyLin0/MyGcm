package tw.com.omnihealthgroup.healthcare.mysystemsettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.gcm.GcmUtil;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;

/**
 * Created by Administrator on 2016/7/26.
 */
public class SettingGCMFragment extends DialogFragment {
    private static final String TAG = "SettingGCMFragment";
    private View rootView;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    private Switch control_btn_send, control_btn_reg;
    private String pushId = null, SHA_encrypt = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.dialogfragment_setting_gcm, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment

        initView();

        initHandler();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new pushdeviceReg().cancel(true);
        new pushdeviceReg_valid().cancel(true);

        new pushdeviceout().cancel(true);
        new pushdeviceout_valid().cancel(true);
    }

    private void initView() {
        //        control_btn_send = (Switch) rootView.findViewById(R.id.control_btn_send);
        control_btn_reg = (Switch) rootView.findViewById(R.id.control_btn_send);
    }

    private void initHandler() {
        if (prf.getInt("pushdeviceReg", 0) == 88) {
            control_btn_reg.setChecked(true);
        } else {
            control_btn_reg.setChecked(false);
        }

        control_btn_reg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (webServiceConnection.isOnline(getContext())) {
                        Log.v(TAG, "pushdeviceReg");
                        pushId = null;
                        SHA_encrypt = null;

                        new pushdeviceReg().execute();
                    } else {
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.msg_connect_faild_title))
                                .setMessage(getString(R.string.msg_connect_faild))
                                .setPositiveButton(getString(R.string.msg_confirm),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                dismiss();
                                            }
                                        }).show();
                    }
                } else {
                    if (webServiceConnection.isOnline(getContext())) {
                        Log.v(TAG, "pushdeviceReg");
                        pushId = null;
                        SHA_encrypt = null;

                        new pushdeviceout().execute();
                    } else {
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.msg_connect_faild_title))
                                .setMessage(getString(R.string.msg_connect_faild))
                                .setPositiveButton(getString(R.string.msg_confirm),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                dismiss();
                                            }
                                        }).show();
                    }
                }
            }
        });
    }

    protected class pushdeviceReg extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    GcmUtil gcmUtil = new GcmUtil();
                    String ModelNumber = Build.DEVICE + "_" + Build.MODEL + "_" + "SDK" + Build.VERSION.SDK;

                    UserAdapter userAdapter = new UserAdapter(getContext());
                    List<User> userList = userAdapter.getAllUser();
                    String UserUniqueId = null;
                    for (User user : userList) {
                        Log.v(TAG + "UserUniqueId", user.getUserUniqueId().toString());
                        UserUniqueId = user.getUserUniqueId();
                    }

                    response = webServiceConnection.pushdeviceReg(gcmUtil.getToken(), UserUniqueId, gcmUtil.getDeviceSerail(), ModelNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.getString("u") != null
                            && response.getString("v") != null) {
                        pushId = response.getString("u");

                        String salt = "24drs_push_serv";
                        String password = "12345";
                        String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                        Log.v(TAG + "AES_decrypt", AES_decrypt);

                        SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                        Log.v(TAG + "SHA_encrypt", SHA_encrypt);

                        pb.dismiss();
                        new pushdeviceReg_valid().execute();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        control_btn_reg.setChecked(true);
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    control_btn_reg.setChecked(true);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                control_btn_reg.setChecked(true);
            } finally {
                pb.dismiss();
            }
        }
    }

    protected class pushdeviceReg_valid extends AsyncTask<String, String, String> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String response = null;
            if (!isCancelled() && pushId != null && SHA_encrypt != null) {
                try {
                    response = webServiceConnection.pushdeviceReg_valid(pushId, SHA_encrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.equals("success")) {

                        GcmUtil gcmUtil = new GcmUtil();
                        prf.edit().putString("GCMRegistration_token", gcmUtil.getToken())
                                .putInt("pushdeviceReg", 88)
                                .commit();
                        Log.v(TAG + "GCMRegistration_token", prf.getString("GCMRegistration_token", ""));

                        Toast.makeText(getContext(), getString(R.string.gcm_pushdeviceReg), Toast.LENGTH_LONG).show();
                        dismiss();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        control_btn_reg.setChecked(true);
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    control_btn_reg.setChecked(true);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                control_btn_reg.setChecked(true);
            } finally {
                pb.dismiss();
            }
        }
    }

    protected class pushdeviceout extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    GcmUtil gcmUtil = new GcmUtil();
                    //                    String ModelNumber = Build.DEVICE + "_" + Build.MODEL + "_" + "SDK" + Build.VERSION.SDK;

                    UserAdapter userAdapter = new UserAdapter(getContext());
                    List<User> userList = userAdapter.getAllUser();
                    String UserUniqueId = null;
                    for (User user : userList) {
                        Log.v(TAG + "UserUniqueId", user.getUserUniqueId().toString());
                        UserUniqueId = user.getUserUniqueId();
                    }

                    response = webServiceConnection.pushdeviceout(gcmUtil.getToken(), UserUniqueId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.getString("u") != null
                            && response.getString("v") != null) {
                        Log.v(TAG, response.getString("u"));
                        Log.v(TAG, response.getString("v"));
                        pushId = response.getString("u");

                        String salt = "24drs_push_serv";
                        String password = "12345";
                        String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                        Log.v(TAG + "AES_decrypt", AES_decrypt);

                        SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                        Log.v(TAG + "SHA_encrypt", SHA_encrypt);

                        pb.dismiss();
                        new pushdeviceout_valid().execute();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        control_btn_reg.setChecked(false);
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    control_btn_reg.setChecked(false);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                control_btn_reg.setChecked(false);
            } finally {
                pb.dismiss();
            }
        }
    }

    protected class pushdeviceout_valid extends AsyncTask<String, String, String> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String response = null;
            if (!isCancelled() && pushId != null && SHA_encrypt != null) {
                try {
                    response = webServiceConnection.pushdeviceout_valid(pushId, SHA_encrypt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.equals("success")) {

                        //                        GcmUtil gcmUtil = new GcmUtil();
                        //                        prf.edit().putString("GCMRegistration_token", gcmUtil.getToken()).commit();
                        //                        Log.v(TAG + "GCMRegistration_token", prf.getString("GCMRegistration_token", ""));
                        prf.edit().putInt("pushdeviceReg", 13).commit();
                        Toast.makeText(getContext(), getString(R.string.gcm_pushdeviceOut), Toast.LENGTH_LONG).show();
                        dismiss();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        control_btn_reg.setChecked(false);
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    control_btn_reg.setChecked(false);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                control_btn_reg.setChecked(false);
            } finally {
                pb.dismiss();
            }
        }
    }

}
