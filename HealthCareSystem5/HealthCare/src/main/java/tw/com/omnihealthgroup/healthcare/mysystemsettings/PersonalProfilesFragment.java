package tw.com.omnihealthgroup.healthcare.mysystemsettings;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;

/**
 * Created by Administrator on 2016/6/7.
 */
public class PersonalProfilesFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "PersonalProfilesFragment";
    private View rootView;
    private WebView myBrowser = null;
    private String authCode = null;
    private static TextView userIDNO, userName, userEngName, userSex, userBirthDay, userNationality, userHomeTEL, userMobile, userEMail, userBlood, userRhType, userMarried;
    private ImageView userimage;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1999;
    private AlertDialog alertDialog = null;
    private Bitmap mProductBitmap = null;
    public static String flag = "0";
    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_personalprofiles));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personalprofiles, container, false);
        Log.v(TAG, "onCreateView");

        initView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        flag = "0";
        checkView();

        // 監聽返回鍵
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new TokenExp().cancel(true);
        new TokenGet().cancel(true);
    }

    private void initView() {
        userIDNO = (TextView) rootView.findViewById(R.id.userIDNO);
        userName = (TextView) rootView.findViewById(R.id.userName);
        userEngName = (TextView) rootView.findViewById(R.id.userEngName);
        userSex = (TextView) rootView.findViewById(R.id.userSex);
        userBirthDay = (TextView) rootView.findViewById(R.id.userBirthDay);
        userNationality = (TextView) rootView.findViewById(R.id.userNationality);
        userHomeTEL = (TextView) rootView.findViewById(R.id.userHomeTEL);
        userMobile = (TextView) rootView.findViewById(R.id.userMobile);
        userEMail = (TextView) rootView.findViewById(R.id.userEMail);
        userBlood = (TextView) rootView.findViewById(R.id.userBlood);
        userRhType = (TextView) rootView.findViewById(R.id.userRhType);
        userMarried = (TextView) rootView.findViewById(R.id.userMarried);

        userimage = (ImageView) rootView.findViewById(R.id.imageView1);

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.alertdialog_imagebutton);

                window.findViewById(R.id.del_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                window.findViewById(R.id.edit_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getImageIntent();

                    }
                });
            }
        });


        rootView.findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), AddPersonalProfilesActivity.class);
                startActivity(intent);
            }
        });
    }

    public Intent getImageIntent() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getContext().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[]{}));

        // Calling activity should exeecute:
        // startActivityForResult(chooserIntent, 1);
        return chooserIntent;
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mProductBitmap = (Bitmap) extras.get("data");
        userimage.setImageBitmap(mProductBitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (resultCode == Activity.RESULT_OK) {
            handleSmallCameraPhoto(intent);
        } else {
            if (requestCode == 1) {
                InputStream stream = null;
                if (intent == null) {
                    System.out.println("DATA IS NULL..");
                } else {
                    try {
                        if (mProductBitmap != null) {
                            mProductBitmap.recycle();
                        }
                        stream = getActivity().getContentResolver().openInputStream(
                                intent.getData());
                        mProductBitmap = BitmapFactory.decodeStream(stream);
                        System.out.println(mProductBitmap);
                        System.out.println("Setting image result");
                        userimage.setImageBitmap(mProductBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null)
                            try {
                                stream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                    }
                }
            }
        }
    }


    //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        Log.v("resultCode", String.valueOf(resultCode));
    //        if (requestCode == 1) {
    //            //            if (resultCode == RESULT_OK) {
    //
    //            Intent intent = new Intent("com.android.camera.action.CROP");
    //            intent.setDataAndType(outputFileUri, "image/*");
    //            intent.putExtra("crop", "true");  //crop = true時就打開裁切畫面
    //            intent.putExtra("aspectX", 1);    //aspectX與aspectY是設定裁切框的比例
    //            intent.putExtra("aspectY", 1);
    //            intent.putExtra("outputX", 150);  //這則是裁切的照片大小
    //            intent.putExtra("outputY", 150);
    //            intent.putExtra("return-data", true);
    //            startActivityForResult(intent, 0);
    //
    //            //            }
    //        } else if (requestCode == 0) {
    //            Bundle extras = data.getExtras();
    //            if (extras != null) {
    //                Bitmap photo = extras.getParcelable("data");
    //
    //                int oldwidth = photo.getWidth();
    //                int oldheight = photo.getHeight();
    //                float scaleWidth = 100 / (float) oldwidth;
    //                float scaleHeight = 100 / (float) oldheight;
    //                Matrix matrix = new Matrix();
    //                matrix.postScale(scaleWidth, scaleHeight);
    //                // create the new Bitmap object
    //                Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, oldwidth,
    //                        oldheight, matrix, true);
    //
    //                if (alertDialog != null) {
    //                    alertDialog.dismiss();
    //                }
    //                userimage.setImageBitmap(resizedBitmap);
    //            }
    //        }
    //    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));

        if (checkLogin.equals("true")) {
            loadPageView();
        } else if (checkLogin.equals("exToke")) {
            new TokenExp().execute();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.message_title))
                    .setMessage(getString(R.string.msg_retokenget))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        getAuthRequest(getActivity(), R.layout.auth_dialog, R.id.webv);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
        }
    }

    public void loadPageView() {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        UserAdapter userAdapter = new UserAdapter(getContext());
        List<User> userList = userAdapter.getAllUser();
        Log.v(TAG + "userList", userList.toString());
        for (User user : userList) {
            try {
                if (user.getUserIDNO() != null
                        && user.getUserIDNO().length() > 0) {
                    userIDNO.setText(user.getUserIDNO());
                } else {
                    userIDNO.setText(getString(R.string.user_message));
                }

                if (user.getName() != null) {
                    userName.setText(user.getName());
                } else {
                    userName.setText(getString(R.string.user_message));
                }

                if (user.getNickname() != null) {
                    userEngName.setText(user.getNickname());
                } else {
                    userEngName.setText(getString(R.string.user_message));
                }

                if (user.getGender().equals("Unknow")) {
                    userSex.setText(getString(R.string.user_message));
                } else if (user.getGender().equals("Male")) {
                    userSex.setText("男性");
                } else if (user.getGender().equals("Female")) {
                    userSex.setText("女性");
                }

                if (user.getBirthday() != null) {
                    userBirthDay.setText(user.getBirthday());
                } else {
                    userBirthDay.setText(getString(R.string.user_message));
                }

                if (user.getUserNationality().equals("0")) {
                    userNationality.setText(getString(R.string.user_message));
                } else if (user.getUserNationality().equals("Taiwan")) {
                    userNationality.setText("台灣");
                }

                if (user.getPhone() != null) {
                    userHomeTEL.setText(user.getPhone());
                } else {
                    userHomeTEL.setText(getString(R.string.user_message));
                }

                if (user.getMobile() != null) {
                    userMobile.setText(user.getMobile());
                } else {
                    userMobile.setText(getString(R.string.user_message));
                }

                if (user.getEmail() != null) {
                    userEMail.setText(user.getEmail());
                } else {
                    userEMail.setText(getString(R.string.user_message));
                }

                if (user.getUserBlood().equals("0")) {
                    userBlood.setText(getString(R.string.user_message));
                } else if (user.getUserBlood().equals("A")) {
                    userBlood.setText("A型");
                } else if (user.getUserBlood().equals("B")) {
                    userBlood.setText("B型");
                } else if (user.getUserBlood().equals("O")) {
                    userBlood.setText("O型");
                } else if (user.getUserBlood().equals("AB")) {
                    userBlood.setText("AB型");
                }

                if (user.getUserRhType().equals("0")) {
                    userRhType.setText(getString(R.string.user_message));
                } else if (user.getUserRhType().equals("Rh+")) {
                    userRhType.setText("Rh陽性");
                } else if (user.getUserRhType().equals("Rh-")) {
                    userRhType.setText("RH陰性");
                }

                if (user.getUserMarried().equals("0")) {
                    userMarried.setText(getString(R.string.user_message));
                } else if (user.getUserMarried().equals("Unmarried")) {
                    userMarried.setText("未婚");
                } else if (user.getUserMarried().equals("Married")) {
                    userMarried.setText("已婚");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pb.dismiss();
    }

    /**
     * GetAuthCode
     */
    private void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(R.string.message_title), getString(R.string.msg_tokenget), true, false);
        auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //去除Dialog標題

        auth_dialog.setContentView(dialogid);
        myBrowser = (WebView) auth_dialog.findViewById(webid);
        webServiceConnection.setBrowserProperty(myBrowser);
        webServiceConnection.setWebviewProperty(myBrowser, getContext());

        String webSiteStr = webServiceConnection.AuthServer + "/" + webServiceConnection.AuthRequest
                + "?response_type=code"
                + "&client_id=" + webServiceConnection.CLIENT_ID
                //                        + "&secret_key=" + CLIENT_SECRET
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

        pb.show();
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
                                .commit();

                        pb.dismiss();
                        loadPageView();

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
                        loadPageView();

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


