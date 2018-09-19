package com.photoweb.piiics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ad4screen.sdk.A4S;
import com.appsee.Appsee;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.AlbumProductFragment;
import com.photoweb.piiics.fragments.PrintProductFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductPageActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ProductActivity";
    private static final String HOME_FRAGMENT_TAG = "PRODUCT_FRAGMENT_TAG";
    //  public static final String COMMAND_COMPLETED_DETAILS_TAG = "COMMAND_COMPLETED_DETAILS_TAG";

    Toolbar toolbar;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {

            String product = getIntent().getStringExtra("PRODUCT");

            if(product == null || product.equals("PRINT")){
                PrintProductFragment homeFragment = new PrintProductFragment();
                ft.replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT_TAG);
                ft.commit();
            }else{
                AlbumProductFragment homeFragment = new AlbumProductFragment();
                ft.replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT_TAG);
                ft.commit();
            }
        }
    }

    @OnClick(R.id.burger_menu_icon)
    public void onBurgerPress()
    {
        super.onBackPressed();
    }

    /*
     *  Init the toolbar_contract
     */
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            Log.i(LOG_TAG, "TOOLBAR NOT NULL");
        } else {
            Log.i(LOG_TAG, "TOOLBAR NULL");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
        // ...
    }


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
