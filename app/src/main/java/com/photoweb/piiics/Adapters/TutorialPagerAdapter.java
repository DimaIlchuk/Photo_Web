package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.photoweb.piiics.fragments.BurgerMenuFragments.TutorialPageFragment;

/**
 * Created by thomas on 07/09/2017.
 */

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TutorialPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        TutorialPageFragment tutorialPageFragment = new TutorialPageFragment();
        tutorialPageFragment.setViewPagerPosition(position);
        return (tutorialPageFragment);
    }

    @Override
    public int getCount() { return (3);}
}
