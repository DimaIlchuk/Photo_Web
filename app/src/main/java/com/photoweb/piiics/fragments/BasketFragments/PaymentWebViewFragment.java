package com.photoweb.piiics.fragments.BasketFragments;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.utils.AeSimpleSHA1;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dnizard on 22/08/2017.
 */

public class PaymentWebViewFragment extends BaseFragment {
    private static final String LOG_TAG = "PaymentWevViewFragment";
    BasketActivity activity;

    @BindView(R.id.webview)
    WebView mWebView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

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

        activity.updateStepsBar(this);
        setUpWebView();
    }

    private void setUpWebView() {
        String stringRequest = "ACCOUNT.PSPID="+Utils.PSPID+"&ALIAS.ORDERID="+ activity.getCommand().getCommandID() +"&CARD.PAYMENTMETHOD=CreditCard&LAYOUT.LANGUAGE="+ getString(R.string.CODE_LANG) +"&PARAMETERS.ACCEPTURL="+Utils.acceptURL+"&PARAMETERS.EXCEPTIONURL="+Utils.failureURL+"&";
        String mUrl = "";

        if(((BasketActivity)getActivity()).getDirectLink())
        {
            mUrl = Utils.paymentURL + "?Alias_OrderId=" + activity.getCommand().getCommandID();
        }else{
            try {
                mUrl = Utils.ogoneURL + stringRequest + "SHASIGNATURE.SHASIGN=" + AeSimpleSHA1.SHA1(stringRequest.replace("&", Utils.passphrase));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new IngenicoWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);

    }

    private class IngenicoWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(LOG_TAG, "shouldOverrideUrlLoading " + url);


            if((url.endsWith("piiics.com/success"))){

                /*if (AWSHandler.get().getPercentUpload() != 100) {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    UploadCommandFragment uploadCommandFragment = new UploadCommandFragment();
                    ft.replace(R.id.fragment_container, uploadCommandFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {

                }*/

                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                ft.replace(R.id.fragment_container, confirmationFragment);
                ft.addToBackStack(null);
                ft.commit();

                return true;
            }

            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i(LOG_TAG, "onreceivedError 1");
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            Log.i(LOG_TAG, "onreceivedError 2");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(LOG_TAG, "onPageStarted");
            view.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(LOG_TAG, "onPageFinished " + url);
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (fragment instanceof PaymentWebViewFragment) {
                view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            if((url.startsWith(Utils.failureURL)) || (url.endsWith("piiics.com/failure"))){
                Toast.makeText(activity, R.string.GENERAL_ERROR, Toast.LENGTH_SHORT).show();
                activity.getSupportFragmentManager().popBackStack();
            }
        }
    }
}
