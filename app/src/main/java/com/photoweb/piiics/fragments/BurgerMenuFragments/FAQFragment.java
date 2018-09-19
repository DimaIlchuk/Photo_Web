package com.photoweb.piiics.fragments.BurgerMenuFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.FAQAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.FAQReceiver;
import com.photoweb.piiics.model.PriceReferences.StickerCategory;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomas on 06/09/2017.
 */

public class FAQFragment extends BaseFragment {
    private static final String LOG_TAG = "FAQFragment";

    MainActivity activity;
    FAQAdapter adapter;

    @BindView(R.id.recycler_view)
    RecyclerView faqRV;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.emptyView)
    TextView emptyView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_faq;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        faqRV.setLayoutManager(layoutManager);
        adapter = new FAQAdapter();
        faqRV.setAdapter(adapter);
        requestFAQ();
    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.FAQ);
    }

    private void requestFAQ() {
        progressBar.setVisibility(View.VISIBLE);

        Call<FAQReceiver> requestCall = BackendAPI.piiicsapi.requestFAQ(getString(R.string.LANG));
        requestCall.enqueue(new Callback<FAQReceiver>() {
            @Override
            public void onResponse(Call<FAQReceiver> call, Response<FAQReceiver> response) {
                Log.i(LOG_TAG, "response : " + call.request().url().toString());

                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    adapter.updateModel(response.body());
                    Log.i(LOG_TAG, "Get FAQReceiver good");
                    handleEmptyView();
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " + String.valueOf(response.code()));
                    if (response.code() == 404) {
                        Log.i(LOG_TAG, "URL invalide");
                    } else {
                        Log.i(LOG_TAG, "Error");
                    }
                    handleEmptyView();
                }
            }

            @Override
            public void onFailure(Call<FAQReceiver> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                handleEmptyView();
                NetworkUtils.checkConnectedToANetwork(getActivity(), false);
            }
        });
    }

    private void handleEmptyView() {
        if (adapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

}
