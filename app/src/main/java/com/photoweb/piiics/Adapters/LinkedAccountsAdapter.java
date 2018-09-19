package com.photoweb.piiics.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments.SettingsFragment;
import com.photoweb.piiics.model.FAQReceiver;

import java.util.ArrayList;

/**
 * Created by thomas on 06/09/2017.
 */

public class LinkedAccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "LinkedAccountsAdapter";

    ArrayList<SettingsFragment.SocialAccount> socialAccounts;

    public LinkedAccountsAdapter(ArrayList<SettingsFragment.SocialAccount> socialAccounts) {
        this.socialAccounts = socialAccounts;
    }

    @Override
    public SocialAccountItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        SocialAccountItem vh;

        // create a new view
        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_account_linked_accounts, parent, false);

        vh = new SocialAccountItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SocialAccountItem socialAccountItem = (SocialAccountItem) holder;
        final SettingsFragment.SocialAccount content = socialAccounts.get(position);

        socialAccountItem.accountNameTV.setText(content.getSocialAccountName());
        if (content.isUserConnected()) {
            socialAccountItem.accountIsConnectedTV.setText(R.string.CONNECTED);
            socialAccountItem.accountIconIV.setImageResource(R.drawable.profil_deco);
        } else {
            socialAccountItem.accountIsConnectedTV.setText(R.string.NOT_CONNECTED);
            socialAccountItem.accountIconIV.setImageResource(content.getDrawableIcon());
        }

        socialAccountItem.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "OnClick - " + content.getSocialAccountName());
                //todo : lancer les connections
            }
        });
    }

    @Override
    public int getItemCount() {
        return socialAccounts.size();
    }

    public static class SocialAccountItem extends RecyclerView.ViewHolder {

        LinearLayout mainView;

        ImageView accountIconIV;

        TextView accountNameTV;
        TextView accountIsConnectedTV;

        public SocialAccountItem(LinearLayout mainView) {
            super(mainView);
            this.mainView = mainView;
            this.accountIconIV = (ImageView) mainView.findViewById(R.id.accountIcon);
            this.accountNameTV = (TextView) mainView.findViewById(R.id.accountName);
            this.accountIsConnectedTV = (TextView) mainView.findViewById(R.id.accountIsConnected);
        }
    }
}
