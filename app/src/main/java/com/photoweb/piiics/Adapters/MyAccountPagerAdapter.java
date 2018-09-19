package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments.CommandsFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments.PersonnalInformationsFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments.SettingsFragment;

/**
 * Created by thomas on 07/09/2017.
 */

public class MyAccountPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public MyAccountPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            CommandsFragment commandsFragment = new CommandsFragment();
            return (commandsFragment);
        } else if (position == 1) {
            PersonnalInformationsFragment personnalInformationsFragment = new PersonnalInformationsFragment();
            return (personnalInformationsFragment);
        } else {
            SettingsFragment settingsFragment = new SettingsFragment();
            return (settingsFragment);
        }
    }

    @Override
    public int getCount() { return (3);}

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return (mContext.getString(R.string.layoutCommand));
        } else if (position == 1) {
            return mContext.getString(R.string.layoutPersonnalInformations);
        } else {
            return mContext.getString(R.string.layoutSettings);
        }
    }
}
