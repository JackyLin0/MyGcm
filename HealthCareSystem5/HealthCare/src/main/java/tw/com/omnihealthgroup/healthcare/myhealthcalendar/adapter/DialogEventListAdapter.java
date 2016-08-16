package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;

public class DialogEventListAdapter extends BaseAdapter {
    private static final String TAG = "DialogEventListAdapter";
    private LayoutInflater adapterLayoutInflater;
    private int listCount = 0;
    private int table;
    private LinkedHashMap<String, List<Object>> dayEventList;
    private LinkedList<String> dayEventTimeList = new LinkedList<>();
    private LinkedList<String> dayEventSubKindList = new LinkedList<>();
    private LinkedList<String> dayEventTitleList = new LinkedList<>();
    private LinkedList<Integer> dayEventColorList = new LinkedList<>();

    public DialogEventListAdapter(Context c) {
        adapterLayoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return listCount;
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
        ViewHolder eventListViewHolder;
        eventListViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = adapterLayoutInflater.inflate(R.layout.dialogfragment_view_listview_custom, parent, false);
            eventListViewHolder.middleLine = (ImageView) convertView.findViewById(R.id.dialog_middle_line);
            eventListViewHolder.dayTimeText = (TextView) convertView.findViewById(R.id.dialog_day_time_text);
            eventListViewHolder.dayEventTitle = (TextView) convertView.findViewById(R.id.dialog_day_event_title);
            eventListViewHolder.dayEventContent = (TextView) convertView.findViewById(R.id.dialog_day_event_content);

            convertView.setTag(eventListViewHolder);
        } else {
            eventListViewHolder = (ViewHolder) convertView.getTag();
        }

        if (dayEventSubKindList != null && dayEventTitleList != null) {
            eventListViewHolder.dayTimeText.setText(dayEventTimeList.get(position));
            eventListViewHolder.dayEventTitle.setText(dayEventSubKindList.get(position));
            eventListViewHolder.dayEventContent.setText(dayEventTitleList.get(position));
            eventListViewHolder.middleLine.setBackgroundResource(dayEventColorList.get(position));
        }
        return convertView;
    }

    /**
     * 設置列表數量
     *
     * @param listCount 列表數量
     */
    public void setCount(int listCount) {
        this.listCount = listCount;
    }

    /**
     * 取得事件類型
     *
     * @return int
     */
    public int getTable() {
        return table;
    }

    /**
     * 設置事件類型
     *
     * @param table 事件類型
     */
    public void setTable(int table) {
        this.table = table;
    }

    /**
     * 取得事件列表
     *
     * @return 事件列表
     */
    public LinkedHashMap<String, List<Object>> getDayEventList() {
        return dayEventList;
    }

    /**
     * 設置事件列表
     *
     * @param dayEventList LinkedList
     */
    public void setDayEventList(LinkedHashMap<String, List<Object>> dayEventList, int eventTable) {
        this.dayEventList = dayEventList;
        List<Object> clinicList, consultationList, operationList, meetingList, personalList, examinationList;
        switch (eventTable) {
            case 0:
                clinicList = getDayEventList().get(ScheduleDAO.TABLE_CLINIC);
                for (int j = 0; j < clinicList.size(); j++) {
                    Clinic clinic = (Clinic) clinicList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(clinic.getClinicSubkind());
                    //                    dayEventTitleList.add(clinic.getClinicTitle());
                    //                    dayEventColorList.add(R.drawable.eventtaggreen);
                    //組合事件日期字串
                    if (clinic.getClinicOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                clinic.getClinicStartTime()
                                        + "\n至\n"
                                        + clinic.getClinicEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                clinic.getClinicStartDate().substring(7, 14)
                                        + "\n"
                                        + clinic.getClinicStartTime()
                                        + "\n至\n"
                                        + clinic.getClinicEndDate().substring(7, 14)
                                        + "\n"
                                        + clinic.getClinicEndTime()
                        );
                    }
                    System.out.println(
                            clinic.getClinicStartDate().substring(0, 14)
                                    + "\n"
                                    + clinic.getClinicStartTime()
                                    + "\n至\n"
                                    + clinic.getClinicEndDate().substring(0, 14)
                                    + "\n"
                                    + clinic.getClinicEndTime()
                    );
                    dayEventSubKindList.add(clinic.getClinicName());
                    dayEventTitleList.add(
                            clinic.getClinicLocation()
                                    + "\n"
                                    + clinic.getClinicNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggreen);
                }

                consultationList = getDayEventList().get(ScheduleDAO.TABLE_CONSULTATION);
                for (int j = 0; j < consultationList.size(); j++) {
                    Consultation consultation = (Consultation) consultationList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(consultation.getConsultationSubkind());
                    //                    dayEventTitleList.add(consultation.getConsultationTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagpurple);
                    //組合事件日期字串
                    if (consultation.getConsultationOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                consultation.getConsultationStartTime()
                                        + "\n至\n"
                                        + consultation.getConsultationEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                consultation.getConsultationStartDate().substring(7, 14)
                                        + "\n"
                                        + consultation.getConsultationStartTime()
                                        + "\n至\n"
                                        + consultation.getConsultationEndDate().substring(7, 14)
                                        + "\n"
                                        + consultation.getConsultationEndTime()
                        );
                    }
                    System.out.println(
                            consultation.getConsultationStartDate().substring(0, 14)
                                    + "\n"
                                    + consultation.getConsultationStartTime()
                                    + "\n至\n"
                                    + consultation.getConsultationEndDate().substring(0, 14)
                                    + "\n"
                                    + consultation.getConsultationEndTime()
                    );
                    dayEventSubKindList.add(consultation.getConsultationName());
                    dayEventTitleList.add(
                            consultation.getConsultationLocation()
                                    + "\n"
                                    + consultation.getConsultationNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagpurple);
                }

                operationList = getDayEventList().get(ScheduleDAO.TABLE_OPERATE);
                for (int j = 0; j < operationList.size(); j++) {
                    Operation operation = (Operation) operationList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(operation.getOperateSubkind());
                    //                    dayEventTitleList.add(operation.getOperateTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagyellow);
                    //組合事件日期字串
                    if (operation.getOperateOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                operation.getOperateStartTime()
                                        + "\n至\n"
                                        + operation.getOperateEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                operation.getOperateStartDate().substring(7, 14)
                                        + "\n"
                                        + operation.getOperateStartTime()
                                        + "\n至\n"
                                        + operation.getOperateEndDate().substring(7, 14)
                                        + "\n"
                                        + operation.getOperateEndTime()
                        );
                    }
                    System.out.println(
                            operation.getOperateStartDate().substring(0, 14)
                                    + "\n"
                                    + operation.getOperateStartTime()
                                    + "\n至\n"
                                    + operation.getOperateEndDate().substring(0, 14)
                                    + "\n"
                                    + operation.getOperateEndTime()
                    );
                    dayEventSubKindList.add(operation.getOperateName());
                    dayEventTitleList.add(
                            operation.getOperateLocation()
                                    + "\n"
                                    + operation.getOperateNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagyellow);
                }

                meetingList = getDayEventList().get(ScheduleDAO.TABLE_MEETING);
                for (int j = 0; j < meetingList.size(); j++) {
                    Meeting meeting = (Meeting) meetingList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(meeting.getMeetingSubkind());
                    //                    dayEventTitleList.add(meeting.getMeetingTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagblue);
                    //組合事件日期字串
                    if (meeting.getMeetingOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                meeting.getMeetingStartTime()
                                        + "\n至\n"
                                        + meeting.getMeetingEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                meeting.getMeetingStartDate().substring(7, 14)
                                        + "\n"
                                        + meeting.getMeetingStartTime()
                                        + "\n至\n"
                                        + meeting.getMeetingEndDate().substring(7, 14)
                                        + "\n"
                                        + meeting.getMeetingEndTime()
                        );
                    }
                    System.out.println(
                            meeting.getMeetingStartDate().substring(0, 14)
                                    + "\n"
                                    + meeting.getMeetingStartTime()
                                    + "\n至\n"
                                    + meeting.getMeetingEndDate().substring(0, 14)
                                    + "\n"
                                    + meeting.getMeetingEndTime()
                    );
                    dayEventSubKindList.add(meeting.getMeetingName());
                    dayEventTitleList.add(
                            meeting.getMeetingLocation()
                                    + "\n"
                                    + meeting.getMeetingNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagblue);
                }

                personalList = getDayEventList().get(ScheduleDAO.TABLE_PERSONAL);
                for (int j = 0; j < personalList.size(); j++) {
                    Personal personal = (Personal) personalList.get(j);
                    //組合事件日期字串
                    if (personal.getPersonalOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                personal.getPersonalStartTime()
                                        + "\n至\n"
                                        + personal.getPersonalEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                personal.getPersonalStartDate().substring(7, 14)
                                        + "\n"
                                        + personal.getPersonalStartTime()
                                        + "\n至\n"
                                        + personal.getPersonalEndDate().substring(7, 14)
                                        + "\n"
                                        + personal.getPersonalEndTime()
                        );
                    }
                    System.out.println(
                            personal.getPersonalStartDate().substring(0, 14)
                                    + "\n"
                                    + personal.getPersonalStartTime()
                                    + "\n至\n"
                                    + personal.getPersonalEndDate().substring(0, 14)
                                    + "\n"
                                    + personal.getPersonalEndTime()
                    );
                    dayEventSubKindList.add(personal.getPersonalName());
                    dayEventTitleList.add(
                            personal.getPersonalLocation()
                                    + "\n"
                                    + personal.getPersonalNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggracegreen);
                }

                examinationList = getDayEventList().get(ScheduleDAO.TABLE_EXAMINATION);
                for (int j = 0; j < examinationList.size(); j++) {
                    Examination examination = (Examination) examinationList.get(j);
                    //組合事件日期字串
                    if (examination.getExaminationOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                examination.getExaminationStartTime()
                                        + "\n至\n"
                                        + examination.getExaminationEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                examination.getExaminationStartDate().substring(7, 14)
                                        + "\n"
                                        + examination.getExaminationStartTime()
                                        + "\n至\n"
                                        + examination.getExaminationEndDate().substring(7, 14)
                                        + "\n"
                                        + examination.getExaminationEndTime()
                        );
                    }
                    System.out.println(
                            examination.getExaminationStartDate().substring(0, 14)
                                    + "\n"
                                    + examination.getExaminationStartTime()
                                    + "\n至\n"
                                    + examination.getExaminationEndDate().substring(0, 14)
                                    + "\n"
                                    + examination.getExaminationEndTime()
                    );
                    dayEventSubKindList.add(examination.getExaminationName());
                    dayEventTitleList.add(
                            examination.getExaminationLocation()
                                    + "\n"
                                    + examination.getExaminationNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggraceblue);
                }
                break;

            case 1:
                clinicList = getDayEventList().get(ScheduleDAO.TABLE_CLINIC);
                for (int j = 0; j < clinicList.size(); j++) {
                    Clinic clinic = (Clinic) clinicList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(clinic.getClinicSubkind());
                    //                    dayEventTitleList.add(clinic.getClinicTitle());
                    //                    dayEventColorList.add(R.drawable.eventtaggreen);
                    //判斷是否跨日
                    if (clinic.getClinicOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                clinic.getClinicStartTime()
                                        + "\n至\n"
                                        + clinic.getClinicEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                clinic.getClinicStartDate().substring(7, 14)
                                        + "\n"
                                        + clinic.getClinicStartTime()
                                        + "\n至\n"
                                        + clinic.getClinicEndDate().substring(7, 14)
                                        + "\n"
                                        + clinic.getClinicEndTime()
                        );
                    }
                    dayEventSubKindList.add(clinic.getClinicName());
                    dayEventTitleList.add(
                            clinic.getClinicLocation()
                                    + "\n"
                                    + clinic.getClinicNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggreen);
                }
                break;

            case 2:
                consultationList = getDayEventList().get(ScheduleDAO.TABLE_CONSULTATION);
                for (int j = 0; j < consultationList.size(); j++) {
                    Consultation consultation = (Consultation) consultationList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(consultation.getConsultationSubkind());
                    //                    dayEventTitleList.add(consultation.getConsultationTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagpurple);
                    //判斷是否跨日
                    if (consultation.getConsultationOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                consultation.getConsultationStartTime()
                                        + "\n至\n"
                                        + consultation.getConsultationEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                consultation.getConsultationStartDate().substring(7, 14)
                                        + "\n"
                                        + consultation.getConsultationStartTime()
                                        + "\n至\n"
                                        + consultation.getConsultationEndDate().substring(7, 14)
                                        + "\n"
                                        + consultation.getConsultationEndTime()
                        );
                    }
                    dayEventSubKindList.add(consultation.getConsultationName());
                    dayEventTitleList.add(
                            consultation.getConsultationLocation()
                                    + "\n"
                                    + consultation.getConsultationNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagpurple);
                }
                break;

            case 3:
                operationList = getDayEventList().get(ScheduleDAO.TABLE_OPERATE);
                for (int j = 0; j < operationList.size(); j++) {
                    Operation operation = (Operation) operationList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(operation.getOperateSubkind());
                    //                    dayEventTitleList.add(operation.getOperateTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagyellow);
                    //判斷是否跨日
                    if (operation.getOperateOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                operation.getOperateStartTime()
                                        + "\n至\n"
                                        + operation.getOperateEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                operation.getOperateStartDate().substring(7, 14)
                                        + "\n"
                                        + operation.getOperateStartTime()
                                        + "\n至\n"
                                        + operation.getOperateEndDate().substring(7, 14)
                                        + "\n"
                                        + operation.getOperateEndTime()
                        );
                    }
                    dayEventSubKindList.add(operation.getOperateName());
                    dayEventTitleList.add(
                            operation.getOperateLocation()
                                    + "\n"
                                    + operation.getOperateNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagyellow);
                }
                break;

            case 4:
                meetingList = getDayEventList().get(ScheduleDAO.TABLE_MEETING);
                for (int j = 0; j < meetingList.size(); j++) {
                    Meeting meeting = (Meeting) meetingList.get(j);
                    //                    dayEventTimeList.add("上午 08：00");
                    //                    dayEventSubKindList.add(meeting.getMeetingSubkind());
                    //                    dayEventTitleList.add(meeting.getMeetingTitle());
                    //                    dayEventColorList.add(R.drawable.eventtagblue);
                    //判斷是否跨日
                    if (meeting.getMeetingOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                meeting.getMeetingStartTime()
                                        + "\n至\n"
                                        + meeting.getMeetingEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                meeting.getMeetingStartDate().substring(7, 14)
                                        + "\n"
                                        + meeting.getMeetingStartTime()
                                        + "\n至\n"
                                        + meeting.getMeetingEndDate().substring(7, 14)
                                        + "\n"
                                        + meeting.getMeetingEndTime()
                        );
                    }
                    dayEventSubKindList.add(meeting.getMeetingName());
                    dayEventTitleList.add(
                            meeting.getMeetingLocation()
                                    + "\n"
                                    + meeting.getMeetingNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtagblue);
                }
                break;

            case 5:
                System.out.println("HELLO ?");
                personalList = getDayEventList().get(ScheduleDAO.TABLE_PERSONAL);
                for (int j = 0; j < personalList.size(); j++) {
                    Personal personal = (Personal) personalList.get(j);
                    //判斷是否跨日
                    if (personal.getPersonalOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                personal.getPersonalStartTime()
                                        + "\n至\n"
                                        + personal.getPersonalEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                personal.getPersonalStartDate().substring(7, 14)
                                        + "\n"
                                        + personal.getPersonalStartTime()
                                        + "\n至\n"
                                        + personal.getPersonalEndDate().substring(7, 14)
                                        + "\n"
                                        + personal.getPersonalEndTime()
                        );
                    }
                    dayEventSubKindList.add(personal.getPersonalName());
                    dayEventTitleList.add(
                            personal.getPersonalLocation()
                                    + "\n"
                                    + personal.getPersonalNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggracegreen);
                }
                break;

            case 6:
                System.out.println("HELLO ?");
                examinationList = getDayEventList().get(ScheduleDAO.TABLE_EXAMINATION);
                for (int j = 0; j < examinationList.size(); j++) {
                    Examination examination = (Examination) examinationList.get(j);
                    //判斷是否跨日
                    if (examination.getExaminationOneDay() > 0) {
                        //無跨日
                        dayEventTimeList.add(
                                examination.getExaminationStartTime()
                                        + "\n至\n"
                                        + examination.getExaminationEndTime()
                        );
                    } else {
                        //有跨日
                        dayEventTimeList.add(
                                examination.getExaminationStartDate().substring(7, 14)
                                        + "\n"
                                        + examination.getExaminationStartTime()
                                        + "\n至\n"
                                        + examination.getExaminationEndDate().substring(7, 14)
                                        + "\n"
                                        + examination.getExaminationEndTime()
                        );
                    }
                    dayEventSubKindList.add(examination.getExaminationName());
                    dayEventTitleList.add(
                            examination.getExaminationLocation()
                                    + "\n"
                                    + examination.getExaminationNote()
                    );
                    dayEventColorList.add(R.mipmap.eventtaggraceblue);
                }
                break;
        }
    }

    private class ViewHolder {
        ImageView middleLine;
        TextView dayTimeText, dayEventTitle, dayEventContent;
    }
}
