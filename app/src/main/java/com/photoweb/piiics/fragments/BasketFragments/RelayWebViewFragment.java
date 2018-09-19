package com.photoweb.piiics.fragments.BasketFragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.CommandHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dnizard on 01/09/2017.
 */

public class RelayWebViewFragment extends BaseFragment {

    BasketActivity activity;

    @BindView(R.id.webview)
    WebView mWebView;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_webviewpayment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setUpWebView();
    }

    private void setUpWebView() {
        //mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new RelayWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/relay.html");

    }

    private class RelayWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if((url.contains("result.html"))){

                CommandHandler.get().setRelayId((url.split("\\?id="))[1]);
                getFragmentManager().popBackStack();

                return true;

            }


            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

    }

}
