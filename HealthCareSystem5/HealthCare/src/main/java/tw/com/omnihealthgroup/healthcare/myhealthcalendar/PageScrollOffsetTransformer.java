package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.support.v4.view.ViewPager;
import android.view.View;

public class PageScrollOffsetTransformer implements ViewPager.PageTransformer {
    /**
     * position參數對應指定頁面相對於畫面中心的位置。它是一個動態屬性，會隨著頁面的滑動而改變。
     * 當一個頁面（page)填充整個畫面時，positoin值為0；
     * 當一個頁面（page)剛剛離開畫面右(左）側時，position值為1（-1）；
     * 當兩個頁面分別滑動到一半時，其中一個頁面是 -0.5，另一個頁面是 0.5。
     * 基於畫面上頁面的位置，通過例如setAlpha()、setTranslationX()或setScaleY()方法來設置頁面的屬性，創建自定義的滑動動畫。
     */
    @Override
    public void transformPage(View page, float position) {
        //  左右暫存頁偏移100
        page.setTranslationX(-100 * position);
    }
}
