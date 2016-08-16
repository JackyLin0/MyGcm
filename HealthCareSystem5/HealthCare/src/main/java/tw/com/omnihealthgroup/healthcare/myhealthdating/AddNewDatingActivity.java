package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodUserDatingDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodUserDating;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;

public class AddNewDatingActivity extends AppCompatActivity {
    private static final String TAG = "AddNewDatingActivity";
    private Button newEventCancel, newEventCommit;
    private EditText add_dating_name;
    private TextView add_dating_number, add_dating_unit, add_dating_type, dating_unit, dating_Heat, dating_carbohydrate, dating_protein, dating_adipose;
    private ImageView photo, del_btn;
    private AlertDialog alertDialog = null;
    private String dating_tpye = null;
    private FoodDefinition foodDefinition;
    private FoodUserDating foodUserDating;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;
    private String eventPK = null, del_PKMark = null;
    private boolean flag_edit = false;
    private Object object = null;
    private long idPK = 0;
    private SeekBar dating_number_seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_add_new);
        Log.v(TAG, "onCreate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤
        prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

        initView(this.getIntent().getExtras());
        initHandler(this.getIntent().getExtras());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        CancelAlertDialog();
    }

    /**
     * 初始化UI元件
     */
    private void initView(Bundle arguments) {
        newEventCancel = (Button) findViewById(R.id.new_event_btn_cancel);
        newEventCancel.setTextColor(getResources().getColor(R.color.white_color));
        newEventCommit = (Button) findViewById(R.id.new_event_btn_commit);
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

        add_dating_name = (EditText) findViewById(R.id.add_dating_name);

        add_dating_unit = (TextView) findViewById(R.id.add_dating_unit);
        add_dating_type = (TextView) findViewById(R.id.add_dating_type);
        add_dating_number = (TextView) findViewById(R.id.add_dating_number);
        dating_unit = (TextView) findViewById(R.id.dating_unit);
        dating_Heat = (TextView) findViewById(R.id.dating_Heat);
        dating_carbohydrate = (TextView) findViewById(R.id.dating_carbohydrate);
        dating_protein = (TextView) findViewById(R.id.dating_protein);
        dating_adipose = (TextView) findViewById(R.id.dating_adipose);

        photo = (ImageView) findViewById(R.id.photo);
        del_btn = (ImageView) findViewById(R.id.del_btn);

        dating_number_seekbar = (SeekBar) findViewById(R.id.dating_number_seekbar);
        //        recycleView = (RecyclerView) findViewById(R.id.recycleView);

    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void initHandler(final Bundle arguments) {
        //取消按鈕
        newEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAlertDialog();
            }
        });

        //完成按鈕
        newEventCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKeyInData();
            }
        });

        //        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //        recycleView.setLayoutManager(layoutManager);
        //
        //        final ArrayList<String> mDataset = new ArrayList<>();
        //        mDataset.add("0");
        //        mDataset.add("0.5");
        //        mDataset.add("1");
        //        mDataset.add("1.5");
        //        mDataset.add("2");
        //        mDataset.add("2.5");
        //        mDataset.add("3");
        //        mDataset.add("3.5");
        //        mDataset.add("4");
        //        mDataset.add("4.5");
        //        mDataset.add("5");
        //
        //        datingUnitAdapter = new DatingUnitAdapter(AddNewDatingActivity.this, mDataset);
        //        recycleView.setAdapter(datingUnitAdapter);
        //
        //        recycleView.addOnItemTouchListener(new RecyclerItemClickListener(AddNewDatingActivity.this,
        //                new RecyclerItemClickListener.OnItemClickListener() {
        //                    @Override
        //                    public void onItemClick(View view, int position) {
        //                        add_dating_number.setText(mDataset.get(position));
        //                    }
        //
        //                    @Override
        //                    public void onLongClick(View view, int posotion) {
        //                        Log.d(TAG, "onLongClick position : " + posotion);
        //                    }
        //                }));
        //
        //        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //                super.onScrollStateChanged(recyclerView, newState);
        //            }
        //
        //            @Override
        //            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //                super.onScrolled(recyclerView, dx, dy);
        //
        //                int loadPos = recyclerView.getAdapter().getItemCount();
        //                Log.v(TAG, String.valueOf(loadPos));
        //
        //            }
        //        });

        if (arguments.getSerializable("foodUserDating") != null) {
            flag_edit = true;

            foodUserDating = (FoodUserDating) arguments.getSerializable("foodUserDating");
            idPK = foodUserDating.get_id();
            Log.v(TAG, String.valueOf(idPK));

            dating_tpye = foodUserDating.getMealtype();

            add_dating_name.setText(foodUserDating.getName());
            add_dating_unit.setText(foodUserDating.getUnit());
            add_dating_number.setText(foodUserDating.getAmount());
            add_dating_number.setTextColor(getResources().getColor(R.color.defTextColor));

            dating_unit.setText(foodUserDating.getAmountunit());
            dating_Heat.setText(foodUserDating.getMoisture());
            dating_protein.setText(foodUserDating.getProtein());
            dating_adipose.setText(foodUserDating.getFat());
            dating_carbohydrate.setText(foodUserDating.getSugar());

            switch (foodUserDating.getRefimgsn()) {
                case "a":
                    photo.setImageResource(R.mipmap.dating_unit_a);
                    break;
                case "b":
                    photo.setImageResource(R.mipmap.dating_unit_b);
                    break;
                case "c":
                    photo.setImageResource(R.mipmap.dating_unit_c);
                    break;
                case "d":
                    photo.setImageResource(R.mipmap.dating_unit_d);
                    break;
                case "e":
                    photo.setImageResource(R.mipmap.dating_unit_e);
                    break;
                case "f":
                    photo.setImageResource(R.mipmap.dating_unit_f);
                    break;
                case "null":
                    photo.setImageResource(R.mipmap.dating_unit_null);
                    break;
            }
            dating_number_seekbar.setMax(10);
            switch (foodUserDating.getAmount()) {
                case "0":
                    dating_number_seekbar.setProgress(0);
                    break;
                case "0.5":
                    dating_number_seekbar.setProgress(1);
                    break;
                case "1":
                    dating_number_seekbar.setProgress(2);
                    break;
                case "1.5":
                    dating_number_seekbar.setProgress(3);
                    break;
                case "2":
                    dating_number_seekbar.setProgress(4);
                    break;
                case "2.5":
                    dating_number_seekbar.setProgress(5);
                    break;
                case "3":
                    dating_number_seekbar.setProgress(6);
                    break;
                case "3.5":
                    dating_number_seekbar.setProgress(7);
                    break;
                case "4":
                    dating_number_seekbar.setProgress(8);
                    break;
                case "4.5":
                    dating_number_seekbar.setProgress(9);
                    break;
                case "5":
                    dating_number_seekbar.setProgress(10);
                    break;
            }

            add_dating_type.setText(foodUserDating.getCooktype());
            add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));

        } else {
            if (arguments.getString("dating_tpye") != null) {
                dating_tpye = arguments.getString("dating_tpye");
                Log.v(TAG, dating_tpye);
            }

            if (arguments.getSerializable("FoodDefinitionDAO") != null) {
                foodDefinition = (FoodDefinition) arguments.getSerializable("FoodDefinitionDAO");

                add_dating_name.setText(foodDefinition.getName());
                add_dating_unit.setText(foodDefinition.getUnit());
                add_dating_number.setText("0");
                add_dating_number.setTextColor(getResources().getColor(R.color.defTextColor));

                dating_number_seekbar.setMax(10);
                dating_number_seekbar.setProgress(0);

                dating_unit.setText(foodDefinition.getAmountunit());
                dating_Heat.setText(foodDefinition.getMoisture());
                dating_protein.setText(foodDefinition.getProtein());
                dating_adipose.setText(foodDefinition.getFat());
                dating_carbohydrate.setText(foodDefinition.getSugar());

                switch (foodDefinition.getRefimgsn()) {
                    case "a":
                        photo.setImageResource(R.mipmap.dating_unit_a);
                        break;
                    case "b":
                        photo.setImageResource(R.mipmap.dating_unit_b);
                        break;
                    case "c":
                        photo.setImageResource(R.mipmap.dating_unit_c);
                        break;
                    case "d":
                        photo.setImageResource(R.mipmap.dating_unit_d);
                        break;
                    case "e":
                        photo.setImageResource(R.mipmap.dating_unit_e);
                        break;
                    case "f":
                        photo.setImageResource(R.mipmap.dating_unit_f);
                        break;
                    case "null":
                        photo.setImageResource(R.mipmap.dating_unit_null);
                        break;
                }
            }

        }

        dating_number_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //拖動時的動作
                switch (progress) {
                    case 0:
                        add_dating_number.setText("0");
                        break;
                    case 1:
                        add_dating_number.setText("0.5");
                        break;
                    case 2:
                        add_dating_number.setText("1");
                        break;
                    case 3:
                        add_dating_number.setText("1.5");
                        break;
                    case 4:
                        add_dating_number.setText("2");
                        break;
                    case 5:
                        add_dating_number.setText("2.5");
                        break;
                    case 6:
                        add_dating_number.setText("3");
                        break;
                    case 7:
                        add_dating_number.setText("3.5");
                        break;
                    case 8:
                        add_dating_number.setText("4");
                        break;
                    case 9:
                        add_dating_number.setText("4.5");
                        break;
                    case 10:
                        add_dating_number.setText("5");
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //按下時的動作
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //放開時的動作

            }
        });

        //        add_dating_unit.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //
        //                Bundle bundle = new Bundle();
        //
        //                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
        //                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
        //                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
        //
        //                DialogFragment dialogFragment = new DatingUnitFragment();
        //                dialogFragment.setArguments(bundle);
        //                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
        //                dialogFragment.show(AddNewDatingActivity.this.getSupportFragmentManager(), "DatingUnitFragment");
        //
        //            }
        //        });

        add_dating_type.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.type_dating);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(AddNewDatingActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                switch (name) {
                                    case "蒸":
                                        add_dating_type.setText(eventType[0]);
                                        add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));
                                        break;
                                    case "滷":
                                        add_dating_type.setText(eventType[1]);
                                        add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));
                                        break;
                                    case "炒":
                                        add_dating_type.setText(eventType[2]);
                                        add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));
                                        break;
                                    case "煎/炸":
                                        add_dating_type.setText(eventType[3]);
                                        add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));
                                        break;
                                    case "未進行其他烹調加工":
                                        add_dating_type.setText(eventType[4]);
                                        add_dating_type.setTextColor(getResources().getColor(R.color.defTextColor));
                                        break;
                                }
                            }
                        }).show();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));

                Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                bundle.putParcelable("bitmap", bitmap);

                DialogFragment dialogFragment = new DatingImageFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(AddNewDatingActivity.this.getSupportFragmentManager(), "DatingImageFragment");

            }
        });

        if (flag_edit == true) {
            del_btn.setVisibility(View.VISIBLE);
            del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(AddNewDatingActivity.this)
                            .setTitle(getString(R.string.message_title))
                            .setMessage(getString(R.string.user_del_dating))
                            .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewDatingActivity.this
                                            , getString(R.string.webview_loading_title)
                                            , getString(R.string.msg_tokenget), false, true);
                                    pb.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    Log.i(TAG, "manual upload data");
                                                    FoodUserDatingDAO foodUserDatingDAO = new FoodUserDatingDAO(AddNewDatingActivity.this);
                                                    if (foodUserDating.toString().length() > 1) {
                                                        foodUserDatingDAO.delete_ID(foodUserDating);

                                                        Looper.prepare();
                                                        Toast.makeText(AddNewDatingActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                        pb.dismiss();
                                                        finish();
                                                        Looper.loop();
                                                        break;
                                                    } else {
                                                        Looper.prepare();
                                                        Toast.makeText(AddNewDatingActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    //                                                    JSONObject reUpdateResponse = webServiceConnection.delCaleinfo(prf.getString("access_token", ""), eventPK);
                                                    //                                                    //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                    //                                                    if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    //                                                        // 更新sql lite資料庫
                                                    //                                                        ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
                                                    //                                                        scheduleDAO.delete_MsgPk(del_PKMark, object);
                                                    //                                                        Looper.prepare();
                                                    //                                                        Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    //
                                                    //                                                        //                                                            flag = "del";
                                                    //                                                        //                                                            Fragment fragment = new MyHealthCalendarFragment();
                                                    //                                                        //
                                                    //                                                        //                                                            getChildFragmentManager().beginTransaction()
                                                    //                                                        //                                                                    .replace(R.id.dialog_main, fragment)
                                                    //                                                        //                                                                            //                                .addToBackStack(null)
                                                    //                                                        //                                                                    .commit();
                                                    //                                                        //
                                                    //                                                        //                                                            //                                            dismiss();
                                                    //                                                        //
                                                    //                                                        //                                                            onDestroyView();
                                                    //                                                        pb.dismiss();
                                                    //                                                        finish();
                                                    //                                                        Looper.loop();
                                                    //                                                        break;
                                                    //                                                    } else {
                                                    //                                                        Looper.prepare();
                                                    //                                                        Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    //                                                        Looper.loop();
                                                    //                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //                                        dialog.dismiss();
                                    return;
                                }
                            }).show();
                }
            });
        }
    }

    private void saveKeyInData() {
        if (add_dating_name.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddNewDatingActivity.this, getResources().getString(R.string.add_dating_name_message));
        } else if (add_dating_type.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddNewDatingActivity.this, getResources().getString(R.string.add_dating_type_message));
        } else if (add_dating_number.getText().toString().trim().equals("")
                || add_dating_number.getText().toString().trim().equals("0")) {
            webServiceConnection.editTextAlertDialog(AddNewDatingActivity.this, getResources().getString(R.string.add_dating_amount_message));
        } else {
            FoodUserDatingDAO foodUserDatingDAO = new FoodUserDatingDAO(AddNewDatingActivity.this);
            FoodUserDating foodUserDating_new = new FoodUserDating();

            String timeStr = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                Date date = format.parse(prf.getString("day_dating", "").toString() + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
                timeStr = format.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            foodUserDating_new.setCooktype(add_dating_type.getText().toString().trim());
            foodUserDating_new.setAmount(add_dating_number.getText().toString().trim());
            foodUserDating_new.setMealtype(dating_tpye);
            foodUserDating_new.setImage("null");
            foodUserDating_new.setCrTime(timeStr);

            if (flag_edit == true) {
                foodUserDating_new.set_id(idPK);
                foodUserDating_new.setType(foodUserDating.getType());
                if (add_dating_name.getText().toString().trim().equals(foodUserDating.getName())) {
                    foodUserDating_new.setName(foodUserDating.getName());
                } else {
                    foodUserDating_new.setName(add_dating_name.getText().toString().trim());
                }
                foodUserDating_new.setContent(foodUserDating.getContent());
                foodUserDating_new.setUnit(foodUserDating.getUnit());
                foodUserDating_new.setAmountunit(foodUserDating.getAmountunit());

                DecimalFormat nf = new DecimalFormat("0.00");
                double e000i = Double.parseDouble(add_dating_number.getText().toString().trim());
                double e001i = Double.parseDouble(foodUserDating.getMoisture());
                double e002i = Double.parseDouble(foodUserDating.getProtein());
                double e003i = Double.parseDouble(foodUserDating.getFat());
                double e004i = Double.parseDouble(foodUserDating.getSugar());

                double Moisture = e001i * e000i;
                double Protein = e002i * e000i;
                double getFat = e003i * e000i;
                double getSugar = e004i * e000i;

                switch (add_dating_type.getText().toString().trim()) {
                    case "蒸":
                        getFat = getFat + 0f;
                        break;
                    case "滷":
                        getFat = getFat + 5f;
                        break;
                    case "炒":
                        getFat = getFat + 10f;
                        break;
                    case "煎/炸":
                        getFat = getFat + 15f;
                        break;
                    case "未進行其他烹調加工":
                        getFat = getFat + 0f;
                        break;
                }

                foodUserDating_new.setMoisturesum(nf.format(Moisture));
                foodUserDating_new.setProteinsum(nf.format(Protein));
                foodUserDating_new.setFatsum(nf.format(getFat));
                foodUserDating_new.setSugarsum(nf.format(getSugar));

                foodUserDating_new.setRefimgsn(foodUserDating.getRefimgsn());
                foodUserDating_new.setMoisture(foodUserDating.getMoisture());
                foodUserDating_new.setProtein(foodUserDating.getProtein());
                foodUserDating_new.setFat(foodUserDating.getFat());
                foodUserDating_new.setSugar(foodUserDating.getSugar());
                foodUserDating_new.setNote(foodUserDating.getNote());
                foodUserDating_new.setStatus(foodUserDating.getStatus());
                foodUserDating_new.setMdTime(foodUserDating.getCrTime());

                foodUserDatingDAO.update(foodUserDating_new);


            } else {
                foodUserDating_new.setType(foodDefinition.getType());
                if (add_dating_name.getText().toString().trim().equals(foodDefinition.getName())) {
                    foodUserDating_new.setName(foodDefinition.getName());
                } else {
                    foodUserDating_new.setName(add_dating_name.getText().toString().trim());
                }
                foodUserDating_new.setContent(foodDefinition.getContent());
                foodUserDating_new.setUnit(foodDefinition.getUnit());
                foodUserDating_new.setAmountunit(foodDefinition.getAmountunit());

                DecimalFormat nf = new DecimalFormat("0.00");
                double e000i = Double.parseDouble(add_dating_number.getText().toString().trim());
                double e001i = Double.parseDouble(foodDefinition.getMoisture());
                double e002i = Double.parseDouble(foodDefinition.getProtein());
                double e003i = Double.parseDouble(foodDefinition.getFat());
                double e004i = Double.parseDouble(foodDefinition.getSugar());

                double Moisture = e001i * e000i;
                double Protein = e002i * e000i;
                double getFat = e003i * e000i;
                double getSugar = e004i * e000i;

                switch (add_dating_type.getText().toString().trim()) {
                    case "蒸":
                        getFat = getFat + 0f;
                        break;
                    case "滷":
                        getFat = getFat + 5f;
                        break;
                    case "炒":
                        getFat = getFat + 10f;
                        break;
                    case "煎/炸":
                        getFat = getFat + 15f;
                        break;
                    case "未進行其他烹調加工":
                        getFat = getFat + 0f;
                        break;
                }

                foodUserDating_new.setMoisturesum(nf.format(Moisture));
                foodUserDating_new.setProteinsum(nf.format(Protein));
                foodUserDating_new.setFatsum(nf.format(getFat));
                foodUserDating_new.setSugarsum(nf.format(getSugar));

                foodUserDating_new.setRefimgsn(foodDefinition.getRefimgsn());
                foodUserDating_new.setMoisture(foodDefinition.getMoisture());
                foodUserDating_new.setProtein(foodDefinition.getProtein());
                foodUserDating_new.setFat(foodDefinition.getFat());
                foodUserDating_new.setSugar(foodDefinition.getSugar());
                foodUserDating_new.setNote(foodDefinition.getNote());
                foodUserDating_new.setStatus(foodDefinition.getStatus());
                foodUserDating_new.setMdTime(foodDefinition.getCrTime());

                foodUserDatingDAO.insert(foodUserDating_new);
            }

            new AlertDialog.Builder(AddNewDatingActivity.this)
                    .setTitle(getString(R.string.message_title))
                    .setMessage(getString(R.string.user_save_dating))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewDatingActivity.this
                                            , getString(R.string.webview_loading_title)
                                            , getString(R.string.msg_tokenget), false, true);
                                    pb.show();

                                    //                                //        final NetService netService = new NetService();
                                    //                                final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                    //                                new Thread(new Runnable() {
                                    //                                    @Override
                                    //                                    public void run() {
                                    //                                        try {
                                    //                                            while (true) {
                                    //                                                Log.i(TAG, "manual upload data");
                                    //                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                    //                                                JSONObject reUpdateResponse = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, false);
                                    //                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                    //                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                    //                                                    // 更新sql lite資料庫
                                    //                                                    bioDataAdapter.updataUploaded(listBioData);
                                    //                                                    //                                                    Log.i(TAG, "資料重新上傳成功");
                                    //                                                    Looper.prepare();
                                    //                                                    Toast.makeText(AddMeasureBodyActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                    //                                                    Looper.loop();
                                    //                                                    break;
                                    //                                                } else {
                                    //                                                    Looper.prepare();
                                    //                                                    Toast.makeText(AddMeasureBodyActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                    //                                                    Looper.loop();
                                    //                                                }
                                    //                                            }
                                    //                                        } catch (Exception e) {
                                    //                                            e.printStackTrace();
                                    //                                        }
                                    //                                    }
                                    //                                }).start();

                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }).show();
        }
    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewDatingActivity.this);
        dialogBuilder
                .setTitle(getResources().getString(R.string.new_dating_cancel_title))
                .setMessage(getResources().getString(R.string.new_event_cancel_message))
                .setPositiveButton(
                        getResources().getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消新增私人行程回到行事曆檢視畫面
                                finish();
                            }
                        })
                .setNeutralButton(
                        getResources().getString(R.string.msg_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //關閉提醒視窗回到新增私人行程
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.show();
        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
        alertMessageText.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

}
