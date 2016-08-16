package tw.com.omnihealthgroup.healthcare.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkLayoutFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthactivity.MyHealthActivityFragment;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.MyHealthCalendarFragment;
import tw.com.omnihealthgroup.healthcare.myhealthcare.MyHealthCareFragment;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcommunity.MyHealthCommunityFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.MyHealthDatingFragment;
import tw.com.omnihealthgroup.healthcare.myhealthvideo.MyHealthVideoFragment;
import tw.com.omnihealthgroup.healthcare.mysystemsettings.MySystemSettingsFragment;

public class MainFragment extends DrawerFrameworkLayoutFragment {
    private static String TAG = "MainFragment";
    private String UserName = null;

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v(TAG, "onViewStateRestored");

        //        if (savedInstanceState != null) return;
        //        DrawerLayout drawerLayout = getView();
        //        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onSetDrawerMain(ViewGroup mainView) {
        super.onSetDrawerMain(mainView);
        Log.v(TAG, "onSetDrawerMain");

        //        String kindCode = getArguments().getString("kindcode");
        //        String msgPk = getArguments().getString("msgpk");
        //        Bundle bundle = new Bundle();
        //        bundle.putString("kindcode", kindCode);
        //        bundle.putString("msgpk", msgPk);

        Fragment defultFragment;
        //        if (kindCode != null) {
        //            switch (kindCode) {
        //                case "M":   //新藥通知
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "S":   //體系推播
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "H":   //健保法規
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "D":   //行政公告
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "E":   //秘書叮嚀
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "B":   //營運焦點
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //                case "C":   //會診排程
        //                    defultFragment = new MyHealthCareFragment();
        //                    defultFragment.setArguments(bundle);
        //                    break;
        //                case "O":   //手術排程
        //                    defultFragment = new MyHealthCareFragment();
        //                    defultFragment.setArguments(bundle);
        //                    break;
        //
        //                default:    //預設: 行事曆
        //                    defultFragment = new MyHealthCareFragment();
        //                    break;
        //            }
        //        } else {
        //            defultFragment = new ScheduleFragment();
        defultFragment = new MyHealthCareFragment();
        //        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(getDrawerMainResId(), defultFragment)
                .commit();
    }

    @Override
    protected void onSetMenuHeader(View headerView) {
        super.onSetMenuHeader(headerView);
        Log.v(TAG, "onSetMenuHeader");

        //        CharSequence acc = MyAccountManager.GetAccountName();
        //        CharSequence name = MyAccountManager.GetHumanName();
        //        Employee employee = MyAccountManager.GetInfo();
        String time;

        UserAdapter userAdapter = new UserAdapter(getContext());
        List<User> userList = userAdapter.getAllUser();
        Log.v(TAG + "userList", userList.toString());
        for (User user : userList) {
            Log.v(TAG + "userName", user.getName().toString());
            UserName = user.getName();
        }

        try {
            String timeStr = new SimpleDateFormat("HH").format(new Date());

            int timeInt = Integer.parseInt(timeStr);
            if (timeInt >= 5 && timeInt < 12) {
                time = "早安";
            } else if (timeInt >= 12 && timeInt < 17) {
                time = "午安";
            } else {
                time = "晚安";
            }

            View v;

            //            if (employee != null && employee.empNo != null && employee.hasPic) {
            //                ((OvalButton) headerView.findViewById(android.R.id.icon))
            //                        .setImageURI(ShowContactWebReference.EMPLOYEE_PIC_LARGE_URL + employee.empNo);
            //
            //                if ((v = headerView.findViewById(android.R.id.icon)) instanceof TextView) {
            //                    ((TextView) v).setText(name);
            //                }
            //
            //                if ((v = headerView.findViewById(android.R.id.title)) instanceof TextView) {
            //                    ((TextView) v).setText(Html.fromHtml(
            //                            // getString(R.string.nv_header_title, time, name, employee == null ? acc : employee.getEmpJobTitleString())));
            //                            getString(R.string.nv_header_title, time, name, getResources().getString(R.string.message))));
            //
            //                    ((TextView) v).setTextSize(20);
            //                }
            //            } else {

            if ((v = headerView.findViewById(android.R.id.icon)) instanceof ImageView) {
                v.setVisibility(View.GONE);
            }

            if ((v = headerView.findViewById(android.R.id.title)) instanceof TextView) {
                ((TextView) v).setText(Html.fromHtml(
                        // getString(R.string.nv_header_title, time, name, employee == null ? acc : employee.getEmpJobTitleString())));
                        getString(R.string.nv_header_title, time, UserName, getResources().getString(R.string.message))));

                v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            //            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Log.v(TAG, "onNavigationItemSelected");

        Fragment fragment = null;
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.navigation_item_1: // 我的健康管理
                fragment = new MyHealthCareFragment();
                // Bundle scheduleBundle = new Bundle();
                // scheduleBundle.putCharSequence(LobbyFragment.KEY_ACC, MyAccountManager.GetAccountName());
                break;

            case R.id.navigation_item_2: // 我的健康行事曆
                fragment = new MyHealthCalendarFragment();
                // Bundle newsBundle = new Bundle();
                // newsBundle.putCharSequence(LobbyFragment.KEY_ACC, MyAccountManager.GetAccountName());
                break;

            case R.id.navigation_item_3: // 我的健康飲食
                fragment = new MyHealthDatingFragment();
                // Bundle myPatientsBundle = new Bundle();
                // myPatientsBundle.putString("USERACCOUNT", getArguments().getString("USERACCOUNT"));
                // fragment.setArguments(myPatientsBundle);
                break;

            case R.id.navigation_item_4: // 我的健康運動
                fragment = new MyHealthActivityFragment();
                // Bundle lobbyBundle = new Bundle();
                // lobbyBundle.putCharSequence(LobbyFragment.KEY_ACC, MyAccountManager.GetAccountName());
                // lobbyBundle.putCharSequence(LobbyFragment.KEY_PWD, getArguments().getString("USERPASSWORD"));
                // lobbyBundle.putCharSequence(LobbyFragment.KEY_NAME, MyAccountManager.GetHumanName());
                // lobbyBundle.putParcelable(LobbyFragment.KEY_INFO, MyAccountManager.GetInfo());
                // fragment.setArguments(lobbyBundle);
                break;

            case R.id.navigation_item_5: // 我的影音衛教
                fragment = new MyHealthVideoFragment();
                // Bundle eFormBundle = new Bundle();
                // eFormBundle.putCharSequence("USERACCOUNT", getArguments().getString("USERACCOUNT"));
                // fragment.setArguments(eFormBundle);
                break;

            case R.id.navigation_item_6: // 我的健康社群
                fragment = new MyHealthCommunityFragment();
                break;

            case R.id.navigation_item_7: // 系統設定
                fragment = new MySystemSettingsFragment();
                break;

            case R.id.navigation_item_8: // 登出
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.message_title))
                        .setMessage(getString(R.string.msg_logout_confirm))
                        .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE).edit().clear().commit();
                                Fragment fragment = new LoginFragment();

                                if (fragment != null) {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, fragment)
                                            .commit();
                                }

                            }
                        })
                        .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();


                //                ConfirmAlertDialog confirmAlertDialog = ConfirmAlertDialog
                //                        .newInstance(R.drawable.ic_launcher2,
                //                                getString(R.string.msg_logout_confirm), null,
                //                                getString(android.R.string.ok),
                //                                getString(android.R.string.cancel));
                //
                //                confirmAlertDialog.setOnDialogConfirmListener(new OnDialogConfirmListener() {
                //                    @Override
                //                    public void onPositiveClick(DialogInterface dialog) {
                //                        getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE).edit().clear().commit();
                //                        Fragment fragment = new LoginFragment();
                //
                //                        if (fragment != null) {
                //                            getFragmentManager()
                //                                    .beginTransaction()
                //                                    .replace(R.id.container, fragment)
                //                                    .commit();
                //                        }
                //                    }
                //
                //                    @Override
                //                    public void onNegativeClick(DialogInterface dialog) {
                //                        dialog.dismiss();
                //                    }
                //
                //                    @Override
                //                    public void onListItemClick(DialogInterface dialog, int position) {
                //                    }
                //                });
                //
                //                confirmAlertDialog.show(getFragmentManager(), TAG);

                break;
        }

        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(getDrawerMainResId(), fragment)
                    .commit();
        }
        return super.onNavigationItemSelected(menuItem);
    }

}
