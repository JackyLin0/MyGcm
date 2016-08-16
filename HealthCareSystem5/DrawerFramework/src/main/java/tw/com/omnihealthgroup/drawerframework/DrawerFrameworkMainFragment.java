package tw.com.omnihealthgroup.drawerframework;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

public class DrawerFrameworkMainFragment extends AbstractDrawerFrameworkMainFragment {

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.drawerframework_main;
    }

    @IdRes
    protected int getToolbarResId() {
        return R.id.DrawerToolbarResId;
    }

    @IdRes
    protected int getContentResId() {
        return R.id.DrawerContentResId;
    }
}
