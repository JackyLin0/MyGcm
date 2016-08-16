package tw.com.omnihealthgroup.drawerframework;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractDrawerFrameworkLayoutFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getDrawerLayoutRes(), container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v;
        if ((v = view) instanceof DrawerLayout) {
            onSetDrawerLayout((DrawerLayout) v);
        }
        if ((v = view.findViewById(getDrawerMainResId())) instanceof ViewGroup) {
            onSetDrawerMain((ViewGroup) v);
        }
        if ((v = view.findViewById(getDrawerMenuResId())) instanceof NavigationView) {
            onSetDrawerMenu((NavigationView) v);
        }
    }

    @NonNull
    @Override
    public final DrawerLayout getView() {
        View view = super.getView();
        if (view instanceof DrawerLayout) return (DrawerLayout) view;
        throw new IllegalArgumentException("super.getView() must be a DrawerLayout!");
    }

    public final void setToolbar(@Nullable Toolbar toolbar) {
        Activity activity = null;
        boolean b;
        if (isResumed() && (activity = getActivity()) instanceof AppCompatActivity) {
            b = true;
        }
        else b = false;
        if (!b) return;
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        /////////////////////////////////////////////////////////////
        DrawerLayout drawerLayout = getView();
        drawerLayout.setDrawerListener(toolbar == null ? null :
                        new ActionBarDrawerToggle(activity, drawerLayout, toolbar, 0, 0) {{
                            syncState();
                        }}
        );
        /////////////////////////////////////////////////////////////
    }

    protected void onSetDrawerLayout(DrawerLayout layoutView){
        layoutView.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    protected void onSetDrawerMain(ViewGroup mainView){}

    protected void onSetMenuHeader(View headerView){}

    @CallSuper
    protected void onSetDrawerMenu(NavigationView menuView){
        if (getNavigationHeaderRes() > 0) {
            onSetMenuHeader(menuView.inflateHeaderView(getNavigationHeaderRes()));
        }
        menuView.inflateMenu(getNavigationMenuRes());
        menuView.setNavigationItemSelectedListener(this);
    }

    @CallSuper
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        getView().closeDrawers();
        return true;
    }

    @LayoutRes
    protected abstract int getDrawerLayoutRes();

    @IdRes
    protected abstract int getDrawerMainResId();

    @IdRes
    protected abstract int getDrawerMenuResId();

    @LayoutRes
    protected abstract int getNavigationHeaderRes();

    @MenuRes
    protected abstract int getNavigationMenuRes();
}
