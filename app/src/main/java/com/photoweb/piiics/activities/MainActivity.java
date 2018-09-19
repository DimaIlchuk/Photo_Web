package com.photoweb.piiics.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ad4screen.sdk.A4S;
import com.appsee.Appsee;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BurgerMenuFragments.CGUFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.FAQFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyCreationsFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.SponsorshipFragment;
import com.photoweb.piiics.fragments.BurgerMenuFragments.TutorialFragment;
import com.photoweb.piiics.fragments.HomeFragment;
import com.photoweb.piiics.model.TutorialReceiver;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainACtivity";
    private static final String HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG";
  //  public static final String COMMAND_COMPLETED_DETAILS_TAG = "COMMAND_COMPLETED_DETAILS_TAG";

    Toolbar toolbar;

    TutorialReceiver tutorialReceiver;

    public TutorialReceiver getTutorialReceiver() {
        return tutorialReceiver;
    }

    public void setTutorialReceiver(TutorialReceiver tutorialReceiver) {
        this.tutorialReceiver = tutorialReceiver;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private UserCommandCompleted selectedCommand;

    public UserCommandCompleted getSelectedCommand() {
        return selectedCommand;
    }

    public void setSelectedCommand(UserCommandCompleted selectedCommand) {
        this.selectedCommand = selectedCommand;
    }

    //---------Burger Menu binding-------
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @BindView(R.id.burger_menu)
    LinearLayout burgerMenuLL;

    @BindView(R.id.tv_username)
    TextView tvUserName;

    @BindView(R.id.tv_free_stuff)
    TextView tvFreeStuff;

    @BindView(R.id.edit_icon)
    TextView tvEdit;

    /* burger's views
    @BindView(R.id.bm_email)
    TextView bmEmailTV;

    @BindView(R.id.bm_user_id)
    TextView bmUserIdTV;
    */

    //-----------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        A4S.get(this).setPushNotificationLocked(false);
        A4S.get(this).setInAppDisplayLocked(false);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            HomeFragment homeFragment = new HomeFragment();
            ft.replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT_TAG);
            ft.commit();
        }
    }



    /*
     *  Init the toolbar_contract
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            Log.i(LOG_TAG, "TOOLBAR NOT NULL");
        } else {
            Log.i(LOG_TAG, "TOOLBAR NULL");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @OnClick(R.id.burger_menu_icon)
    public void onBurgerMenuIconClick() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);

        if(UserInfo.getInt("id") == 0){
            tvUserName.setText(getString(R.string.CONNECT_ME));
            tvFreeStuff.setText(getString(R.string.LEFT, 50, getString(R.string.PRINT)));
        }else{
            tvUserName.setText(UserInfo.get("username"));

            int free = UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");

            tvFreeStuff.setText(free > 1 ? getString(R.string.LEFT, free, getString(R.string.PRINT)) : getString(R.string.ONE_LEFT, free, getString(R.string.SINGLE_PRINT)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
        // ...
    }


    //----------burger menu click listeners--------------

    @OnClick({R.id.userInfos, R.id.myAccount})
    public void onMyAccountClick() {
        startBurgerMenuFragment(new MyAccountFragment());
    }

    @OnClick(R.id.home)
    public void onHomeClick() {
        tvEdit.setVisibility(View.GONE);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(currentFragment instanceof HomeFragment)) {
            getSupportFragmentManager().popBackStack();
        }
        drawerLayout.closeDrawer(Gravity.START);
    }

    @OnClick(R.id.myCreations)
    public void onMyCreationsClick() {
        startBurgerMenuFragment(new MyCreationsFragment());
    }

    @OnClick(R.id.sponsorship)
    public void onSponsorshipClick() {
        startBurgerMenuFragment(new SponsorshipFragment());
    }

    @OnClick(R.id.tutorial)
    public void onTutorialClick() {
        startBurgerMenuFragment(new TutorialFragment());
    }

    @OnClick(R.id.faq)
    public void onFAQClick() {
        startBurgerMenuFragment(new FAQFragment());
    }

    @OnClick(R.id.contact)
    public void onContactClick() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","contact@piiics.com", null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Piiics Android]"+getString(R.string.EMAIL_TITLE));
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<html><body><br><br><br><br><p>--- "+getString(R.string.EMAIL_WARNING)+" ----</p>")
                        .append("<p>"+getString(R.string.EMAIL_PHONE)+" : " + Build.MANUFACTURER + " " + Build.MODEL + "</p>")
                        .append("<p>OS : " + Build.VERSION.SDK_INT + "</p>")
                        .append("<p>UDID : " + Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID) + "</p>")
                        .append("<p>"+getString(R.string.EMAIL_NUMBER)+" : " + UserInfo.getInt("id") + "</p>")
                        .toString())
        );

        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @OnClick(R.id.cgu)
    public void onCGUClick()
    {
        startBurgerMenuFragment(new CGUFragment());
    }

    private void startBurgerMenuFragment(Fragment fragment) {

        if(fragment instanceof MyCreationsFragment){
            tvEdit.setVisibility(View.VISIBLE);
        }else{
            tvEdit.setVisibility(View.GONE);
        }

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
        drawerLayout.closeDrawer(Gravity.START);
    }

    //----------------------------------------------------

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        // ...
    }
}
