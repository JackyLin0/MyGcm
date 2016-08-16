package tw.com.omnihealthgroup.healthcare.myhealthcalendar.object;

import java.io.Serializable;

// 開刀 - 量測提醒
public class Operation implements Serializable {
    //    /**
    //     * 來源+科別
    //     */
    //    public String operateSubkind;

    /**
     * 科別+姓名
     */
    public String operateTitle;

    /**
     * 來源
     */
    public String operateFrom;

    //    /**
    //     * 開刀日期
    //     */
    //    public int operateDate;

    /**
     * 預計開刀日期
     */
    public String operateTime;

    /**
     * 病歷號
     */
    public String operateMedicalNumber;

    //    /**
    //     * 姓名
    //     */
    //    public String operateName;

    /**
     * 科別
     */
    public String operateDivision;

    /**
     * 主診斷
     */
    public String operateDiagnosis;

    /**
     * 手術名稱
     */
    public String operation;

    /**
     * 備註
     */
    public String operationNote;

    /**
     * JSON OBJECT
     */
    public String operateJsonObj;

    /**
     * MsgPk
     */
    public String operateMsgPk;

    private String operateEventTypePk;

    /**
     * 最後下載時間
     */
    public long operateLastSaveTime;

    ///////////////////////////////////////////////////////////
    /**
     * 類別
     */
    private String operateSubkind;

    /**
     * 時間紀錄
     */
    private int operateDate;

    /**
     * 行程名稱
     */
    private String operateName;

    /**
     * 行程地點
     */
    private String operateLocation;

    /**
     * 起始日期
     */
    private String operateStartDate;

    /**
     * 結束日期
     */
    private String operateEndDate;

    /**
     * 起始時間
     */
    private String operateStartTime;

    /**
     * 結束時間
     */
    private String operateEndTime;

    /**
     * 備註
     */
    private String operateNote;

    /**
     * 是否跨日
     */
    private int operateOneDay;

    ///////////////////////////////////////////////////////

    public String getOperationNote() {
        return operationNote;
    }

    public void setOperationNote(String operationNote) {
        this.operationNote = operationNote;
    }

    public String getOperateLocation() {
        return operateLocation;
    }

    public void setOperateLocation(String operateLocation) {
        this.operateLocation = operateLocation;
    }

    public String getOperateStartDate() {
        return operateStartDate;
    }

    public void setOperateStartDate(String operateStartDate) {
        this.operateStartDate = operateStartDate;
    }

    public String getOperateEndDate() {
        return operateEndDate;
    }

    public void setOperateEndDate(String operateEndDate) {
        this.operateEndDate = operateEndDate;
    }

    public String getOperateStartTime() {
        return operateStartTime;
    }

    public void setOperateStartTime(String operateStartTime) {
        this.operateStartTime = operateStartTime;
    }

    public String getOperateEndTime() {
        return operateEndTime;
    }

    public void setOperateEndTime(String operateEndTime) {
        this.operateEndTime = operateEndTime;
    }

    public int getOperateOneDay() {
        return operateOneDay;
    }

    public void setOperateOneDay(int operateOneDay) {
        this.operateOneDay = operateOneDay;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 取得(來源+科別)
     *
     * @return String
     */
    public String getOperateSubkind() {
        return operateSubkind;
    }

    /**
     * 設置(來源+科別)
     *
     * @param operateSubkind 來源+科別
     */
    public void setOperateSubkind(String operateSubkind) {
        this.operateSubkind = operateSubkind;
    }

    /**
     * 取得(科別+姓名)
     *
     * @return String
     */
    public String getOperateTitle() {
        return operateTitle;
    }

    /**
     * 設置(科別+姓名)
     *
     * @param operateTitle 科別+姓名
     */
    public void setOperateTitle(String operateTitle) {
        this.operateTitle = operateTitle;
    }

    /**
     * 取得來源
     *
     * @return String
     */
    public String getOperateFrom() {
        return operateFrom;
    }

    /**
     * 設置來源
     *
     * @param operateFrom 來源
     */
    public void setOperateFrom(String operateFrom) {
        this.operateFrom = operateFrom;
    }

    /**
     * 取得開刀日期
     *
     * @return int
     */
    public int getOperateDate() {
        return operateDate;
    }

    /**
     * 設置開刀日期
     *
     * @param operateDate 開刀日期
     */
    public void setOperateDate(int operateDate) {
        this.operateDate = operateDate;
    }

    /**
     * 取得預計開刀日期
     *
     * @return String
     */
    public String getOperateTime() {
        return operateTime;
    }

    /**
     * 取得預計開刀日期
     *
     * @param OperateTime 預計開刀日期
     */
    public void setOperateTime(String OperateTime) {
        this.operateTime = OperateTime;
    }

    /**
     * 取得病歷號
     *
     * @return String
     */
    public String getOperateMedicalNumber() {
        return operateMedicalNumber;
    }

    /**
     * 設置病歷號
     *
     * @param operateMedicalNumber 病歷號
     */
    public void setOperateMedicalNumber(String operateMedicalNumber) {
        this.operateMedicalNumber = operateMedicalNumber;
    }

    /**
     * 取得姓名
     *
     * @return String
     */
    public String getOperateName() {
        return operateName;
    }

    /**
     * 設置姓名
     *
     * @param operateName 姓名
     */
    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    /**
     * 取得科別
     *
     * @return String
     */
    public String getOperateDivision() {
        return operateDivision;
    }

    /**
     * 設置科別
     *
     * @param operateDivision 科別
     */
    public void setOperateDivision(String operateDivision) {
        this.operateDivision = operateDivision;
    }

    /**
     * 取得主診斷
     *
     * @return String
     */
    public String getOperateDiagnosis() {
        return operateDiagnosis;
    }

    /**
     * 設置主診斷
     *
     * @param operateDiagnosis 主診斷
     */
    public void setOperateDiagnosis(String operateDiagnosis) {
        this.operateDiagnosis = operateDiagnosis;
    }

    /**
     * 取得手術名稱
     *
     * @return String
     */
    public String getOperation() {
        return operation;
    }

    /**
     * 設置手術名稱
     *
     * @param operation 手術名稱
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * 取得備註
     *
     * @return String
     */
    public String getOperateNote() {
        return operationNote;
    }

    /**
     * 設置備註
     *
     * @param operationNote 備註
     */
    public void setOperateNote(String operationNote) {
        this.operationNote = operationNote;
    }

    /**
     * 取得JSONObject
     *
     * @return String
     */
    public String getOperateJsonObj() {
        return operateJsonObj;
    }

    /**
     * 設置JSONObject
     *
     * @param operateJsonObj JSONObject
     */
    public void setOperateJsonObj(String operateJsonObj) {
        this.operateJsonObj = operateJsonObj;
    }

    /**
     * 取得MsgPk
     *
     * @return String
     */
    public String getOperateMsgPk() {
        return operateMsgPk;
    }

    /**
     * 設置MsgPk
     *
     * @param operateMsgPk MsgPk
     */
    public void setOperateMsgPk(String operateMsgPk) {
        this.operateMsgPk = operateMsgPk;
    }

    public String getOperateEventTypePk() {
        return operateEventTypePk;
    }

    public void setOperateEventTypePk(String operateEventTypePk) {
        this.operateEventTypePk = operateEventTypePk;
    }

    /**
     * 取得最後下載時間
     *
     * @return long
     */
    public long getOperateLastSaveTime() {
        return operateLastSaveTime;
    }

    /**
     * 設置最後下載時間
     *
     * @param operateLastSaveTime 最後下載時間
     */
    public void setOperateLastSaveTime(long operateLastSaveTime) {
        this.operateLastSaveTime = operateLastSaveTime;
    }
}
