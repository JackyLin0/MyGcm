package tw.com.omnihealthgroup.drawerframework;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractDrawerFrameworkMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v;
        if (view == null) return;
        if ((v = view.findViewById(getToolbarResId())) instanceof Toolbar) {
            onSetToolbar((Toolbar) v);
        }
        if ((v = view.findViewById(getContentResId())) instanceof ViewGroup) {
            onSetContent((ViewGroup) v);
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = getToolbar();
        Fragment fragment = getParentFragment();
        if (toolbar != null && fragment instanceof DrawerFrameworkLayoutFragment) {
            ((DrawerFrameworkLayoutFragment) fragment).setToolbar(toolbar);
        }
    }

    public final Toolbar getToolbar() {
        View toolbar = null;
        View view;
        boolean b;
        if ((view = getView()) != null) {
            if ((toolbar = view.findViewById(getToolbarResId())) instanceof Toolbar) {
                b = true;
            } else {
                b = false;
            }
        }
        else {
            b = false;
        }
        if (b) {
            return (Toolbar) toolbar;
        }
        else {
            return null;
        }
    }

    protected void onSetToolbar(Toolbar toolbar) {}

    protected void onSetContent(ViewGroup contentView) {}

    @LayoutRes
    protected abstract int getLayoutRes();

    @IdRes
    protected abstract int getToolbarResId();

    @IdRes
    protected abstract int getContentResId();
}
