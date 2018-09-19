package com.photoweb.piiics.fragments.LoginFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photoweb.piiics.BuildConfig;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.LoginActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.BookReference;
import com.photoweb.piiics.model.PriceReferences.FormatAndBookReferenceGeneral;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.model.PriceReferences.StickerCategory;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.DraftsUtils;
import com.photoweb.piiics.utils.NetworkUtils;
import com.photoweb.piiics.utils.PopUps;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 14/04/2017.
 */

public class SplashscreenFragment extends BaseFragment {
    private static final String LOG_TAG = "SplashScreenFragment";

    LoginActivity activity;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.version)
    TextView version;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_splashscreen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((LoginActivity) getActivity());
        activity.startAWSrequests();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        //version.setText("" + BuildConfig.VERSION_CODE);

        initApp();

    }

    private void initApp() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(LOG_TAG, "init app");

        //SocialHandler.get().init(activity);//le laisser ici ? verifier son retour ?

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Log.d(LOG_TAG, "after 300ms");
                DraftsUtils.createDraftDirectories(getContext());
                requestInfosPrice();
            }
        }, 300);


    }

    private void requestInfosPrice() {
        requestBackgroundReferences();
        //requestFormatAndBookReferences();
        //requestStickersCategoryReferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //toolbar.setVisibility(View.GONE);
    }

    private void requestBackgroundReferences() {
        Call<ArrayList<BackgroundReference>> requestCall = BackendAPI.piiicsapi.requestBackgroundReferences();

        requestCall.enqueue(new Callback<ArrayList<BackgroundReference>>() {
            @Override
            public void onResponse(Call<ArrayList<BackgroundReference>> call, Response<ArrayList<BackgroundReference>> response) {
                Log.i(LOG_TAG, "response : " + call.request().url().toString());
                if (response.isSuccessful()) {
                    Log.i(LOG_TAG, "Get backgroudReferences good");
                    PriceReferences.setBackgrounds(response.body());
                    for (BackgroundReference backgroundReference : PriceReferences.getBackgrounds()) {
                        Log.i(LOG_TAG, "background name : " + backgroundReference.getName());
                    }
                    PriceReferences.setBackgroundReferencesDL(true);
                    requestFormatAndBookReferences();
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " + String.valueOf(response.code()));
                    if (response.code() == 404) {
                        Log.i(LOG_TAG, "URL invalide");
                    } else {
                        Log.i(LOG_TAG, "Error");
                    }
                    progressBar.setVisibility(View.GONE);
                    PopUps.popUpFatalError(activity, "Erreur", "Une erreur est survenue, merci de réessayer plus tard");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BackgroundReference>> call, Throwable t) {
                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);
                if (!NetworkUtils.checkConnectedToANetwork(activity, false)) {
                    PopUps.popUpFatalError(activity, "Aucune connexion", "Merci de verifier votre connexion internet");
                }
            }
        });
    }

    private void requestFormatAndBookReferences() {
        Call<FormatAndBookReferenceGeneral> requestCall = BackendAPI.piiicsapi.requestFormatAndBookReferences();

        requestCall.enqueue(new Callback<FormatAndBookReferenceGeneral>() {
            @Override
            public void onResponse(Call<FormatAndBookReferenceGeneral> call, Response<FormatAndBookReferenceGeneral> response) {
                Log.i(LOG_TAG, "response : " + call.request().url().toString());
                if (response.isSuccessful()) {
                    Log.i(LOG_TAG, "Get formatAndBookReferences good");
                    splitFormatAndBookReferences(response.body());
                    requestStickersCategoryReferences();
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " + String.valueOf(response.code()));
                    if (response.code() == 404) {
                        Log.i(LOG_TAG, "URL invalide");
                    } else {
                        Log.i(LOG_TAG, "Error");
                    }
                    progressBar.setVisibility(View.GONE);
                    PopUps.popUpFatalError(activity, "Erreur", "Une erreur est survenue, merci de réessayer plus tard");
                }
            }

            @Override
            public void onFailure(Call<FormatAndBookReferenceGeneral> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (!NetworkUtils.checkConnectedToANetwork(activity, false)) {
                    PopUps.popUpFatalError(activity, "Aucune connexion", "Merci de verifier votre connexion internet");
                }
            }
        });
    }

    private void splitFormatAndBookReferences(FormatAndBookReferenceGeneral formatAndBookReferenceGeneral) {
        ArrayList<FormatReference> formatReferences = new ArrayList<>();
        ArrayList<BookReference> bookReferences = new ArrayList<>();

        for (FormatReference formatOrBookReference : formatAndBookReferenceGeneral.getFormatAndBookReferences()) {
            if (formatOrBookReference.getName().equals(PriceReferences.STANDARD_FORMAT) ||
                    formatOrBookReference.getName().equals("square") ||
                    formatOrBookReference.getName().equals("panoramic") ||
                    formatOrBookReference.getName().equals("page")) {
                formatReferences.add(formatOrBookReference);
            } else {
                BookReference bookReference = new BookReference(formatOrBookReference);
                bookReferences.add(bookReference);
            }
        }
        PriceReferences.setFormats(formatReferences);
        PriceReferences.setBookReferences(bookReferences);
    }

    private void requestStickersCategoryReferences() {
        Call<ArrayList<StickerCategory>> requestCall = BackendAPI.piiicsapi.requestStickerCategoryReferences();

        requestCall.enqueue(new Callback<ArrayList<StickerCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<StickerCategory>> call, Response<ArrayList<StickerCategory>> response) {
                Log.i(LOG_TAG, "response : " + call.request().url().toString());
                if (response.isSuccessful()) {
                    Log.i(LOG_TAG, "Get stickerCategoryReferences good");
                    PriceReferences.setStickerCategories(response.body());
                    PriceReferences.setStickerReferencesDL(true);
                    checkRequestsFinishes();
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " + String.valueOf(response.code()));
                    if (response.code() == 404) {
                        Log.i(LOG_TAG, "URL invalide");
                    } else {
                        Log.i(LOG_TAG, "Error");
                    }
                    if(progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    PopUps.popUpFatalError(activity, "Erreur", "Une erreur est survenue, merci de réessayer plus tard");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<StickerCategory>> call, Throwable t) {
                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);
                if (!NetworkUtils.checkConnectedToANetwork(activity, false)) {
                    PopUps.popUpFatalError(activity, "Aucune connexion", "Merci de verifier votre connexion internet");
                }
            }
        });
    }

    private void checkRequestsFinishes() {
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);

            initDefaultPriceReferences();

            loadNextView();
        }
    }

    private void initDefaultPriceReferences() {
        if (PriceReferences.getDefaultBackground() == null) {
            Log.i(LOG_TAG, "DEFAULT BACKGROUND DONT EXIST");
            //default background not finded : error message + kill app ?
        }

        if (PriceReferences.getDefaultformat() == null) {
            Log.i(LOG_TAG, "DEFAULT FORMAT DONT EXIST");
            //default format not finded : error message + kill app ?
        }
    }

    private void loadNextView() {
        if (UserInfo.getInt("id") == 0) {
            if (!firstTimeTutorialIsRead()) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new TutorialFirstTimeFragment())
                        .commitAllowingStateLoss();
            } else {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commitAllowingStateLoss();
            }
        } else {
            activity.sendHome();
        }
    }

    private boolean firstTimeTutorialIsRead() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.firstTimeTutorialIsRead), false);
    }

}
