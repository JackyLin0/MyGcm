package tw.com.omnihealthgroup.drawerframework;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;

public class DrawerFrameworkLayoutFragment extends AbstractDrawerFrameworkLayoutFragment {

    @LayoutRes
    protected int getDrawerLayoutRes() {
        return R.layout.drawerframework_layout;
    }

    @IdRes
    protected int getDrawerMainResId() {
        return R.id.DrawerMainResId;
    }

    @IdRes
    protected int getDrawerMenuResId() {
        return R.id.DrawerMenuResId;
    }

    @LayoutRes
    protected int getNavigationHeaderRes() {
        return R.layout.nv_headerlayout;
    }

    @MenuRes
    protected int getNavigationMenuRes() {
        return R.menu.nv_menu;
    }
}
