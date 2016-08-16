package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 門診 - 一般記事
public class Clinic implements Serializable {
    /**
     * 類別
     */
    private String clinicSubkind;

    /**
     * 時間
     */
    private int clinicDate;

    /**
     * 標題
     */
    private String clinicTitle;

    /**
     * JSON OBJECT
     */
    private String clinicJsonObj;

    /**
     * MsgPk
     */
    private String clinicMsgPk;

    private String clinicEventTypePk;
    ////////////////////////////////////////////////////
    //    /**
    //     * 類別
    //     */
    //    private String clinicSubkind;
    //
    //    /**
    //     * 時間紀錄
    //     */
    //    private int clinicDate;

    /**
     * 行程名稱
     */
    private String clinicName;

    /**
     * 行程地點
     */
    private String clinicLocation;

    /**
     * 起始日期
     */
    private String clinicStartDate;

    /**
     * 結束日期
     */
    private String clinicEndDate;

    /**
     * 起始時間
     */
    private String clinicStartTime;

    /**
     * 結束時間
     */
    private String clinicEndTime;

    /**
     * 備註
     */
    private String clinicNote;

    /**
     * 是否跨日
     */
    private int clinicOneDay;

    //////////////////////////////////////////////////

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicLocation() {
        return clinicLocation;
    }

    public void setClinicLocation(String clinicLocation) {
        this.clinicLocation = clinicLocation;
    }

    public String getClinicStartDate() {
        return clinicStartDate;
    }

    public void setClinicStartDate(String clinicStartDate) {
        this.clinicStartDate = clinicStartDate;
    }

    public String getClinicEndDate() {
        return clinicEndDate;
    }

    public void setClinicEndDate(String clinicEndDate) {
        this.clinicEndDate = clinicEndDate;
    }

    public String getClinicStartTime() {
        return clinicStartTime;
    }

    public void setClinicStartTime(String clinicStartTime) {
        this.clinicStartTime = clinicStartTime;
    }

    public String getClinicEndTime() {
        return clinicEndTime;
    }

    public void setClinicEndTime(String clinicEndTime) {
        this.clinicEndTime = clinicEndTime;
    }

    public String getClinicNote() {
        return clinicNote;
    }

    public void setClinicNote(String clinicNote) {
        this.clinicNote = clinicNote;
    }

    public int getClinicOneDay() {
        return clinicOneDay;
    }

    public void setClinicOneDay(int clinicOneDay) {
        this.clinicOneDay = clinicOneDay;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 取得類別
     *
     * @return String
     */
    public String getClinicSubkind() {
        return clinicSubkind;
    }

    /**
     * 設置類別
     *
     * @param clinicSubkind 類別
     */
    public void setClinicSubkind(String clinicSubkind) {
        this.clinicSubkind = clinicSubkind;
    }

    /**
     * 取得時間
     *
     * @return int
     */
    public int getClinicDate() {
        return clinicDate;
    }

    /**
     * 設置時間
     *
     * @param clinicDate 時間
     */
    public void setClinicDate(int clinicDate) {
        this.clinicDate = clinicDate;
    }

    /**
     * 取得標題
     *
     * @return String
     */
    public String getClinicTitle() {
        return clinicTitle;
    }

    /**
     * 設置標題
     *
     * @param clinicTitle 標題
     */
    public void setClinicTitle(String clinicTitle) {
        this.clinicTitle = clinicTitle;
    }

    /**
     * 取得JsonObj
     *
     * @return String
     */
    public String getClinicJsonObj() {
        return clinicJsonObj;
    }

    /**
     * 設置JsonObj
     *
     * @param clinicJsonObj JsonObj
     */
    public void setClinicJsonObj(String clinicJsonObj) {
        this.clinicJsonObj = clinicJsonObj;
    }

    /**
     * 取得MsgPk
     *
     * @return String
     */
    public String getClinicMsgPk() {
        return clinicMsgPk;
    }

    /**
     * 設置MsgPk
     *
     * @param clinicMsgPk MsgPk
     */
    public void setClinicMsgPk(String clinicMsgPk) {
        this.clinicMsgPk = clinicMsgPk;
    }

    public String getClinicEventTypePk() {
        return clinicEventTypePk;
    }

    public void setClinicEventTypePk(String clinicEventTypePk) {
        this.clinicEventTypePk = clinicEventTypePk;
    }
}
