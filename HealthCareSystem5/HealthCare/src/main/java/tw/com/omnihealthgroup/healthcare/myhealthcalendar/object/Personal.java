package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 私人行程 - 預約看診
public class Personal implements Serializable {
    /**
     * 類別
     */
    private String personalSubkind;

    /**
     * 時間紀錄
     */
    private int personalDate;

    /**
     * 行程名稱
     */
    private String personalName;

    /**
     * 行程地點
     */
    private String personalLocation;

    /**
     * 起始日期
     */
    private String personalStartDate;

    /**
     * 結束日期
     */
    private String personalEndDate;

    /**
     * 起始時間
     */
    private String personalStartTime;

    /**
     * 結束時間
     */
    private String personalEndTime;

    /**
     * 備註
     */
    private String personalNote;

    /**
     * 是否跨日
     */
    private int personalOneDay;

    /**
     * JSON OBJECT
     */
    private String personalJsonObj;

    /**
     * MsgPk
     */
    private String personalMsgPk;

    private String personalEventTypePk;

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 取得類別
     *
     * @return String
     */
    public String getPersonalSubkind() {
        return personalSubkind;
    }

    /**
     * 設置類別
     *
     * @param personalSubkind 類別
     */
    public void setPersonalSubkind(String personalSubkind) {
        this.personalSubkind = personalSubkind;
    }

    /**
     * 取得時間紀錄
     *
     * @return int
     */
    public int getPersonalDate() {
        return personalDate;
    }

    /**
     * 設置時間紀錄
     *
     * @param personalDate 時間紀錄
     */
    public void setPersonalDate(int personalDate) {
        this.personalDate = personalDate;
    }

    /**
     * 取得行程名稱
     *
     * @return String
     */
    public String getPersonalName() {
        return personalName;
    }

    /**
     * 設置行程名稱
     *
     * @param personalName 行程名稱
     */
    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    /**
     * 取得行程地點
     *
     * @return String
     */
    public String getPersonalLocation() {
        return personalLocation;
    }

    /**
     * 設置行程地點
     *
     * @param personalLocation 行程地點
     */
    public void setPersonalLocation(String personalLocation) {
        this.personalLocation = personalLocation;
    }

    /**
     * 取得行程起始日期
     *
     * @return String
     */
    public String getPersonalStartDate() {
        return personalStartDate;
    }

    /**
     * 設置行程起始日期
     *
     * @param personalStartDate 行程起始時間
     */
    public void setPersonalStartDate(String personalStartDate) {
        this.personalStartDate = personalStartDate;
    }

    /**
     * 取得行程結束日期
     *
     * @return String
     */
    public String getPersonalEndDate() {
        return personalEndDate;
    }

    /**
     * 設置行程結束日期
     *
     * @param personalEndDate 結束時間
     */
    public void setPersonalEndDate(String personalEndDate) {
        this.personalEndDate = personalEndDate;
    }

    /**
     * 取得起始時間
     *
     * @return String
     */
    public String getPersonalStartTime() {
        return personalStartTime;
    }

    /**
     * 設置起始時間
     *
     * @param personalStartTime 起始時間
     */
    public void setPersonalStartTime(String personalStartTime) {
        this.personalStartTime = personalStartTime;
    }

    /**
     * 取得結束時間
     *
     * @return String
     */
    public String getPersonalEndTime() {
        return personalEndTime;
    }

    /**
     * 設置結束時間
     *
     * @param personalEndTime 設置結束時間
     */
    public void setPersonalEndTime(String personalEndTime) {
        this.personalEndTime = personalEndTime;
    }

    /**
     * 取得備註
     *
     * @return String
     */
    public String getPersonalNote() {
        return personalNote;
    }

    /**
     * 設置備註
     *
     * @param personalNote 備註
     */
    public void setPersonalNote(String personalNote) {
        this.personalNote = personalNote;
    }

    /**
     * 判斷是否跨日
     *
     * @return boolean
     */
    public int getPersonalOneDay() {
        return personalOneDay;
    }

    /**
     * 設置跨日的boolean值
     *
     * @param personalOneDay 是否跨日
     */
    public void setPersonalOneDay(int personalOneDay) {
        this.personalOneDay = personalOneDay;
    }

    public String getPersonalJsonObj() {
        return personalJsonObj;
    }

    public void setPersonalJsonObj(String personalJsonObj) {
        this.personalJsonObj = personalJsonObj;
    }

    public String getPersonalMsgPk() {
        return personalMsgPk;
    }

    public void setPersonalMsgPk(String personalMsgPk) {
        this.personalMsgPk = personalMsgPk;
    }

    public String getPersonalEventTypePk() {
        return personalEventTypePk;
    }

    public void setPersonalEventTypePk(String personalEventTypePk) {
        this.personalEventTypePk = personalEventTypePk;
    }
}
