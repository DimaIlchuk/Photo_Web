package com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.CommandDetailsAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by thomas on 08/09/2017.
 */

public class CommandCompletedDetailsFragment extends BaseFragment {
    private static final String LOG_TAG = "CommandDetailsFrag";
    MainActivity activity;
    Toolbar toolbar;
    UserCommandCompleted commandCompleted;

    @BindView(R.id.date)
    TextView dateTV;

    @BindView(R.id.price)
    TextView priceTV;

    @BindView(R.id.commandStatus)
    TextView commandStatusTV;

    @BindView(R.id.tv_product) TextView productTV;
    @BindView(R.id.tv_order) TextView orderTV;
    @BindView(R.id.tv_address) TextView addressTV;
    @BindView(R.id.tv_estimate) TextView estimateTV;
    @BindView(R.id.follow) TextView followTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_account_command_details;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity) getActivity());
        commandCompleted = activity.getSelectedCommand();
        setCommandCompletedDetailsToolbar();
        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Log.d(LOG_TAG, commandCompleted.toString());
    }

    private void setCommandCompletedDetailsToolbar() {
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar = activity.getToolbar();

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(commandCompleted.getCommandTitle());

        ImageView burgerMenuIcon = toolbar.findViewById(R.id.burger_menu_icon);
        burgerMenuIcon.setVisibility(GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "IN OPTIONS SELECTED");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(LOG_TAG, "POP BACK STACK");
                activity.getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateTV.setText(commandCompleted.getFormattedDate());
        priceTV.setText(commandCompleted.getFormattedPrice());
        commandStatusTV.setText(commandCompleted.getFormattedStatus());

        productTV.setText(commandCompleted.getProduct().equals("PRINT") ? getString(R.string.PRINT) : getString(R.string.ALBUM));
        orderTV.setText(getString(R.string.CMD_NB) + commandCompleted.getAcceptance());

        addressTV.setText(Html.fromHtml(commandCompleted.getDeliveryAddress() == null ? "<br><br>" : commandCompleted.getDeliveryAddress()));

        if(commandCompleted.getShipmentTrackURL() == null || commandCompleted.getShipmentTrackURL().isEmpty() || commandCompleted.getShipmentTrackURL().equals("")){
            followTV.setVisibility(GONE);
        }else{
            followTV.setVisibility(View.VISIBLE);
        }

        Date date = commandCompleted.getRealDate();
        int printMin = commandCompleted.getProduct().equals("PRINT") ? 2 : 3;
        int printMax = commandCompleted.getProduct().equals("PRINT") ? 4 : 5;
        int deliveryMin = 2;
        int deliveryMax = 5;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, printMin + deliveryMin);
        Date dateMin = c.getTime();

        c.setTime(date);
        c.add(Calendar.DATE, printMax + deliveryMax);
        Date dateMax = c.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        estimateTV.setText(getString(R.string.ESTIMATION, sdf.format(dateMin), sdf.format(dateMax)));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setDefaultToolbar();
        activity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void setDefaultToolbar() {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ImageView burgerMenuIcon = (ImageView) toolbar.findViewById(R.id.burger_menu_icon);
        burgerMenuIcon.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.follow)
    public void onFollowClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commandCompleted.getShipmentTrackURL()));
        startActivity(browserIntent);
    }

    @OnClick(R.id.tv_contact)
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
                        .append("<p>UDID : " + Settings.Secure.getString(getActivity().getContentResolver(),
                                Settings.Secure.ANDROID_ID) + "</p>")
                        .append("<p>"+getString(R.string.EMAIL_NUMBER)+" : " + UserInfo.getInt("id") + "</p>")
                        .toString())
        );

        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
