package com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.photoweb.piiics.Adapters.LinkedAccountsAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 06/09/2017.
 */

public class SettingsFragment extends BaseFragment {

    private static final String LOG_TAG = "SettingsFragment";

    private final String ACCOUNT_NAME_FACEBOOK = "Facebook";
    private final String ACCOUNT_NAME_GOOGLE_PHOTOS = "Google Photos";
    private final String ACCOUNT_NAME_INSTAGRAM = "Instagram";
    private final String ACCOUNT_NAME_DROPBOX = "Dropbox";

    ArrayList<SocialAccount> socialAccounts;

    @BindView(R.id.recyclerView)
    RecyclerView linkedAccountsRV;

    @BindView(R.id.switchPartnerOffers)
    Switch partnerOffersSW;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_account_settings;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPartnerOffersSwitch();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linkedAccountsRV.setLayoutManager(layoutManager);

        //chopper les comptes disponibles et check si l'user et co ou pas

        initAccounts();

        LinkedAccountsAdapter adapter = new LinkedAccountsAdapter(socialAccounts);
        linkedAccountsRV.setAdapter(adapter);
    }

    private void initPartnerOffersSwitch() {
        partnerOffersSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserInfo.set("optin", isChecked);
            }
        });
    }

    private void initAccounts() {
        ArrayList<SocialAccount> socialAccounts;

        socialAccounts = createAccounts();
        socialAccounts = checkConnectedAccounts(socialAccounts);
        this.socialAccounts = socialAccounts;
    }

    private ArrayList<SocialAccount> createAccounts() {
        ArrayList<SocialAccount> socialAccounts = new ArrayList<>();
        socialAccounts.add(new SocialAccount(R.drawable.facebook_profil, ACCOUNT_NAME_FACEBOOK));
        socialAccounts.add(new SocialAccount(R.drawable.google_photo_profil, ACCOUNT_NAME_GOOGLE_PHOTOS));
        socialAccounts.add(new SocialAccount(R.drawable.instagram_profil, ACCOUNT_NAME_INSTAGRAM));
        socialAccounts.add(new SocialAccount(R.drawable.dropbox_profil, ACCOUNT_NAME_DROPBOX));
        return socialAccounts;
    }

    private ArrayList<SocialAccount> checkConnectedAccounts(ArrayList<SocialAccount> socialAccounts) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String facebookToken = sharedPref.getString("Facebook", null);
        String googleToken = sharedPref.getString("Google", null);
        String instagramToken = sharedPref.getString("INSTAGRAM_access_token", null);
        String dropBoxToken = sharedPref.getString("Dropbox", null);

        Log.i(LOG_TAG, "facebookToken : " + facebookToken);
        Log.i(LOG_TAG, "googleToken : " + googleToken);
        Log.i(LOG_TAG, "instagramToken : " + instagramToken);
        Log.i(LOG_TAG, "dropBoxToken : " + dropBoxToken);


        for (SocialAccount socialAccount : socialAccounts) {
            if (socialAccount.getSocialAccountName().equals(ACCOUNT_NAME_FACEBOOK)) {
                socialAccount.setUserConnected((facebookToken != null));
            } else if (socialAccount.getSocialAccountName().equals(ACCOUNT_NAME_GOOGLE_PHOTOS)) {
                socialAccount.setUserConnected((googleToken != null));
            } else if (socialAccount.getSocialAccountName().equals(ACCOUNT_NAME_INSTAGRAM)) {
                socialAccount.setUserConnected((instagramToken != null));
            } else if (socialAccount.getSocialAccountName().equals(ACCOUNT_NAME_DROPBOX)) {
                socialAccount.setUserConnected((dropBoxToken != null));
            } else {
                socialAccount.setUserConnected(false);
            }
        }
        return socialAccounts;
    }



    public class SocialAccount {

        int drawableIcon;
        String socialAccountName;
        boolean isUserConnected;

        public int getDrawableIcon() {
            return drawableIcon;
        }

        public void setDrawableIcon(int drawableIcon) {
            this.drawableIcon = drawableIcon;
        }

        public String getSocialAccountName() {
            return socialAccountName;
        }

        public void setSocialAccountName(String socialAccountName) {
            this.socialAccountName = socialAccountName;
        }

        public boolean isUserConnected() {
            return isUserConnected;
        }

        public void setUserConnected(boolean userConnected) {
            isUserConnected = userConnected;
        }

        public SocialAccount(int drawableIcon, String socialAccountName) {
            this.drawableIcon = drawableIcon;
            this.socialAccountName = socialAccountName;
            this.isUserConnected = false;
        }
    }
}
