package com.photoweb.piiics.fragments.BurgerMenuFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.MyAccountPagerAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;

/**
 * Created by thomas on 06/09/2017.
 */

public class MyAccountFragment extends BaseFragment {

    MainActivity activity;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.userName)
    TextView userNameTV;

    @BindView(R.id.freePrints)
    TextView freePrintsTV;

    @BindView(R.id.freeAlbum)
    TextView freeAlbumTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_account;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbar();
        setUserInfos();
        initViewPager();
    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.PROFILE);
    }

    private void setUserInfos() {
        int userID = (int) UserInfo.get().get("id");
        if (userID == 0) {
            userNameTV.setText(getString(R.string.CONNECT_ME));
        } else {
            String userName = (String) UserInfo.get().get("username");
            int free = UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");

            userNameTV.setText(userName);
            freeAlbumTV.setText(getString(R.string.ONE_LEFT, UserInfo.getInt("book_available"), getString(R.string.SINGLE_ALBUM)));
            freePrintsTV.setText(free > 1 ? getString(R.string.LEFT, free, getString(R.string.PRINT)) : getString(R.string.ONE_LEFT, free, getString(R.string.SINGLE_PRINT)));
        }
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(2);
        MyAccountPagerAdapter adapter = new MyAccountPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
