package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class DialogViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "DialogViewPagerAdapter";
    public ArrayList<Fragment> fragmentList;

    public DialogViewPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragmentList) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}
