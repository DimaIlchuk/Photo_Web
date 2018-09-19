package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.photoweb.piiics.fragments.BurgerMenuFragments.TutorialPageFragment;
import com.photoweb.piiics.fragments.LoginFragments.TutorialLastPageFragment;

/**
 * Created by thomas on 07/09/2017.
 */

public class FirstTimeTutorialPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FirstTimeTutorialPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 3) {
            TutorialLastPageFragment tutorialLastPageFragment = new TutorialLastPageFragment();
            return (tutorialLastPageFragment);
        } else {
            TutorialPageFragment tutorialPageFragment = new TutorialPageFragment();
            tutorialPageFragment.setViewPagerPosition(position);
            return (tutorialPageFragment);
        }
    }

    @Override
    public int getCount() { return (4);}
}
