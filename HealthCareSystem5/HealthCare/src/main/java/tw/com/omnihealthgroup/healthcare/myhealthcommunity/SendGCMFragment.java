package tw.com.omnihealthgroup.healthcare.myhealthcommunity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;

/**
 * Created by Administrator on 2016/7/26.
 */
public class SendGCMFragment extends DialogFragment {
    private static final String TAG = "SendGCMFragment";
    private View rootView;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    private EditText add_gcm_name, add_gcm_body;
    private Button btn_commit;
    private JSONObject reguestobj = null;
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
        rootView = inflater.inflate(R.layout.dialogfragment_send_gcm, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment

        initView();

        initHandler();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new pushmessage().cancel(true);
        new pushmessage_valid().cancel(true);
    }

    private void initView() {
        add_gcm_name = (EditText) rootView.findViewById(R.id.add_gcm_name);
        add_gcm_body = (EditText) rootView.findViewById(R.id.add_gcm_body);
        btn_commit = (Button) rootView.findViewById(R.id.btn_commit);
    }

    private void initHandler() {
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_gcm_name.getText().toString().trim().equals("")) {
                    webServiceConnection.editTextAlertDialog(getContext(), getResources().getString(R.string.add_gcm_name));
                } else if (add_gcm_body.getText().toString().trim().equals("")) {
                    webServiceConnection.editTextAlertDialog(getContext(), getResources().getString(R.string.add_gcm_body));
                } else {
                    if (webServiceConnection.isOnline(getContext())) {
                        try {
                            reguestobj = new JSONObject();
                            reguestobj.put("title", add_gcm_name.getText().toString().trim());
                            reguestobj.put("body", add_gcm_body.getText().toString().trim());

                            new pushmessage().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

    protected class pushmessage extends AsyncTask<String, String, JSONObject> {
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
                    UserAdapter userAdapter = new UserAdapter(getContext());
                    List<User> userList = userAdapter.getAllUser();
                    String UserUniqueId = null;
                    for (User user : userList) {
                        Log.v(TAG + "UserUniqueId", user.getUserUniqueId().toString());
                        UserUniqueId = user.getUserUniqueId();
                    }

                    response = webServiceConnection.pushmessage(reguestobj, UserUniqueId);
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
                        Log.v(TAG + TAG, response.getString("u"));
                        Log.v(TAG + TAG, response.getString("v"));
                        pushId = response.getString("u");

                        String salt = "24drs_push_serv";
                        String password = "12345";
                        String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                        Log.v(TAG + "AES_decrypt", AES_decrypt);

                        SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                        Log.v(TAG + "SHA_encrypt", SHA_encrypt);

                        pb.dismiss();
                        new pushmessage_valid().execute();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
            } finally {
                pb.dismiss();
            }
        }
    }

    protected class pushmessage_valid extends AsyncTask<String, String, String> {
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
                    response = webServiceConnection.pushmessage_valid(pushId, SHA_encrypt);
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

                        if (prf.getInt("pushdeviceReg", 0) == 88) {
                            Toast.makeText(getContext(), getString(R.string.gcm_sendMessage), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.gcm_sendMessage) + "；" + getString(R.string.gcm_remind), Toast.LENGTH_LONG).show();
                        }
                        dismiss();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
            } finally {
                pb.dismiss();
            }
        }
    }

}
