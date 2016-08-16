package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.EventClassifyData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;

public class DayViewAdapter extends BaseAdapter {
    private static final String TAG = "DayViewAdapter";
    private LayoutInflater adapterLayoutInflater;
    private EventClassifyData eventClassifyData;

    private ArrayList<Integer> monthDayList;
    private ArrayList<String> monthWeekDayList;

    public DayViewAdapter(Context c) {
        adapterLayoutInflater = LayoutInflater.from(c);
        eventClassifyData = EventClassifyData.EVENT_CLASSIFY_DATA;
        eventClassifyData.setCalendarData();
        monthDayList = new ArrayList<>();
        monthWeekDayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return eventClassifyData.howManyDaysInMonth();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (EventClassifyData.DAYVIEW_NOTIFY_CHANGED) {
            eventClassifyData.setCalendarData();
            monthDayList.clear();
            monthWeekDayList.clear();
            EventClassifyData.DAYVIEW_NOTIFY_CHANGED = false;
        }
        Resources getWeekResource = adapterLayoutInflater.getContext().getResources();
        eventClassifyData.setCalendarWeek(getWeekResource);
        eventClassifyData.setCalendarDay();
        monthDayList.add(eventClassifyData.getCalendarDay());
        monthWeekDayList.add(eventClassifyData.getCalendarWeek());

        ViewHolder dayViewHolder;
        dayViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = adapterLayoutInflater.inflate(R.layout.dayview_list, parent, false);
            dayViewHolder.dateOfWeekWord = (TextView) convertView.findViewById(R.id.dayview_date_word);
            dayViewHolder.dateOfMonthNum = (TextView) convertView.findViewById(R.id.dayview_date_num);
            dayViewHolder.eventGreen = (LinearLayout) convertView.findViewById(R.id.event_green);
            dayViewHolder.eventPurple = (LinearLayout) convertView.findViewById(R.id.event_purple);
            dayViewHolder.eventYellow = (LinearLayout) convertView.findViewById(R.id.event_yellow);
            dayViewHolder.eventBlue = (LinearLayout) convertView.findViewById(R.id.event_blue);
            dayViewHolder.eventGraceGreen = (LinearLayout) convertView.findViewById(R.id.event_gracegreen);
            dayViewHolder.eventGraceBlue = (LinearLayout) convertView.findViewById(R.id.event_graceblue);
            dayViewHolder.eventNone = (LinearLayout) convertView.findViewById(R.id.event_none);

            dayViewHolder.eventGreenText = (TextView) convertView.findViewById(R.id.dayview_event_green_text);
            dayViewHolder.eventPurpleText = (TextView) convertView.findViewById(R.id.dayview_event_purple_text);
            dayViewHolder.eventYellowText = (TextView) convertView.findViewById(R.id.dayview_event_yellow_text);
            dayViewHolder.eventBlueText = (TextView) convertView.findViewById(R.id.dayview_event_blue_text);
            dayViewHolder.eventGraceGreenText = (TextView) convertView.findViewById(R.id.dayview_event_gracegreen_text);
            dayViewHolder.eventGraceBlueText = (TextView) convertView.findViewById(R.id.dayview_event_graceblue_text);

            convertView.setTag(dayViewHolder);
        } else {
            dayViewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            // 寫入星期與日期，並在當天日期加上顏色
            if ((eventClassifyData.getToolBarYear() == Calendar.getInstance().get(Calendar.YEAR))
                    && (eventClassifyData.getToolBarMonth() == Calendar.getInstance().get(Calendar.MONTH) + 1)
                    && (monthDayList.get(position) == Calendar.getInstance().get(Calendar.DATE))) {
                // 橘色
                dayViewHolder.dateOfMonthNum.setTextColor(0xfffd6334);
                dayViewHolder.dateOfWeekWord.setTextColor(0xfffd6334);
            } else {
                // 白色
                dayViewHolder.dateOfMonthNum.setTextColor(0xffffffff);
                dayViewHolder.dateOfWeekWord.setTextColor(0xffffffff);
            }
            dayViewHolder.dateOfMonthNum.setText(String.valueOf(monthDayList.get(position)));   // 寫入日期
            dayViewHolder.dateOfWeekWord.setText(String.valueOf(monthWeekDayList.get(position)));   // 寫入星期

            // 判斷要顯示的事件類型
            LinearLayout[] eventVisibilityArray = new LinearLayout[]{
                    dayViewHolder.eventNone, dayViewHolder.eventGreen,
                    dayViewHolder.eventPurple, dayViewHolder.eventYellow,
                    dayViewHolder.eventBlue, dayViewHolder.eventGraceGreen,
                    dayViewHolder.eventGraceBlue
            };
            eventClassifyData.chooseDayEventClassify(eventVisibilityArray);

            TextView[] dayEventTextArray = new TextView[]{
                    dayViewHolder.eventGreenText, dayViewHolder.eventPurpleText,
                    dayViewHolder.eventYellowText, dayViewHolder.eventBlueText,
                    dayViewHolder.eventGraceGreenText, dayViewHolder.eventGraceBlueText
            };

            //初始化各類別事件的內容
            for (int i = 0; i < dayEventTextArray.length; i++) {
                dayEventTextArray[i].setText("");
            }

            //組合今天的日期用於查詢有無事件
            StringBuilder todayTimeString = new StringBuilder();
            todayTimeString.append(eventClassifyData.getToolBarYear());
            if (eventClassifyData.getToolBarMonth() < 10) {
                todayTimeString.append("0");
            }
            todayTimeString.append(eventClassifyData.getToolBarMonth());
            if (monthDayList.get(position) < 10) {
                todayTimeString.append("0");
            }
            todayTimeString.append(monthDayList.get(position));

            //取得今日事件
            getDayEventData(convertView, todayTimeString.toString(), dayEventTextArray);

            // 紀錄日期、星期、position
            convertView.setTag(R.id.dayevent_date, monthDayList.get(position));
            convertView.setTag(R.id.dayevent_day_in_week, monthWeekDayList.get(position));
            convertView.setTag(R.id.dayevent_position, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * 取得並顯示每日的事件內容
     *
     * @param convertView       用於DB確認的view
     * @param todayTime         今天的時間
     * @param dayEventTextArray 類別事件的TextView物件陣列
     */
    public void getDayEventData(View convertView, String todayTime, TextView[] dayEventTextArray) {
        int table = eventClassifyData.getEventClassify();
        ScheduleDAO scheduleDAO = new ScheduleDAO(convertView.getContext());
        StringBuilder dayEventTextBuilder = new StringBuilder();
        if (table == 0) {
            for (int i = 0; i < ScheduleDAO.TABLE_ARRAY.length; i++) {
                List<Object> searchDayEventList = scheduleDAO.getDayEvent(i, todayTime);
                switch (ScheduleDAO.TABLE_ARRAY[i]) {
                    case ScheduleDAO.TABLE_CLINIC:
                        dayEventTextBuilder.append(dayEventTextArray[0].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Clinic clinic = (Clinic) searchDayEventList.get(j);
                            //時間 + 科別
                            //                            dayEventTextBuilder.append("上午 08：00    ");
                            //                            dayEventTextBuilder.append(clinic.getClinicDate());
                            //                            dayEventTextBuilder.append("\n");
                            dayEventTextBuilder.append(clinic.getClinicStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(clinic.getClinicName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[0].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;

                    case ScheduleDAO.TABLE_CONSULTATION:
                        dayEventTextBuilder.append(dayEventTextArray[1].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Consultation consultation = (Consultation) searchDayEventList.get(j);
                            //時間 + 會診科別
                            //                            dayEventTextBuilder.append("上午 08：00    ");
                            //                            dayEventTextBuilder.append(consultation.getConsultationDivision().replace(" ", ""));
                            //                            dayEventTextBuilder.append("\n");
                            dayEventTextBuilder.append(consultation.getConsultationStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(consultation.getConsultationName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[1].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;

                    case ScheduleDAO.TABLE_OPERATE:
                        dayEventTextBuilder.append(dayEventTextArray[2].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Operation operation = (Operation) searchDayEventList.get(j);
                            //時間 + 科別
                            //                            dayEventTextBuilder.append("上午 08：00    ");
                            //                            dayEventTextBuilder.append(operation.getOperateDivision().replace(" ", ""));
                            //                            dayEventTextBuilder.append("\n");
                            dayEventTextBuilder.append(operation.getOperateStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(operation.getOperateName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[2].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;

                    case ScheduleDAO.TABLE_MEETING:
                        dayEventTextBuilder.append(dayEventTextArray[3].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Meeting meeting = (Meeting) searchDayEventList.get(j);
                            //時間 + 科別
                            //                            dayEventTextBuilder.append("上午 08：00    ");
                            //                            dayEventTextBuilder.append(meeting.getMeetingDate());
                            //                            dayEventTextBuilder.append("\n");
                            dayEventTextBuilder.append(meeting.getMeetingStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(meeting.getMeetingName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[3].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;

                    case ScheduleDAO.TABLE_PERSONAL:
                        dayEventTextBuilder.append(dayEventTextArray[4].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Personal personal = (Personal) searchDayEventList.get(j);
                            //時間 + 科別
                            dayEventTextBuilder.append(personal.getPersonalStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(personal.getPersonalName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[4].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;

                    case ScheduleDAO.TABLE_EXAMINATION:
                        dayEventTextBuilder.append(dayEventTextArray[5].getText());
                        for (int j = 0; j < searchDayEventList.size(); j++) {
                            Examination examination = (Examination) searchDayEventList.get(j);
                            //時間 + 科別
                            dayEventTextBuilder.append(examination.getExaminationStartTime());
                            dayEventTextBuilder.append("    ");
                            dayEventTextBuilder.append(examination.getExaminationName());
                            dayEventTextBuilder.append("\n");
                        }
                        dayEventTextArray[5].setText(dayEventTextBuilder);
                        dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                        break;
                }
            }
        } else {
            List<Object> searchDayEventList = scheduleDAO.getDayEvent(table, todayTime);
            switch (table) {
                case 1:
                    dayEventTextBuilder.append(dayEventTextArray[0].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        Clinic clinic = (Clinic) searchDayEventList.get(i);
                        //時間 + 科別
                        //                        dayEventTextBuilder.append("上午 08：00    ");
                        //                        dayEventTextBuilder.append(clinic.getClinicDate());
                        //                        dayEventTextBuilder.append("\n");
                        dayEventTextBuilder.append(clinic.getClinicStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(clinic.getClinicName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[0].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;

                case 2:
                    dayEventTextBuilder.append(dayEventTextArray[1].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        Consultation consultation = (Consultation) searchDayEventList.get(i);
                        //時間 + 會診科別
                        //                        dayEventTextBuilder.append("上午 08：00    ");
                        //                        dayEventTextBuilder.append(consultation.getConsultationDivision().replace(" ", ""));
                        //                        dayEventTextBuilder.append("\n");
                        dayEventTextBuilder.append(consultation.getConsultationStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(consultation.getConsultationName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[1].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;

                case 3:
                    dayEventTextBuilder.append(dayEventTextArray[2].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        Operation operation = (Operation) searchDayEventList.get(i);
                        //時間 + 科別
                        //                        dayEventTextBuilder.append("上午 08：00    ");
                        //                        dayEventTextBuilder.append(operation.getOperateDivision().replace(" ", ""));
                        //                        dayEventTextBuilder.append("\n");
                        dayEventTextBuilder.append(operation.getOperateStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(operation.getOperateName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[2].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;

                case 4:
                    dayEventTextBuilder.append(dayEventTextArray[3].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        Meeting meeting = (Meeting) searchDayEventList.get(i);
                        //時間 + 科別
//                        dayEventTextBuilder.append("上午 08：00    ");
//                        dayEventTextBuilder.append(meeting.getMeetingDate());
//                        dayEventTextBuilder.append("\n");
                        dayEventTextBuilder.append(meeting.getMeetingStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(meeting.getMeetingName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[3].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;

                case 5:
                    dayEventTextBuilder.append(dayEventTextArray[4].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        //TODO 顯示總覽時會在這邊爆out fo index exception
                        Personal personal = (Personal) searchDayEventList.get(i);
                        //時間 + 科別
                        dayEventTextBuilder.append(personal.getPersonalStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(personal.getPersonalName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[4].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;

                case 6:
                    dayEventTextBuilder.append(dayEventTextArray[5].getText());
                    for (int i = 0; i < searchDayEventList.size(); i++) {
                        //TODO 顯示總覽時會在這邊爆out fo index exception
                        Examination examination = (Examination) searchDayEventList.get(i);
                        //時間 + 科別
                        dayEventTextBuilder.append(examination.getExaminationStartTime());
                        dayEventTextBuilder.append("    ");
                        dayEventTextBuilder.append(examination.getExaminationName());
                        dayEventTextBuilder.append("\n");
                    }
                    dayEventTextArray[5].setText(dayEventTextBuilder);
                    dayEventTextBuilder.delete(0, dayEventTextBuilder.length());
                    break;
            }
        }
    }

    private class ViewHolder {
        TextView dateOfWeekWord, dateOfMonthNum;
        LinearLayout eventGreen, eventPurple, eventYellow, eventBlue, eventGraceGreen, eventGraceBlue, eventNone;
        TextView eventGreenText, eventPurpleText, eventYellowText, eventBlueText, eventGraceGreenText, eventGraceBlueText;
    }
}
