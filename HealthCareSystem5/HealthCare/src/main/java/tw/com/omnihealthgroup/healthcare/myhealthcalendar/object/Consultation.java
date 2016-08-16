package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 會診 - 重要活動
public class Consultation implements Serializable {
    /**
     * 會診類別
     */
    private String consultationSubkind;

    /**
     * 開立時間
     */
    private int consultationDate;

    /**
     * 開立科別＋床號＋姓名
     */
    private String consultationTitle;

    /**
     * 開立者
     */
    private String consultationAdmin;

    /**
     * 姓名
     */
    private String consultationName;

    /**
     * 性別
     */
    private String consultationSex;

    /**
     * 床號
     */
    private String consultationBedNo;

    /**
     * 開立醫師
     */
    private String consultationDoctor;

    /**
     * 開立科別
     */
    private String consultationAdminDivision;

    /**
     * 會診科別
     */
    private String consultationDivision;

    /**
     * REPORT REQUESTED REGARDING
     */
    private String consultationRRR;

    /**
     * BIREF SUMMARY
     */
    private String consultationBirefSummary;

    /**
     * 備註
     */
    private String consultationNote;

    /**
     * JSON OBJECT
     */
    private String consultationJsonObj;

    /**
     * MsgPk
     */
    private String consultationMsgPk;

    private String consultationEventTypePk;
    /////////////////////////////////////////////////////////
    //    /**
    //     * 類別
    //     */
    //    private String consultationSubkind;
    //
    //    /**
    //     * 時間紀錄
    //     */
    //    private int consultationDate;

    //    /**
    //     * 行程名稱
    //     */
    //    private String consultationName;

    /**
     * 行程地點
     */
    private String consultationLocation;

    /**
     * 起始日期
     */
    private String consultationStartDate;

    /**
     * 結束日期
     */
    private String consultationEndDate;

    /**
     * 起始時間
     */
    private String consultationStartTime;

    /**
     * 結束時間
     */
    private String consultationEndTime;

    //    /**
    //     * 備註
    //     */
    //    private String consultationNote;

    /**
     * 是否跨日
     */
    private int consultationOneDay;

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getConsultationLocation() {
        return consultationLocation;
    }

    public void setConsultationLocation(String consultationLocation) {
        this.consultationLocation = consultationLocation;
    }

    public String getConsultationStartDate() {
        return consultationStartDate;
    }

    public void setConsultationStartDate(String consultationStartDate) {
        this.consultationStartDate = consultationStartDate;
    }

    public String getConsultationEndDate() {
        return consultationEndDate;
    }

    public void setConsultationEndDate(String consultationEndDate) {
        this.consultationEndDate = consultationEndDate;
    }

    public String getConsultationStartTime() {
        return consultationStartTime;
    }

    public void setConsultationStartTime(String consultationStartTime) {
        this.consultationStartTime = consultationStartTime;
    }

    public String getConsultationEndTime() {
        return consultationEndTime;
    }

    public void setConsultationEndTime(String consultationEndTime) {
        this.consultationEndTime = consultationEndTime;
    }

    public int getConsultationOneDay() {
        return consultationOneDay;
    }

    public void setConsultationOneDay(int consultationOneDay) {
        this.consultationOneDay = consultationOneDay;
    }

    ////////////////////////////////////////////

    /**
     * 取得會診類別
     *
     * @return String
     */
    public String getConsultationSubkind() {
        return consultationSubkind;
    }

    /**
     * 設置會診類別
     *
     * @param consultationSubkind 會診類別
     */
    public void setConsultationSubkind(String consultationSubkind) {
        this.consultationSubkind = consultationSubkind;
    }

    /**
     * 取得(開立科別＋床號＋姓名)
     *
     * @return String
     */
    public String getConsultationTitle() {
        return consultationTitle;
    }

    /**
     * 設置(開立科別＋床號＋姓名)
     *
     * @param consultationTitle 開立科別＋床號＋姓名
     */
    public void setConsultationTitle(String consultationTitle) {
        this.consultationTitle = consultationTitle;
    }

    /**
     * 取得開立時間
     *
     * @return int
     */
    public int getConsultationDate() {
        return consultationDate;
    }

    /**
     * 設置開立時間
     *
     * @param consultationDate 開立時間
     */
    public void setConsultationDate(int consultationDate) {
        this.consultationDate = consultationDate;
    }

    /**
     * 取得開立者
     *
     * @return String
     */
    public String getConsultationAdmin() {
        return consultationAdmin;
    }

    /**
     * 設置開立者
     *
     * @param consultationAdmin 開立者
     */
    public void setConsultationAdmin(String consultationAdmin) {
        this.consultationAdmin = consultationAdmin;
    }

    /**
     * 取得姓名
     *
     * @return String
     */
    public String getConsultationName() {
        return consultationName;
    }

    /**
     * 設置姓名
     *
     * @param consultationName 姓名
     */
    public void setConsultationName(String consultationName) {
        this.consultationName = consultationName;
    }

    /**
     * 取得性別
     *
     * @return String
     */
    public String getConsultationSex() {
        return consultationSex;
    }

    /**
     * 設置性別
     *
     * @param consultationSex 性別
     */
    public void setConsultationSex(String consultationSex) {
        this.consultationSex = consultationSex;
    }

    /**
     * 取得床號
     *
     * @return String
     */
    public String getConsultationBedNo() {
        return consultationBedNo;
    }

    /**
     * 設置床號
     *
     * @param consultationBedNo 床號
     */
    public void setConsultationBedNo(String consultationBedNo) {
        this.consultationBedNo = consultationBedNo;
    }

    /**
     * 取得開立醫師
     *
     * @return String
     */
    public String getConsultationDoctor() {
        return consultationDoctor;
    }

    /**
     * 設置開立醫師
     *
     * @param consultationDoctor 開立醫師
     */
    public void setConsultationDoctor(String consultationDoctor) {
        this.consultationDoctor = consultationDoctor;
    }

    /**
     * 取得開立科別
     *
     * @return String
     */
    public String getConsultationAdminDivision() {
        return consultationAdminDivision;
    }

    /**
     * 設置開立科別
     *
     * @param consultationAdminDivision 開立科別
     */
    public void setConsultationAdminDivision(String consultationAdminDivision) {
        this.consultationAdminDivision = consultationAdminDivision;
    }

    /**
     * 取得會診科別
     *
     * @return String
     */
    public String getConsultationDivision() {
        return consultationDivision;
    }

    /**
     * 設置會診科別
     *
     * @param consultationDivision 會診科別
     */
    public void setConsultationDivision(String consultationDivision) {
        this.consultationDivision = consultationDivision;
    }

    /**
     * 取得REPORT REQUESTED REGARDING
     *
     * @return String
     */
    public String getConsultationRRR() {
        return consultationRRR;
    }

    /**
     * 設置REPORT REQUESTED REGARDING
     *
     * @param consultationRRR REPORT REQUESTED REGARDING
     */
    public void setConsultationRRR(String consultationRRR) {
        this.consultationRRR = consultationRRR;
    }

    /**
     * 取得BIREF SUMMARY
     *
     * @return String
     */
    public String getConsultationBirefSummary() {
        return consultationBirefSummary;
    }

    /**
     * 設置BIREF SUMMARY
     *
     * @param consultationBirefSummary BIREF SUMMARY
     */
    public void setConsultationBirefSummary(String consultationBirefSummary) {
        this.consultationBirefSummary = consultationBirefSummary;
    }

    /**
     * 取得備註
     *
     * @return String
     */
    public String getConsultationNote() {
        return consultationNote;
    }

    /**
     * 設置備註
     *
     * @param consultationNote 備註
     */
    public void setConsultationNote(String consultationNote) {
        this.consultationNote = consultationNote;
    }

    /**
     * 取得JSONObject
     *
     * @return String
     */
    public String getConsultationJsonObj() {
        return consultationJsonObj;
    }

    /**
     * 設置JSONObject
     *
     * @param consultationJsonObj JSONObject
     */
    public void setConsultationJsonObj(String consultationJsonObj) {
        this.consultationJsonObj = consultationJsonObj;
    }

    /**
     * 取得MsgPk
     *
     * @return String
     */
    public String getConsultationMsgPk() {
        return consultationMsgPk;
    }

    /**
     * 設置MsgPk
     *
     * @param consultationMsgPk MsgPk
     */
    public void setConsultationMsgPk(String consultationMsgPk) {
        this.consultationMsgPk = consultationMsgPk;
    }

    public String getConsultationEventTypePk() {
        return consultationEventTypePk;
    }

    public void setConsultationEventTypePk(String consultationEventTypePk) {
        this.consultationEventTypePk = consultationEventTypePk;
    }
}
