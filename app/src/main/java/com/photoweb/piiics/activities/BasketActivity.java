package com.photoweb.piiics.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ad4screen.sdk.A4S;
import com.appsee.Appsee;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.photoweb.piiics.PiiicsExceptionHandler;
import com.photoweb.piiics.R;
import com.photoweb.piiics.fragments.BasketFragments.ConfirmationFragment;
import com.photoweb.piiics.fragments.BasketFragments.DeliveryAddressFragment;
import com.photoweb.piiics.fragments.BasketFragments.DeliveryMethodFragment;
import com.photoweb.piiics.fragments.BasketFragments.EmailRequestFragment;
import com.photoweb.piiics.fragments.BasketFragments.OptionLogoFragment;
import com.photoweb.piiics.fragments.BasketFragments.PaymentFragment;
import com.photoweb.piiics.fragments.BasketFragments.PaymentWebViewFragment;
import com.photoweb.piiics.fragments.BasketFragments.SummaryFragment;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.DeliveryMethod;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PaymentMethod;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.Utils;
import com.photoweb.piiics.utils.stripe.PiiicsEphemeralKeyProvider;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.model.Card;
import com.stripe.android.model.Customer;
import com.stripe.android.model.CustomerSource;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceCardData;
import com.stripe.android.view.PaymentMethodsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

/**
 * Created by thomas on 11/07/2017.
 */

public class BasketActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, PaymentMethodNonceCreatedListener {
    private static final String LOG_TAG = "BasketActivity";

    public static final int STRIPE_REQUEST = 55555;

    @BindView(R.id.stepsbar)
    LinearLayout stepsBarLL;

    @BindView(R.id.home_button)
    TextView tvHome;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.waiting)
    RelativeLayout waiting;

    //------------- stepsBar icons -----------
    ImageView summaryFragmentIcon;
    ImageView deliveryAddressFragmentIcon;
    ImageView deliveryMethodFragmentIcon;
    ImageView paymentFragmentIcon;
    ImageView confirmationFragmentIcon;
    //----------------------------------------

    private Command command;
    private ArrayList<EditorPic> pics;

    private int deliveryMethodPriceInCts;
    private DeliveryMethod deliveryMethodSelected;

    private PaymentMethod paymentMethodSelected;
    public String selectedCardBrand;
    public String selectedCardInfos;
    public String selectedCardIds;

    private GoogleApiClient googleApiClient;

    private final String summaryTag = "SUMMARYTAG";
    private final String deliveryAddressTag = "DELIVERYADDRESSTAG";
    private final String billingAddressTag = "BILLINGADDRESSTAG";
    private final String deliveryMethodTag = "DELIVERYMETHODTAG";
    private final String paymentTag = "PAYMENTTAG";
    //  private AddressData addressDataDelivery;
    //  private AddressData addressDataBilling;

    private Boolean directLink = false;

    private int status = 1;

    public String getSummaryTag() {
        return summaryTag;
    }

    public DeliveryMethod getDeliveryMethodSelected() {
        return deliveryMethodSelected;
    }

    public void setDeliveryMethodSelected(DeliveryMethod deliveryMethodSelected) {
        this.deliveryMethodSelected = deliveryMethodSelected;
    }

    public PaymentMethod getPaymentMethodSelected() {
        return paymentMethodSelected;
    }

    public void setPaymentMethodSelected(PaymentMethod paymentMethodSelected){
        this.paymentMethodSelected = paymentMethodSelected;

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PaymentFragment) {
            ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initTotalPrice();
        }
    }

    public String getDeliveryAddressTag() {
        return deliveryAddressTag;
    }

    public String getBillingAddressTag() {
        return billingAddressTag;
    }

    public String getDeliveryMethodTag() {
        return deliveryMethodTag;
    }

    public String getPaymentTag() {
        return paymentTag;
    }

    public Boolean getDirectLink() {
        return directLink;
    }

    public void setDirectLink(Boolean directLink) {
        this.directLink = directLink;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public ArrayList<EditorPic> getPics() {
        return pics;
    }

    public int getDeliveryMethodPriceInCts() {
        return deliveryMethodPriceInCts;
    }

    public void setDeliveryMethodPriceInCts(int deliveryMethodPriceInCts) {
        this.deliveryMethodPriceInCts = deliveryMethodPriceInCts;
    }

    public Command getCommand() {
        return CommandHandler.get().currentCommand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Appsee.start("ca29b14487ac4c8e843ecb90c54c413f");

        Thread.setDefaultUncaughtExceptionHandler(new PiiicsExceptionHandler(this));

        setContentView(R.layout.activity_basket);
        ButterKnife.bind(this);
        CommandHandler.get().setBillingDict(new AddressData());
        CommandHandler.get().setDeliveryDict(new AddressData());
        initStepsBar();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        command = CommandHandler.get().currentCommand;

        if(command == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.ERROR)
                    .setMessage(R.string.GENERAL_ERROR)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        pics = command.getEditorPics();

        deliveryMethodPriceInCts = 0;

        initToolbar();

        launchFragment();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    private void initStepsBar() {
        summaryFragmentIcon = stepsBarLL.findViewById(R.id.summary);
        deliveryAddressFragmentIcon = stepsBarLL.findViewById(R.id.delivery_address);
        deliveryMethodFragmentIcon = stepsBarLL.findViewById(R.id.delivery_method);
        paymentFragmentIcon = stepsBarLL.findViewById(R.id.payment);
        confirmationFragmentIcon = stepsBarLL.findViewById(R.id.confirmation);
    }

    private void launchFragment() {
        if (CommandHandler.get().currentCommand.getProduct().equals("PRINT")) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                SummaryFragment summaryFragment = new SummaryFragment();
                ft.replace(R.id.fragment_container, summaryFragment);
                ft.commit();
            }
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                ft.replace(R.id.fragment_container, new OptionLogoFragment());
                ft.commit();
            }
        }

    }

    public void updateStepsBar(Fragment fragment) {
        if (fragment instanceof SummaryFragment) {
            summaryFragmentIcon.setImageResource(R.drawable.panier_noir);
            deliveryAddressFragmentIcon.setImageResource(R.drawable.localisation_gris);
            deliveryMethodFragmentIcon.setImageResource(R.drawable.livraison_gris);
            paymentFragmentIcon.setImageResource(R.drawable.monnaie_gris);
            confirmationFragmentIcon.setImageResource(R.drawable.confirmation_gris);

            status = 1;
        } else if (fragment instanceof DeliveryAddressFragment) {
            summaryFragmentIcon.setImageResource(R.drawable.panier_noir);
            deliveryAddressFragmentIcon.setImageResource(R.drawable.localisation_noir);
            deliveryMethodFragmentIcon.setImageResource(R.drawable.livraison_gris);
            paymentFragmentIcon.setImageResource(R.drawable.monnaie_gris);
            confirmationFragmentIcon.setImageResource(R.drawable.confirmation_gris);

            status = 2;
        } else if (fragment instanceof DeliveryMethodFragment || fragment instanceof EmailRequestFragment) {
            summaryFragmentIcon.setImageResource(R.drawable.panier_noir);
            deliveryAddressFragmentIcon.setImageResource(R.drawable.localisation_noir);
            deliveryMethodFragmentIcon.setImageResource(R.drawable.livraison_noir);
            paymentFragmentIcon.setImageResource(R.drawable.monnaie_gris);
            confirmationFragmentIcon.setImageResource(R.drawable.confirmation_gris);

            status = 3;
        } else if (fragment instanceof PaymentFragment || fragment instanceof PaymentWebViewFragment) {
            summaryFragmentIcon.setImageResource(R.drawable.panier_noir);
            deliveryAddressFragmentIcon.setImageResource(R.drawable.localisation_noir);
            deliveryMethodFragmentIcon.setImageResource(R.drawable.livraison_noir);
            paymentFragmentIcon.setImageResource(R.drawable.monnaie_noir);
            confirmationFragmentIcon.setImageResource(R.drawable.confirmation_gris);

            status = 4;
        } else {
            summaryFragmentIcon.setImageResource(R.drawable.panier_noir);
            deliveryAddressFragmentIcon.setImageResource(R.drawable.localisation_noir);
            deliveryMethodFragmentIcon.setImageResource(R.drawable.livraison_noir);
            paymentFragmentIcon.setImageResource(R.drawable.monnaie_noir);
            confirmationFragmentIcon.setImageResource(R.drawable.confirmation_noir);

            status = 5;
        }
    }

    public AddressData getCurrentAddressData(String fragmentTag) {
        Log.i(LOG_TAG, "fragmentTag : " + fragmentTag);
        Log.i(LOG_TAG, "deliveryAddressTag : " + deliveryAddressTag);
        if (fragmentTag.equals(deliveryAddressTag)) {
            return CommandHandler.get().getDeliveryDict();
        } else {
            return CommandHandler.get().getBillingDict();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ConfirmationFragment) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PaymentFragment) {
            ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onRecapDelivryMethodClick();
        } else {
            super.onBackPressed();
        }
    }

    public void showHomeButton() {
        tvHome.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.home_button)
    public void onHomeClick() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.summary)
    public void onSummaryClick()
    {
        Log.d(LOG_TAG, "summary click");
        getSupportFragmentManager().popBackStack(SummaryFragment.FRAGMENT_TRANSACTION_TAG, POP_BACK_STACK_INCLUSIVE);
    }

    @OnClick(R.id.delivery_address)
    public void onDeliveryAddressClick()
    {
        Log.d(LOG_TAG, "delivery click");
        if(status > 1 && status < 5){
            getSupportFragmentManager().popBackStack(DeliveryAddressFragment.FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG, POP_BACK_STACK_INCLUSIVE);
        }
    }

    @OnClick(R.id.delivery_method)
    public void onDeliveryMethodClick()
    {
        Log.d(LOG_TAG, "method click");
        if(status > 2 && status < 5){
            getSupportFragmentManager().popBackStack(DeliveryMethodFragment.FRAGMENT_TRANSACTION_TAG, POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void showLoading(){
        waiting.setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        waiting.setVisibility(View.GONE);
    }

    //Accengage parameters

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        // ...
    }

    @Override
    protected void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);
        // ...
    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
        // ...
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setAlbumOptionsMode(boolean albumOptionsMode) {
        if (albumOptionsMode) {
            stepsBarLL.setVisibility(View.GONE);
            toolbarTitle.setText(R.string.OPTIONS);
        } else {
            stepsBarLL.setVisibility(View.VISIBLE);
            toolbarTitle.setText(R.string.BASKET);
        }
    }

    public void initStripe(){
        PaymentConfiguration.init(Utils.STRIPE_PUBLISHABLE_KEY);

        CustomerSession.initCustomerSession(
                new PiiicsEphemeralKeyProvider(
                        new PiiicsEphemeralKeyProvider.ProgressListener() {
                            @Override
                            public void onStringResponse(String string) {
                                if (string.startsWith("Error: ")) {
                                    Toast.makeText(BasketActivity.this, string, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, this));

        CustomerSession.getInstance().retrieveCurrentCustomer(
                new CustomerSession.CustomerRetrievalListener() {
                    @Override
                    public void onCustomerRetrieved(@NonNull Customer customer) {
                        Log.d(LOG_TAG, "Customer :" + customer.getDefaultSource());
                        if(customer.getDefaultSource() != null){
                            String selectedSource = customer.getDefaultSource();
                            CustomerSource source = customer.getSourceById(selectedSource);
                            selectedCardIds = selectedSource;

                            if(selectedSource.startsWith("card_")){
                                buildCardString(source.asCard());
                            }

                            if(selectedSource.startsWith("src_")){
                                buildCardString((SourceCardData)source.asSource().getSourceTypeModel());
                            }

                            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PaymentFragment) {
                                ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initPaymentMethods();
                                ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initTotalPrice();
                            }
                        }
                    }

                    @Override
                    public void onError(int errorCode, @Nullable String errorMessage) {
                        Log.d(LOG_TAG, "Error : " + errorCode);
                    }
                });
    }

    public void startStripePayment()
    {
        Intent payIntent = PaymentMethodsActivity.newIntent(this);
        startActivityForResult(payIntent, STRIPE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STRIPE_REQUEST && resultCode == RESULT_OK) {
            String selectedSource = data.getStringExtra(PaymentMethodsActivity.EXTRA_SELECTED_PAYMENT);
            Log.d(LOG_TAG, selectedSource);

            String id = "";

            try {
                JSONObject j = new JSONObject(selectedSource);
                id = j.getString("id");
                selectedCardIds = id;

                if(id.startsWith("src_")){
                    Source source = Source.fromString(selectedSource);
                    if (source != null && Source.CARD.equals(source.getType())) {
                        buildCardString((SourceCardData)source.getSourceTypeModel());
                    }
                }

                if(id.startsWith("card_")){
                    Card card = Card.fromString(selectedSource);
                    if(card != null)
                        buildCardString(card);
                }

                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PaymentFragment) {
                    ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initPaymentMethods();
                    ((PaymentFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).initTotalPrice();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildCardString(@NonNull SourceCardData data) {
        selectedCardBrand = data.getBrand();
        selectedCardInfos = "XXXX XXXX XXXX " + data.getLast4() + "  (" + data.getExpiryMonth() + "/" + data.getExpiryYear() + ")";
    }

    private void buildCardString(@NonNull Card data) {
        selectedCardBrand = data.getBrand();
        selectedCardInfos = "XXXX XXXX XXXX " + data.getLast4() + "  (" + data.getExpMonth() + "/" + data.getExpYear() + ")";
    }

    public void createCharge()
    {
        if (!selectedCardIds.equals("")) {
            SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);

            Call<HashMap<String, String>> requestCall = BackendAPI.piiicsapi.createcharge(command.getCommandID(), selectedCardIds, "Bearer " + prefs.getString("token", ""));
            requestCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    if (response.isSuccessful()) {
                        HashMap<String, String> obj = response.body();

                        if (obj != null && obj.get("status") != null && obj.get("status").equals("succeeded")) {

                            sendConfirmation();

                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.GENERAL_ERROR), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.GENERAL_ERROR), Toast.LENGTH_SHORT).show();
                    }

                    hideLoading();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    Log.d("PAYPAL", "error : " + t.getLocalizedMessage());
                    hideLoading();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.GENERAL_ERROR), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        String nonce = paymentMethodNonce.getNonce();

        SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        Call<HashMap<String, String>> requestCall = BackendAPI.piiicsapi.paypalpayment(command.getCommandID(), nonce, "Bearer " + prefs.getString("token", ""));
        requestCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, String> obj = response.body();
                    Log.d("PAYPAL", obj.get("status"));

                    if(obj != null && obj.get("status") != null && obj.get("status").equals("success")){

                        sendConfirmation();

                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.GENERAL_ERROR), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.GENERAL_ERROR), Toast.LENGTH_SHORT).show();
                }

                hideLoading();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Log.d("PAYPAL", "error : " + t.getLocalizedMessage());
                hideLoading();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void sendConfirmation()
    {
        hideLoading();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ConfirmationFragment confirmationFragment = new ConfirmationFragment();
        ft.replace(R.id.fragment_container, confirmationFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
