package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 私人行程 - 預約看診
public class Examination implements Serializable {
    /**
     * 類別
     */
    private String examinationSubkind;

    /**
     * 時間紀錄
     */
    private int examinationDate;

    /**
     * 行程名稱
     */
    private String examinationName;

    /**
     * 行程地點
     */
    private String examinationLocation;

    /**
     * 起始日期
     */
    private String examinationStartDate;

    /**
     * 結束日期
     */
    private String examinationEndDate;

    /**
     * 起始時間
     */
    private String examinationStartTime;

    /**
     * 結束時間
     */
    private String examinationEndTime;

    /**
     * 備註
     */
    private String examinationNote;

    /**
     * 是否跨日
     */
    private int examinationOneDay;

    /**
     * JSON OBJECT
     */
    private String examinationJsonObj;

    /**
     * MsgPk
     */
    private String examinationMsgPk;

    private String examinationEventTypePk;

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 取得類別
     *
     * @return String
     */
    public String getExaminationSubkind() {
        return examinationSubkind;
    }

    /**
     * 設置類別
     *
     * @param examinationSubkind 類別
     */
    public void setExaminationSubkind(String examinationSubkind) {
        this.examinationSubkind = examinationSubkind;
    }

    /**
     * 取得時間紀錄
     *
     * @return int
     */
    public int getExaminationDate() {
        return examinationDate;
    }

    /**
     * 設置時間紀錄
     *
     * @param examinationDate 時間紀錄
     */
    public void setExaminationDate(int examinationDate) {
        this.examinationDate = examinationDate;
    }

    /**
     * 取得行程名稱
     *
     * @return String
     */
    public String getExaminationName() {
        return examinationName;
    }

    /**
     * 設置行程名稱
     *
     * @param examinationName 行程名稱
     */
    public void setExaminationName(String examinationName) {
        this.examinationName = examinationName;
    }

    /**
     * 取得行程地點
     *
     * @return String
     */
    public String getExaminationLocation() {
        return examinationLocation;
    }

    /**
     * 設置行程地點
     *
     * @param examinationLocation 行程地點
     */
    public void setExaminationLocation(String examinationLocation) {
        this.examinationLocation = examinationLocation;
    }

    /**
     * 取得行程起始日期
     *
     * @return String
     */
    public String getExaminationStartDate() {
        return examinationStartDate;
    }

    /**
     * 設置行程起始日期
     *
     * @param examinationStartDate 行程起始時間
     */
    public void setExaminationStartDate(String examinationStartDate) {
        this.examinationStartDate = examinationStartDate;
    }

    /**
     * 取得行程結束日期
     *
     * @return String
     */
    public String getExaminationEndDate() {
        return examinationEndDate;
    }

    /**
     * 設置行程結束日期
     *
     * @param examinationEndDate 結束時間
     */
    public void setExaminationEndDate(String examinationEndDate) {
        this.examinationEndDate = examinationEndDate;
    }

    /**
     * 取得起始時間
     *
     * @return String
     */
    public String getExaminationStartTime() {
        return examinationStartTime;
    }

    /**
     * 設置起始時間
     *
     * @param examinationStartTime 起始時間
     */
    public void setExaminationStartTime(String examinationStartTime) {
        this.examinationStartTime = examinationStartTime;
    }

    /**
     * 取得結束時間
     *
     * @return String
     */
    public String getExaminationEndTime() {
        return examinationEndTime;
    }

    /**
     * 設置結束時間
     *
     * @param examinationEndTime 設置結束時間
     */
    public void setExaminationEndTime(String examinationEndTime) {
        this.examinationEndTime = examinationEndTime;
    }

    /**
     * 取得備註
     *
     * @return String
     */
    public String getExaminationNote() {
        return examinationNote;
    }

    /**
     * 設置備註
     *
     * @param examinationNote 備註
     */
    public void setExaminationNote(String examinationNote) {
        this.examinationNote = examinationNote;
    }

    /**
     * 判斷是否跨日
     *
     * @return boolean
     */
    public int getExaminationOneDay() {
        return examinationOneDay;
    }

    /**
     * 設置跨日的boolean值
     *
     * @param examinationOneDay 是否跨日
     */
    public void setExaminationOneDay(int examinationOneDay) {
        this.examinationOneDay = examinationOneDay;
    }

    public String getExaminationJsonObj() {
        return examinationJsonObj;
    }

    public void setExaminationJsonObj(String examinationJsonObj) {
        this.examinationJsonObj = examinationJsonObj;
    }

    public String getExaminationMsgPk() {
        return examinationMsgPk;
    }

    public void setExaminationMsgPk(String examinationMsgPk) {
        this.examinationMsgPk = examinationMsgPk;
    }

    public String getExaminationEventTypePk() {
        return examinationEventTypePk;
    }

    public void setExaminationEventTypePk(String examinationEventTypePk) {
        this.examinationEventTypePk = examinationEventTypePk;
    }
}
