package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 會議 - 用藥提醒
public class Meeting implements Serializable {
    //    /**
    //     * 類別
    //     */
    //    private String meetingSubkind;
    //
    //    /**
    //     * 時間
    //     */
    //    private int meetingDate;

    /**
     * 標題
     */
    private String meetingTitle;

    /**
     * JSON OBJECT
     */
    private String meetingJsonObj;

    /**
     * MsgPk
     */
    private String meetingMsgPk;

    private String meetingEventTypePk;
    ////////////////////////////////////////////////////
    /**
     * 類別
     */
    private String meetingSubkind;

    /**
     * 時間紀錄
     */
    private int meetingDate;

    /**
     * 行程名稱
     */
    private String meetingName;

    /**
     * 行程地點
     */
    private String meetingLocation;

    /**
     * 起始日期
     */
    private String meetingStartDate;

    /**
     * 結束日期
     */
    private String meetingEndDate;

    /**
     * 起始時間
     */
    private String meetingStartTime;

    /**
     * 結束時間
     */
    private String meetingEndTime;

    /**
     * 備註
     */
    private String meetingNote;

    /**
     * 是否跨日
     */
    private int meetingOneDay;

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public String getMeetingStartDate() {
        return meetingStartDate;
    }

    public void setMeetingStartDate(String meetingStartDate) {
        this.meetingStartDate = meetingStartDate;
    }

    public String getMeetingEndDate() {
        return meetingEndDate;
    }

    public void setMeetingEndDate(String meetingEndDate) {
        this.meetingEndDate = meetingEndDate;
    }

    public String getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(String meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public String getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(String meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }

    public String getMeetingNote() {
        return meetingNote;
    }

    public void setMeetingNote(String meetingNote) {
        this.meetingNote = meetingNote;
    }

    public int getMeetingOneDay() {
        return meetingOneDay;
    }

    public void setMeetingOneDay(int meetingOneDay) {
        this.meetingOneDay = meetingOneDay;
    }

    ///////////////////////////////////////////////////////////

    /**
     * 取得類別
     *
     * @return String
     */
    public String getMeetingSubkind() {
        return meetingSubkind;
    }

    /**
     * 設置類別
     *
     * @param meetingSubkind 類別
     */
    public void setMeetingSubkind(String meetingSubkind) {
        this.meetingSubkind = meetingSubkind;
    }

    /**
     * 取得時間
     *
     * @return String
     */
    public int getMeetingDate() {
        return meetingDate;
    }

    /**
     * 設置時間
     *
     * @param meetingDate 時間
     */
    public void setMeetingDate(int meetingDate) {
        this.meetingDate = meetingDate;
    }

    /**
     * 取得標題
     *
     * @return String
     */
    public String getMeetingTitle() {
        return meetingTitle;
    }

    /**
     * 設置標題
     *
     * @param meetingTitle 標題
     */
    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    /**
     * 取得JsonObj
     *
     * @return String
     */
    public String getMeetingJsonObj() {
        return meetingJsonObj;
    }

    /**
     * 設置JsonObj
     *
     * @param meetingJsonObj JsonObj
     */
    public void setMeetingJsonObj(String meetingJsonObj) {
        this.meetingJsonObj = meetingJsonObj;
    }

    /**
     * 取得MsgPk
     *
     * @return String
     */
    public String getMeetingMsgPk() {
        return meetingMsgPk;
    }

    /**
     * 設置MsgPk
     *
     * @param meetingMsgPk MsgPk
     */
    public void setMeetingMsgPk(String meetingMsgPk) {
        this.meetingMsgPk = meetingMsgPk;
    }

    public String getMeetingEventTypePk() {
        return meetingEventTypePk;
    }

    public void setMeetingEventTypePk(String meetingEventTypePk) {
        this.meetingEventTypePk = meetingEventTypePk;
    }
}
