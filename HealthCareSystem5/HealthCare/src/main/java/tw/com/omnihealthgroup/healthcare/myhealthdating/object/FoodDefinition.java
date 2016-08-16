package tw.com.omnihealthgroup.healthcare.myhealthdating.object;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/2.
 */
public class FoodDefinition implements Serializable {
    private long _id; //食品分類
    private String type; //食品分類
    private String name; //食品名稱
    private String content; //內容物描述
    private String unit; //單位
    private String amount; //每份食物單位
    private String amountunit; //每份食物單位(g/ml)
    private String refimgsn; //每份食物單位參考圖
    private String moisture; //每單位熱量(kcal)
    private String protein; //每單位蛋白質(g)
    private String fat; //每單位脂肪(g)
    private String sugar; //每單位碳水化合物(g)
    private String note; //狀態
    private String status; //狀態
    private String crTime; //建立時間
    private String mdTime; //修改時間

    //    public PatientIndexItem()
    //    {}
    //
    //    public PatientIndexItem(long id, String area, String dept_manager, String station_manager, String vs
    //            , String np, String ns, String emerg)
    //    {
    //        this.setId(id);
    //        this.setArea(area);
    //        this.setDept_manager(dept_manager);
    //        this.setStation_manager(station_manager);
    //        this.setVs(vs);
    //        this.setNp(np);
    //        this.setNs(ns);
    //        this.setEmerg(emerg);
    //    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountunit() {
        return amountunit;
    }

    public void setAmountunit(String amountunit) {
        this.amountunit = amountunit;
    }

    public String getRefimgsn() {
        return refimgsn;
    }

    public void setRefimgsn(String refimgsn) {
        this.refimgsn = refimgsn;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrTime() {
        return crTime;
    }

    public void setCrTime(String crTime) {
        this.crTime = crTime;
    }

    public String getMdTime() {
        return mdTime;
    }

    public void setMdTime(String mdTime) {
        this.mdTime = mdTime;
    }
}
