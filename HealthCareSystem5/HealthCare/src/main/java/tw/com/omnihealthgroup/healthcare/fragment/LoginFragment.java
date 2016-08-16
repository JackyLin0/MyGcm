package tw.com.omnihealthgroup.healthcare.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.gcm.GcmUtil;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.util.GSONUtil;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;


public class LoginFragment extends Fragment {
    public static final String TAG = "LoginFragment";
    private View rootView;
    private Button loginBtn;
    private WebView myBrowser = null;
    private String authCode = null, pushId = null, SHA_encrypt = null;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    //GCM
    //    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 10000;
    //    private BroadcastReceiver gcmRegistrationBroadcastReceiver;
    //    private boolean isReceiverRegistered = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_health, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        checkView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new queryUserinfo().cancel(true);
        new pushdeviceReg().cancel(true);
        new pushdeviceReg_valid().cancel(true);

        new TokenExp().cancel(true);
        new TokenGet().cancel(true);
    }

    private void initView() {
        loginBtn = (Button) rootView.findViewById(R.id.main_btnSubmit);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);

            }
        });
    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (webServiceConnection.isOnline(getContext())) {
            if (prf.getString("access_token", "").getBytes().length > 0
                    && prf.getString("take_time", "").getBytes().length > 0) {
                Log.v(TAG + "access_token", String.valueOf(prf.getString("access_token", "").getBytes().length));
                String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));
                if (checkLogin.equals("true")) {
                    new queryUserinfo().execute();
                } else if (checkLogin.equals("exToke")) {
                    new TokenExp().execute();
                } else {
                    getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);
                }
            } else {
                getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);
                //                Log.v("access_token", String.valueOf(prf.getString("access_token", "").getBytes().length));
                //                initView();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.msg_connect_faild_title))
                    .setMessage(getString(R.string.msg_connect_faild))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    getActivity().finish();
                                }
                            }).show();
        }
    }

    protected class queryUserinfo extends AsyncTask<String, String, JSONObject> {
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
                    response = webServiceConnection.queryUserinfo(prf.getString("access_token", ""));
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
                    if (response.getString("message").equals("Success.")) {

                        Map<String, String> responsemap = new HashMap<>();
                        responsemap = GSONUtil.GsonToMaps(response.getJSONObject("result").toString());

                        UserAdapter userAdapter = new UserAdapter(getContext());
                        userAdapter.delAllUser();
                        User user = new User();

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = formatter.parse(responsemap.get("userBirthDay"));
                        String timeStr = formatter.format(date);

                        user.setUserUniqueId(responsemap.get("userUniqueId"));
                        user.setRfid(responsemap.get("rfid"));
                        user.setUserIDNO(responsemap.get("userIDNO"));
                        user.setName(responsemap.get("userName"));
                        user.setNickname(responsemap.get("userEngName"));
                        user.setGender(responsemap.get("userSex"));
                        user.setBirthday(timeStr);
                        user.setUserNationality(responsemap.get("userNationality"));
                        user.setPhone(responsemap.get("userHomeTEL"));
                        user.setMobile(responsemap.get("userMobile"));
                        user.setEmail(responsemap.get("userEMail"));
                        user.setUserBlood(responsemap.get("userBlood"));
                        user.setUserRhType(responsemap.get("userRhType"));
                        user.setUserMarried(responsemap.get("userMarried"));

                        userAdapter.createtUser(user);

                        pb.dismiss();

                        GcmUtil gcmUtil = new GcmUtil();
                        if (prf.getString("GCMRegistration_token", "").getBytes().length < 1
                                && gcmUtil.getToken() != prf.getString("GCMRegistration_token", "")) {
                            new pushdeviceReg().execute();
                        } else {
                            //進入側選單
                            Fragment fragment = new MainFragment();
                            //                        Bundle bundle = new Bundle();
                            //                        bundle.putString("userName", responsemap.get("userName"));
                            //                        if (getArguments() != null) {
                            //                            bundle.putString("kindcode", getArguments().getString("kindcode"));
                            //                            bundle.putString("msgpk", getArguments().getString("msgpk"));
                            //                            System.out.println(TAG + " / " + getArguments().getString("kindcode"));
                            //                        }
                            //                        fragment.setArguments(bundle);
                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, fragment)
                                            //                                .commit();
                                    .commitAllowingStateLoss();
                        }

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
                    Log.v("response", response.toString());
                    if (response.getString("u") != null
                            && response.getString("v") != null) {
                        Log.v(TAG, response.getString("u"));
                        Log.v(TAG, response.getString("v"));
                        pushId = response.getString("u");

                        String salt = "24drs_push_serv";
                        String password = "12345";
                        String AES_decrypt = webServiceConnection.decrypt(response.getString("v"), salt.getBytes(), password.getBytes());
                        Log.v("AES_decrypt", AES_decrypt);

                        SHA_encrypt = webServiceConnection.getSHA(AES_decrypt);
                        Log.v("SHA_encrypt", SHA_encrypt);

                        pb.dismiss();
                        new pushdeviceReg_valid().execute();
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
                        //                        Map<String, String> responsemap = new HashMap<>();
                        //                        responsemap = GSONUtil.GsonToMaps(response.getJSONObject("result").toString());
                        //                        Log.v("responsemap", responsemap.toString());
                        //
                        //                        UserAdapter userAdapter = new UserAdapter(getContext());
                        //                        userAdapter.delAllUser();
                        //                        User user = new User();
                        //
                        //                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        //                        Date date = formatter.parse(responsemap.get("userBirthDay"));
                        //                        String timeStr = formatter.format(date);
                        //
                        //                        user.setUserUniqueId(responsemap.get("userUniqueId"));
                        //                        user.setRfid(responsemap.get("rfid"));
                        //                        user.setUserIDNO(responsemap.get("userIDNO"));
                        //                        user.setName(responsemap.get("userName"));
                        //                        user.setNickname(responsemap.get("userEngName"));
                        //                        user.setGender(responsemap.get("userSex"));
                        //                        user.setBirthday(timeStr);
                        //                        user.setUserNationality(responsemap.get("userNationality"));
                        //                        user.setPhone(responsemap.get("userHomeTEL"));
                        //                        user.setMobile(responsemap.get("userMobile"));
                        //                        user.setEmail(responsemap.get("userEMail"));
                        //                        user.setUserBlood(responsemap.get("userBlood"));
                        //                        user.setUserRhType(responsemap.get("userRhType"));
                        //                        user.setUserMarried(responsemap.get("userMarried"));
                        //
                        //                        userAdapter.createtUser(user);

                        pb.dismiss();
                        //進入側選單
                        Fragment fragment = new MainFragment();
                        //                        Bundle bundle = new Bundle();
                        //                        bundle.putString("userName", responsemap.get("userName"));
                        //                        if (getArguments() != null) {
                        //                            bundle.putString("kindcode", getArguments().getString("kindcode"));
                        //                            bundle.putString("msgpk", getArguments().getString("msgpk"));
                        //                            System.out.println(TAG + " / " + getArguments().getString("kindcode"));
                        //                        }
                        //                        fragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, fragment)
                                        //                                .commit();
                                .commitAllowingStateLoss();
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

    /**
     * GetAuthCode
     */
    protected void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(R.string.message_title), getString(R.string.msg_tokenget), true, false);
        pb.show();

        auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //去除Dialog標題
        auth_dialog.setContentView(dialogid);
        myBrowser = (WebView) auth_dialog.findViewById(webid);
        webServiceConnection.setBrowserProperty(myBrowser);
        webServiceConnection.setWebviewProperty(myBrowser, getContext());

        String webSiteStr = webServiceConnection.AuthServer + "/" + webServiceConnection.AuthRequest
                + "?response_type=code"
                + "&client_id=" + webServiceConnection.CLIENT_ID
                //                + "&secret_key=" + webServiceConnection.CLIENT_SECRET
                + "&redirect_url=" + webServiceConnection.REDIRECT_URI
                + "&display=page"
                + "&scope=user_profile"
                + "&scope=measure_data"
                + "&scope=health_report"
                + "&state=Oauth_Call"
                + "&access_type=online"
                + "&prompt=none"
                + "&login_hint=email";

        Log.v(TAG + "webSiteStr", webSiteStr);
        myBrowser.loadUrl(webSiteStr);

        myBrowser.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb.dismiss();
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    //                    Toast.makeText(context, "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                    pb.dismiss();
                    auth_dialog.dismiss();
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(context, getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    protected class TokenGet extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), true, false);
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
                    response = webServiceConnection.getTokenRequest(authCode, webServiceConnection.CLIENT_ID, webServiceConnection.CLIENT_SECRET);
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
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .putInt("day_count", 3) //初始筆數
                                .commit();

                        pb.dismiss();
                        new queryUserinfo().execute();

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

    protected class TokenExp extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenexp), true, false);
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
                    response = webServiceConnection.getTokenExRequest(prf.getString("access_token", ""), prf.getString("refresh_token", ""));
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
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .commit();

                        pb.dismiss();
                        new queryUserinfo().execute();

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

    /**
     * 登入的主流程
     *
     * @param username MIS帳號
     * @param password MIS密碼
     */

    //    protected void loginSOP(final CharSequence username, final CharSequence password) {
    //        final ProgressFragment progressFragment = ProgressFragment.newInstance("登入中...", false);
    //        progressFragment.show(LoginFragment.this.getFragmentManager(), null);
    //        LoginFragment.this.onLogin(username, password, new OnLoginProgressState() {
    //
    //                    @Override
    //                    public void onProgress(final LoginProgressState state) {
    //                        new Handler(Looper.getMainLooper()).post(
    //                                new Runnable() {
    //
    //                                    @Override
    //                                    public void run() {
    //                                        Toast.makeText(
    //                                                getContext(),
    //                                                state.s,
    //                                                Toast.LENGTH_SHORT
    //                                        ).show();
    //                                    }
    //                                }
    //                        );
    //                    }
    //                },
    //                new OnLoginCallback() {
    //                    @Override
    //                    public boolean onResult(boolean isSuccess) {
    //                        progressFragment.dismissAllowingStateLoss();
    //                        //登入成功
    //                        if (isSuccess) {
    //                            new Thread(new Runnable() {
    //                                @Override
    //                                public void run() {
    //                                    Log.d(TAG, "Start Register to thired party server");
    //                                    GcmUtil gcmUtil = new GcmUtil();
    //                                    //註冊推播開關設定
    //                                    WebServiceConnection webServiceConnection = new WebServiceConnection();
    //                                    webServiceConnection.getRegisterMachCode(
    //                                            WebServiceConnection.APP_ID,
    //                                            MyAccountManager.GetAccountName().toString(),
    //                                            gcmUtil.getToken(),
    //                                            gcmUtil.getDeviceSerail()
    //                                    );
    //                                    Log.d(TAG, "Registed to thired party server");
    //                                }
    //                            }).start();
    //
    //                            //進入側選單
    //                            Fragment mainFragment = new MainFragment();
    //                            Bundle bundle = new Bundle();
    //                            bundle.putString("USERACCOUNT", username.toString());
    //                            bundle.putString("USERPASSWORD", password.toString());
    //                            if (getArguments() != null) {
    //                                bundle.putString("kindcode", getArguments().getString("kindcode"));
    //                                bundle.putString("msgpk", getArguments().getString("msgpk"));
    //                                System.out.println(TAG + " / " + getArguments().getString("kindcode"));
    //                            }
    //                            mainFragment.setArguments(bundle);
    //                            getFragmentManager()
    //                                    .beginTransaction()
    //                                    .replace(R.id.container, mainFragment)
    //                                    .commit();
    //                        }
    //                        return false; // return false = 不消耗 onResult 結果
    //                    }
    //                }
    //        );
    //    }

    /**
     * 登入接口
     *
     * @param acc             MIS帳號
     * @param pwd             MIS密碼
     * @param onLoginCallback 登入結果回饋
     */
    //    protected void onLogin(
    //            final CharSequence acc,
    //            final CharSequence pwd,
    //            @NonNull final OnLoginProgressState onLoginProgressHint,
    //            @NonNull final OnLoginCallback onLoginCallback
    //    ) {
    //        /**
    //         * 帳密寫入SharedPreference
    //         */
    //        getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE)
    //                .edit().putString(ACC, acc.toString()).putString(PWD, pwd.toString()).commit();
    //        /**
    //         * 初始化 MyAccountManager
    //         */
    //        String localPart = MyAccountManager.Extract(acc);
    //        if (localPart == null) {
    //            onLoginProgressHint.onProgress(LoginProgressState.AccountError);
    //            onLoginCallback.onResult(false);
    //            return;
    //        }
    //
    //        SmackApplication smackApplication = (SmackApplication) getActivity().getApplication();
    //        MyAccountManager.Initial(acc, smackApplication.getFullJid(localPart, true));
    //
    //        /**
    //         * MIS Check, Query My Info, and XMPP Login
    //         */
    //        new AsyncTask<Void, Void, Boolean>() {
    //
    //            @Override
    //            protected Boolean doInBackground(Void... params) {
    //                // MIS Check
    //                boolean isSuccess = Boolean.parseBoolean(
    //                        ShowContactWebReference.GetMISCheck(acc.toString(), pwd.toString(), true)
    //                );
    //                if (!isSuccess)
    //                    return false;
    //                onLoginProgressHint.onProgress(LoginProgressState.MISLoginSuccess);
    //                //                System.out.println(TAG + "MIS ACC / " + acc.toString());
    //                // Query My Info
    //                //檢查MIS帳號是否有英文字母，並替換成0
    //                String employeeAcc = acc.toString();
    //                String wordsCheck = "[a-zA-Z]+";
    //                if (employeeAcc.substring(0, 1).matches(wordsCheck)) {
    //                    employeeAcc = "0" + employeeAcc.substring(1);
    //                    if (employeeAcc.substring(1, 2).matches(wordsCheck)) {
    //                        employeeAcc = employeeAcc.substring(0, 1) + "0" + employeeAcc.substring(2);
    //                    }
    //                }
    //                //檢查MIS帳號長度，不滿6位則補0
    //                if (employeeAcc.length() < 6) {
    //                    employeeAcc = "0" + employeeAcc;
    //                } else if (employeeAcc.length() > 6) {
    //                    employeeAcc = employeeAcc.substring(employeeAcc.length() - 6);
    //                }
    //                Employee employee = EmployeeHelper.QueryEmployeeInfo(employeeAcc);
    //                if (employee != null && employee.empNo != null) {
    //                    MyAccountManager.SetHumanName(employee.name);
    //                    MyAccountManager.SetEmail(employee.mail);
    //                    MyAccountManager.SetInfo(employee);
    //                }
    //
    //                // 建立資料庫物件
    //                EmployeeDAO employeeDAO = new EmployeeDAO(getActivity());
    //                // 載入聯絡人資料
    //                if (employee != null && employeeDAO.getAll(EmployeeDAO.TABLE_EMPLOYEE_ALL).size() == 0) {
    //
    //                    //                    System.out.println(TAG + "employeeDAO.getAll() / " + employeeDAO.getAll(EmployeeDAO.TABLE_EMPLOYEE_ALL).size());
    //
    //                    // 依照公司 / 院區代碼撈取資料
    //                    employeeDAO.deleteAll(
    //                            EmployeeDAO.TABLE_EMPLOYEE_COMPANY,
    //                            employeeDAO.getAll(EmployeeDAO.TABLE_EMPLOYEE_COMPANY)
    //                    );
    //                    List<Employee> empList = EmployeeHelper.QueryCompanyEmployeeInfo(employee.companyID);
    //                    //                    System.out.println(TAG + "empList / " + empList.size());
    //                    for (int i = 0; i < empList.size(); i++) {
    //                        employee = empList.get(i);
    //                        // 新增員工資料到資料庫
    //                        employeeDAO.insert(EmployeeDAO.TABLE_EMPLOYEE_COMPANY, employee);
    //                    }
    //                } else {
    //                    Log.w(TAG, "NO EMPLOYEE DATA");
    //                }
    //                return true;
    //            }
    //
    //            @Override
    //            protected void onPostExecute(Boolean result) {
    //                if (!result)
    //                    onLoginProgressHint.onProgress(LoginProgressState.MISLoginFailure);
    //                onLoginCallback.onResult(result);
    //            }
    //        }.execute();
    //    }

    //    /**
    //     * LoginFragment 的 onLogin 專用 OnLoginCallback
    //     */
    //    public interface OnLoginCallback {
    //        /**
    //         * 回饋是否登入成功
    //         *
    //         * @param isSuccess true = 登入成功
    //         * @return true = consumed result (意思說當 return true 時, 登入的結果『已耗盡』, 不必再繼續『剩下』原型程序)
    //         */
    //        boolean onResult(boolean isSuccess);
    //    }
    //
    //    public interface OnLoginProgressState {
    //        void onProgress(LoginProgressState state);
    //    }
    //
    //    public enum LoginProgressState {
    //        MISLoginSuccess("MIS檢查成功"),
    //        MISLoginFailure("MIS檢查失敗"),
    //        AccountError("帳號錯誤");
    //
    //        LoginProgressState(String s) {
    //            this.s = s;
    //        }
    //
    //        final String s;
    //    }
}
