package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

public class ScheduleString {

    public static final String MSG_PK = "MsgPk";
    /**
     * 會診
     */
    public static final class ConsultationSchedule {
        public static final String CONSULTATION_KIND = "C";  //KindCode
        public static final String CONSULTATION_CATEGORY = "會診類別";
        public static final String CONSULTATION_TIME = "開立時間";
        public static final String CONSULTATION_ADMIN = "開立者";
        public static final String CONSULTATION_NAME = "姓名";
        public static final String CONSULTATION_SEX = "性別";
        public static final String CONSULTATION_BED_NO = "床號";
        public static final String CONSULTATION_DOCTOR = "開立醫師";
        public static final String CONSULTATION_ADMIN_DIVISION = "開立科別";
        public static final String CONSULTATION_DIVISION = "會診科別";
        public static final String CONSULTATION_RRR = "會診REPORT REQUESTED REGARDING";
        public static final String CONSULTATION_BS = "會診BIREF SUMMARY";
        public static final String CONSULTATION_NOTE = "備註";
    }

    /**
     * 開刀
     */
    public static final class OperateSchedule {
        public static final String OPERATE_KIND = "O";  //KindCode
        public static final String OPERATE_FROM = "來源";
        public static final String OPERATE_DATE = "開刀日期";
        public static final String OPERATE_SCHEDULE_DATE = "預計開刀時間";
        public static final String OPERATE_MEDICAL_NUMBER = "病歷號";
        public static final String OPERATE_NAME = "姓名";
        public static final String OPERATE_DIVISION = "科別";
        public static final String OPERATE_DIAGNOSIS = "主診斷";
        public static final String OPERATE_OPERATION = "手術名稱";
        public static final String OPERATE_NOTE = "備註";
    }

}
