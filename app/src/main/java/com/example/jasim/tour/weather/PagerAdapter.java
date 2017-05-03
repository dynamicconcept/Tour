package com.example.jasim.tour.weather;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jasim.tour.weather.Helpers.DayFormatter;
import com.example.jasim.tour.weather.Model.Mode;



public class PagerAdapter extends FragmentPagerAdapter {

   Mode mode;
    Context context;

    public PagerAdapter(FragmentManager fragmentManager, Context context, Mode mode) {
        super(fragmentManager);
        if (fragmentManager.getFragments() != null) {
            fragmentManager.getFragments().clear();
        }
        this.mode = mode;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        ItemsFragmentActivity fragment = new ItemsFragmentActivity();
        fragment.getInstance(position,mode);
        return fragment;
    }

    @Override
    public int getCount() {
        return mode.getList().size();
    }

    public CharSequence getPageTitle(int position) {
             DayFormatter dayFormatter=new DayFormatter(context);
        return dayFormatter.format(mode.getList().get(position).getDt());
    }
}