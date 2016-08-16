package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;

/**
 * Created by Administrator on 2016/6/22.
 */
public class EventViewAdapter extends RecyclerView.Adapter<EventViewAdapter.ViewHolder> {
    private static final String TAG = "EventViewAdapter";
    private LinkedHashMap<String, List<Object>> dayEventList;
    private LinkedList<String> dayEventTimeList = new LinkedList<>();
    private LinkedList<String> dayEventSubKindList = new LinkedList<>();
    private LinkedList<String> dayEventTitleList = new LinkedList<>();
    private LinkedList<Integer> dayEventColorList = new LinkedList<>();

    private int listCount = 0;
    private int table;
    private Context mcontext;

    // Provide a suitable constructor (depends on the kind of dataset)
    //    public EventViewAdapter(Context context, ArrayList<String> myDataset, ArrayList<String> myPictureset) {
    public EventViewAdapter(Context context) {
        //        mDataset = myDataset;
        //        mPictureset = myPictureset;
        mcontext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mcardView;
        public TextView dayTimeText, dayEventTitle, dayEventContent;

        public ImageView middleLine;

        public ViewHolder(View v) {
            super(v);
            mcardView = (CardView) v.findViewById(R.id.cardview_item_event);
            dayTimeText = (TextView) v.findViewById(R.id.dialog_day_time_text);
            dayEventTitle = (TextView) v.findViewById(R.id.dialog_day_event_title);
            dayEventContent = (TextView) v.findViewById(R.id.dialog_day_event_content);
            middleLine = (ImageView) v.findViewById(R.id.dialog_middle_line);


        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item_event, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EventViewAdapter.ViewHolder holder, int position) {
        if (dayEventSubKindList != null && dayEventTitleList != null) {
            holder.dayTimeText.setText(dayEventTimeList.get(position));
            holder.dayEventTitle.setText(dayEventSubKindList.get(position));
            holder.dayEventContent.setText(dayEventTitleList.get(position));
            holder.dayEventContent.setVisibility(View.GONE);
            holder.mcardView.setCardBackgroundColor(mcontext.getResources().getColor(dayEventColorList.get(position)));
        }
        //        Picasso.with(holder.mimageView.getContext()).load(mPictureset.get(position)).into(holder.mimageView);

    }

    @Override
    public int getItemCount() {
        return listCount;
    }

    public void add(String text, int position) {
        //        mDataset.add(position, text);
        //        mPictureset.add(position, text);
        notifyItemInserted(position);  //有一个新项插入到了 position 位置
        //        notifyItemRangeInserted(position, count) - 在 position 位置插入了 count 个新项目
    }

    public void remove(int position) {
        //        mDataset.remove(position);
        //        mPictureset.remove(position);
        notifyItemRemoved(position);
        //        notifyItemRangeRemoved(position, count)
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
        int listCount = 0;

        clinicList = getDayEventList().get(ScheduleDAO.TABLE_CLINIC);
        //        String count = "1";
        for (int j = 0; j < clinicList.size(); j++) {
            Clinic clinic = (Clinic) clinicList.get(j);
            //                    dayEventTimeList.add("上午 08：00");
            //                    dayEventSubKindList.add(clinic.getClinicSubkind());
            //                    dayEventTitleList.add(clinic.getClinicTitle());
            //                    dayEventColorList.add(R.drawable.eventtaggreen);
            //組合事件日期字串
            //                    if (clinic.getClinicOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                clinic.getClinicStartTime()
            //                                        + "\n至\n"
            //                                        + clinic.getClinicEndTime());
            //                    } else {
            //有跨日
            //            if (!clinic.getClinicMsgPk().equals(count)) {
            dayEventTimeList.add(
                    clinic.getClinicStartDate().substring(7, 14)
                            + " "
                            + clinic.getClinicStartTime()
                            + "\n至\n"
                            + clinic.getClinicEndDate().substring(7, 14)
                            + " "
                            + clinic.getClinicEndTime());
            //                    }

            dayEventSubKindList.add(clinic.getClinicName());
            dayEventTitleList.add(
                    clinic.getClinicLocation()
                            + "\n"
                            + clinic.getClinicNote());
            //                    dayEventColorList.add(R.drawable.eventtaggreen);
            dayEventColorList.add(R.color.event_type_green);
            //                count = clinic.getClinicMsgPk();
            //                listCount = listCount + 1;
            //            }
        }

        consultationList = getDayEventList().get(ScheduleDAO.TABLE_CONSULTATION);
        //        count = "1";
        for (int j = 0; j < consultationList.size(); j++) {
            Consultation consultation = (Consultation) consultationList.get(j);
            //                    dayEventTimeList.add("上午 08：00");
            //                    dayEventSubKindList.add(consultation.getConsultationSubkind());
            //                    dayEventTitleList.add(consultation.getConsultationTitle());
            //                    dayEventColorList.add(R.drawable.eventtagpurple);
            //組合事件日期字串
            //                    if (consultation.getConsultationOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                consultation.getConsultationStartTime()
            //                                        + "\n至\n"
            //                                        + consultation.getConsultationEndTime());
            //                    } else {
            //有跨日
            //            if (!consultation.getConsultationMsgPk().equals(count)) {
            dayEventTimeList.add(
                    consultation.getConsultationStartDate().substring(7, 14)
                            + " "
                            + consultation.getConsultationStartTime()
                            + "\n至\n"
                            + consultation.getConsultationEndDate().substring(7, 14)
                            + " "
                            + consultation.getConsultationEndTime());
            //                    }

            dayEventSubKindList.add(consultation.getConsultationName());
            dayEventTitleList.add(
                    consultation.getConsultationLocation()
                            + "\n"
                            + consultation.getConsultationNote());
            //                    dayEventColorList.add(R.drawable.eventtagpurple);
            dayEventColorList.add(R.color.event_type_purple);
            //                count = consultation.getConsultationMsgPk();
            //                listCount = listCount + 1;
            //            }
        }

        operationList = getDayEventList().get(ScheduleDAO.TABLE_OPERATE);
        //        count = "1";
        for (int j = 0; j < operationList.size(); j++) {
            Operation operation = (Operation) operationList.get(j);
            //                    dayEventTimeList.add("上午 08：00");
            //                    dayEventSubKindList.add(operation.getOperateSubkind());
            //                    dayEventTitleList.add(operation.getOperateTitle());
            //                    dayEventColorList.add(R.drawable.eventtagyellow);
            //組合事件日期字串
            //                    if (operation.getOperateOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                operation.getOperateStartTime()
            //                                        + "\n至\n"
            //                                        + operation.getOperateEndTime());
            //                    } else {
            //有跨日
            //            if (!operation.getOperateMsgPk().equals(count)) {
            dayEventTimeList.add(
                    operation.getOperateStartDate().substring(7, 14)
                            + " "
                            + operation.getOperateStartTime()
                            + "\n至\n"
                            + operation.getOperateEndDate().substring(7, 14)
                            + " "
                            + operation.getOperateEndTime());
            //                    }

            dayEventSubKindList.add(operation.getOperateName());
            dayEventTitleList.add(
                    operation.getOperateLocation()
                            + "\n"
                            + operation.getOperateNote());
            //                    dayEventColorList.add(R.drawable.eventtagyellow);
            dayEventColorList.add(R.color.event_type_yellow);
            //                count = operation.getOperateMsgPk();
            //                listCount = listCount + 1;
            //            }
        }

        meetingList = getDayEventList().get(ScheduleDAO.TABLE_MEETING);
        //        count = "1";
        for (int j = 0; j < meetingList.size(); j++) {
            Meeting meeting = (Meeting) meetingList.get(j);
            //                    dayEventTimeList.add("上午 08：00");
            //                    dayEventSubKindList.add(meeting.getMeetingSubkind());
            //                    dayEventTitleList.add(meeting.getMeetingTitle());
            //                    dayEventColorList.add(R.drawable.eventtagblue);
            //組合事件日期字串
            //                    if (meeting.getMeetingOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                meeting.getMeetingStartTime()
            //                                        + "\n至\n"
            //                                        + meeting.getMeetingEndTime());
            //                    } else {
            //有跨日
            //            if (!meeting.getMeetingMsgPk().equals(count)) {
            dayEventTimeList.add(
                    meeting.getMeetingStartDate().substring(7, 14)
                            + " "
                            + meeting.getMeetingStartTime()
                            + "\n至\n"
                            + meeting.getMeetingEndDate().substring(7, 14)
                            + " "
                            + meeting.getMeetingEndTime());
            //                    }

            dayEventSubKindList.add(meeting.getMeetingName());
            dayEventTitleList.add(
                    meeting.getMeetingLocation()
                            + "\n"
                            + meeting.getMeetingNote());
            //                    dayEventColorList.add(R.drawable.eventtagblue);
            dayEventColorList.add(R.color.event_type_blue);
            //                count = meeting.getMeetingMsgPk();
            //                listCount = listCount + 1;
            //            }
        }

        personalList = getDayEventList().get(ScheduleDAO.TABLE_PERSONAL);
        //        count = "1";
        for (int j = 0; j < personalList.size(); j++) {
            Personal personal = (Personal) personalList.get(j);
            //組合事件日期字串
            //                    if (personal.getPersonalOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                personal.getPersonalStartTime()
            //                                        + "\n至\n"
            //                                        + personal.getPersonalEndTime());
            //                    } else {
            //有跨日
            //            if (!personal.getPersonalMsgPk().equals(count)) {
            dayEventTimeList.add(
                    personal.getPersonalStartDate().substring(7, 14)
                            + " "
                            + personal.getPersonalStartTime()
                            + "\n至\n"
                            + personal.getPersonalEndDate().substring(7, 14)
                            + " "
                            + personal.getPersonalEndTime());
            //                    }

            dayEventSubKindList.add(personal.getPersonalName());
            dayEventTitleList.add(
                    personal.getPersonalLocation()
                            + "\n"
                            + personal.getPersonalNote());

            //                    dayEventColorList.add(R.drawable.eventtaggracegreen);
            dayEventColorList.add(R.color.event_type_gracegreen);
            //                count = personal.getPersonalMsgPk();
            //                listCount = listCount + 1;
            //            }
        }

        examinationList = getDayEventList().get(ScheduleDAO.TABLE_EXAMINATION);
        //        count = "1";
        for (int j = 0; j < examinationList.size(); j++) {
            Examination examination = (Examination) examinationList.get(j);
            //組合事件日期字串
            //                    if (examination.getExaminationOneDay() > 0) {
            //                        //無跨日
            //                        dayEventTimeList.add(
            //                                examination.getExaminationStartTime()
            //                                        + "\n至\n"
            //                                        + examination.getExaminationEndTime());
            //                    } else {
            //有跨日
            //            if (!examination.getExaminationMsgPk().equals(count)) {
            dayEventTimeList.add(
                    examination.getExaminationStartDate().substring(7, 14)
                            + " "
                            + examination.getExaminationStartTime()
                            + "\n至\n"
                            + examination.getExaminationEndDate().substring(7, 14)
                            + " "
                            + examination.getExaminationEndTime());
            //                    }

            dayEventSubKindList.add(examination.getExaminationName());
            dayEventTitleList.add(
                    examination.getExaminationLocation()
                            + "\n"
                            + examination.getExaminationNote());
            //                    dayEventColorList.add(R.drawable.eventtaggraceblue);
            dayEventColorList.add(R.color.event_type_graceblue);
            //                count = examination.getExaminationMsgPk();
            //                listCount = listCount + 1;
            //            }
        }
        //        setCount(listCount);
    }

}
