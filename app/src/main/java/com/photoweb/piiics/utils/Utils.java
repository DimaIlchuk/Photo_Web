package com.photoweb.piiics.utils;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.regions.Regions;
import com.photoweb.piiics.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by dnizard on 25/04/2017.
 */

public class Utils {

    public static final String MESSENGER_INTENT_KEY
            = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    public static final int MSG_UNCOLOR_START = 0;
    public static final int MSG_UNCOLOR_STOP = 1;
    public static final int MSG_COLOR_START = 2;
    public static final int MSG_COLOR_STOP = 3;
    public static final String WORK_DURATION_KEY =
            BuildConfig.APPLICATION_ID + ".WORK_DURATION_KEY";

    public static String package_gabarit = "com.photoweb.piiics.utils.gabarits.";

    public static String API_URL = "https://ws.piiics.com/api/";
    public static String INSTA_URL = "https://dev.appndesk.com/piiics.php";

    public static String AWS_BUCKET = "piiicss3";
    public static String AWS_POOL = "eu-west-1:981147e6-7b26-4091-89d3-44cf6ecebbbc";
    public static Regions AWS_REGION = Regions.EU_WEST_1;

    public static int stdBig = 1796;
    public static int stdSmall = 1205;
    public static int panoBig = 2138;

    public static int pageWidth = 2251;
    public static int pageHeight = 2305;

    public static String defaultPageBg = "Blanc - Bloc.jpg";

    //public static String confirmationColorButton = 0x04ccc2

    public static String PSPID = "PIICS";
    public static String passphrase = "PiiicsTest@2017!";
    public static String acceptURL = "https://sys-bo.com/piiics/ok.php";
    public static String failureURL = "https://sys-bo.com/piiics/nok.php";
    public static String ogoneURL = "https://secure.ogone.com/Tokenization/HostedPage?";
    public static String paymentURL = "https://sys-bo.com/piiics/payment.php";

    //Stripe
    public static String STRIPE_PUBLISHABLE_KEY =
            "pk_live_h6Y4RuR9HJKbotJdFvVqqbq9";

    public static String[] listCountries = {"FR", "DE", "IT", "ES", "NL",
            "AT", "BE", "BG", "CY", "HR", "DK", "EE", "FI",
            "GR", "HU", "IE", "LV", "LT", "LU", "MT", "PL",
            "PT", "CZ", "RO", "GB", "SK", "SI", "SE"};

    /**
     * An instance of Picasso, customized to have Auth header added
     */
    private static Picasso authPicasso = null;

    /**
     * Get a reference to Picasso, modified to add Authorization header to network calls
     * @param ctx
     * @param authToken
     * @return
     */
    public static Picasso getAuthPicasso(Context ctx, final String authToken) {
        if(authPicasso == null) {
            Picasso.Builder builder = new Picasso.Builder(ctx);

            builder.downloader(new UrlConnectionDownloader(ctx) {
                @Override
                protected HttpURLConnection openConnection(Uri uri) throws IOException {
                    HttpURLConnection connection = super.openConnection(uri);
                    connection.setRequestProperty (GoogleConstants.HEADER_NAME_AUTH,
                            GoogleConstants.HEADER_AUTH_VAL_PRFX + authToken);
                    return connection;
                }
            });

            authPicasso = builder.build();
        }
        return authPicasso;
    }

}
