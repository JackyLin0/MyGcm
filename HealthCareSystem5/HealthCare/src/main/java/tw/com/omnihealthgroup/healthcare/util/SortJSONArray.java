package tw.com.omnihealthgroup.healthcare.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class SortJSONArray implements Comparator<JSONObject> {
    public static final String TAG = "SortJSONArray：";

    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        try {
            Integer objTime1 = lhs.getInt("公告日期");
            Integer objTime2 = rhs.getInt("公告日期");
            System.out.println(TAG + objTime1 + " / " + objTime2);

            Integer result = objTime1.compareTo(objTime2);
            System.out.println(TAG + result);

            //  日期較新的在前面
            return (result > 0 ? -1 : result < 0 ? 1 : 0);
            //  日期較舊的在前面
//            return (result > 0 ? 1 : result < 0 ? -1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
