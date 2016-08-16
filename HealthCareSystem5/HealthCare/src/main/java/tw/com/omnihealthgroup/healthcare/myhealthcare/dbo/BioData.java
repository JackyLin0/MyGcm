package tw.com.omnihealthgroup.healthcare.myhealthcare.dbo;

public class BioData {
    //BIO_DATA
    String _id;
    // USER_ID  使用者帳號
    String userId;
    // DEVICE_SN  使用者設備序號
    String deviceSn;
    //設備Mac Address
    String deviceMac;
    // DEVICE_TIME  量測時間(Device)
    String deviceTime;
    // DEVICE_ID  量測紀錄編號
    String deviceId;
    // DEVICE_TYPE  量測類型		// (1:體重、2:血糖、3:血壓)
    String deviceType;
    // READ_TIME  手機讀取時間
    String readTime;
    // SEX 性別    1:男生
    String sex;
    // AGE 年齡
    String age;
    // BODY_HEIGHT  身高
    String bodyHeight;
    // BODY_WEIGHT  體重
    String bodyWeight;
    // BMI 身體質量指數
    String bmi;
    // AC  飯前血糖
    String ac;
    // PC  飯後血糖
    String pc;
    // NM  隨機血糖(不知道是飯前或飯後，無法分類)
    String nm;
    // BHP  收縮壓
    String bhp;
    // BLP  舒張壓
    String blp;
    // PULSE  脈搏
    String pulse;
    // TEMPERATURE  體溫
    String temperature;
    // HEADCIRCUMFERENCE  頭圍
    String headcircumference;
    // ARMCIRCUMFERENCE  上臂圍
    String armcircumference;
    //WAISTLINE  腰圍
    String waistline;
    // HIPS  臀圍
    String hips;
    // UPLOADED  使否上傳(1.已上傳,0未上傳)
    int uploaded;
    // inputType  資料來源(Device:儀器, Manual:手動輸入)
    String inputType;

    public BioData() {

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getBodyHeight() {
        return bodyHeight;
    }

    public void setBodyHeight(String bodyHeight) {
        this.bodyHeight = bodyHeight;
    }

    public String getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(String bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getBhp() {
        return bhp;
    }

    public void setBhp(String bhp) {
        this.bhp = bhp;
    }

    public String getBlp() {
        return blp;
    }

    public void setBlp(String blp) {
        this.blp = blp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHeadcircumference() {
        return headcircumference;
    }

    public void setHeadcircumference(String headcircumference) {
        this.headcircumference = headcircumference;
    }

    public String getArmcircumference() {
        return armcircumference;
    }

    public void setArmcircumference(String armcircumference) {
        this.armcircumference = armcircumference;
    }

    public String getWaistline() {
        return waistline;
    }

    public void setWaistline(String waistline) {
        this.waistline = waistline;
    }

    public String getHips() {
        return hips;
    }

    public void setHips(String hips) {
        this.hips = hips;
    }

}
