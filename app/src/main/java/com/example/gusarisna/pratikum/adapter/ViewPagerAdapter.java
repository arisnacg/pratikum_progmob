package com.example.gusarisna.pratikum.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> listFragment = new ArrayList<>();
    private final List<String> listNama = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return listFragment.get(i);
    }

    @Override
    public int getCount() {
        return listNama.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listNama.get(position);
    }

    public void addFragment(Fragment fragment, String nama){
        listFragment.add(fragment);
        listNama.add(nama);
    }


}
