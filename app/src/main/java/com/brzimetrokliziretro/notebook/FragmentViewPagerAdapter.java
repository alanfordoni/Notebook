package com.brzimetrokliziretro.notebook;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FragmentViewPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentAll();
        } else if (position == 1){
            return new FragmentPersonal();
        } else if(position == 2){
            return new FragmentBusiness();
        } else {
            return new FragmentToDoList();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.all_notes);
            case 1:
                return mContext.getString(R.string.personal_notes);
            case 2:
                return mContext.getString(R.string.business_notes);
            case 3:
                return mContext.getString(R.string.list_name);
            default:
                return null;
        }
    }
}
