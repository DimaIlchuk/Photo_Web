package com.photoweb.piiics.fragments.BasketFragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PayPalRequest;
import com.photoweb.piiics.Adapters.PaymentMethodAdapter;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.AddressData;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PaymentMethod;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.model.PromoCode;
import com.photoweb.piiics.model.SummaryCategory;
import com.photoweb.piiics.model.User;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.photoweb.piiics.model.Command.LOG_TAG;
import static com.photoweb.piiics.model.Command.convertPriceToString;

/**
 * Created by thomas on 13/07/2017.
 */

public class PaymentFragment extends BaseFragment {
    public static String TAG = "PAYMENT";

    @BindView(R.id.command_total_price)
    TextView commandTotalPriceTV;

    @BindView(R.id.delivery_total_price)
    TextView deliveryTotalPriceTV;

    @BindView(R.id.recap_command_infos)
    TextView recapCommandInfosTV;

    @BindView(R.id.delivery_address_name)
    TextView deliveryAddressNameTV;
    @BindView(R.id.delivery_address_address)
    TextView deliveryAddressAddressTV;
    @BindView(R.id.delivery_address_postal_code)
    TextView deliveryAddressPostalCodeAndCityTV;
    @BindView(R.id.delivery_address_country)
    TextView deliveryAddressCountryTV;

    @BindView(R.id.billing_address_title)
    TextView billingAddressTitle;
    @BindView(R.id.billing_address_infos)
    LinearLayout billingAddressInfosLL;
    @BindView(R.id.billing_address_name)
    TextView billingAddressNameTV;
    @BindView(R.id.billing_address_address)
    TextView billingAddressAddressTV;
    @BindView(R.id.billing_address_postal_code)
    TextView billingAddressPostalCodeAndCityTV;
    @BindView(R.id.billing_address_country)
    TextView billingAddressCountryTV;

    @BindView(R.id.promo_code_edittext)
    EditText promoCodeET;
    @BindView(R.id.reductionTV)
    TextView reductionTV;

    @BindView(R.id.validate)
    TextView validateTV;

    BasketActivity activity;
    Command command;
    String promoCode;
    int promo = 0;

    ArrayList<PaymentMethod> paymentMethods;
    PaymentMethodAdapter paymentMethodAdapter;

    PaymentMethod cbPayment;
    PaymentMethod paypalPayment;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    BraintreeFragment mBraintreeFragment;

    private static SharedPreferences prefs;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_payment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        command = activity.getCommand();

        initPaymentMethods();
        saveDefaultPayment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);
        initTotalPriceCommand();///
        initDeliveryPrice();
        initTotalPrice();
        initCommandRecap();
        initDeliveryAddressInfos();
        initBillingAddressInfos();
        activity.initStripe();

        prefs = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);

        Log.d(TAG, "Command price : " + CommandHandler.get().getTotalPrice());

        initRecycler();
    }

    private void initRecycler()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        paymentMethodAdapter = new PaymentMethodAdapter(activity, this, paymentMethods);
        recyclerView.setAdapter(paymentMethodAdapter);
    }

    public void initPaymentMethods()
    {
        if(activity.selectedCardInfos == null)
            cbPayment = new PaymentMethod("Empty", getString(R.string.SELECT_CARD), "");
        else {
            cbPayment = new PaymentMethod("CreditCard", getString(R.string.USE_CARD) + activity.selectedCardBrand, activity.selectedCardInfos);
            cbPayment.setDefaultPayment(true);
            activity.setPaymentMethodSelected(cbPayment);
        }

        paypalPayment = new PaymentMethod("PayPal", getString(R.string.PAY_PAYPAL), "");

        paymentMethods = new ArrayList<>();

        paymentMethods.add(cbPayment);

        if(Locale.getDefault().getCountry().toLowerCase().equals("de")){
            paymentMethods.add(paypalPayment);
        }

        if(paymentMethodAdapter != null) {
            paymentMethodAdapter = new PaymentMethodAdapter(activity, this, paymentMethods);
            recyclerView.setAdapter(paymentMethodAdapter);
        }
    }

    private void saveDefaultPayment()
    {
        for (PaymentMethod paymentMethod : paymentMethods)
        {
            if(paymentMethod.isDefaultPayment()){
                activity.setPaymentMethodSelected(paymentMethod);
                break;
            }
        }
    }

    private void initTotalPriceCommand() {
        String totalPriceStr = "ERROR";
        try {
            if (command.getProduct().equals("ALBUM")) {
                int totalPrice = command.getAllPicsPrice() + command.getAlbumOptions().getOptionsTotalPrice();
                totalPriceStr = Command.convertPriceToString(totalPrice);
            } else {
                totalPriceStr = command.getAllPicsPriceStr();
            }
        } catch (PriceSecurityException pse) {
            pse.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        commandTotalPriceTV.setText(totalPriceStr);
    }

    private void initDeliveryPrice() {
        String deliveryPrice = convertPriceToString(activity.getDeliveryMethodSelected().getPrice());
        deliveryTotalPriceTV.setText(deliveryPrice);
    }

    public void initTotalPrice() {

        if((activity.getPaymentMethodSelected() == null || activity.getPaymentMethodSelected().getIdentifier().equals("Empty")) && ((float) CommandHandler.get().getTotalPrice()) / 100 - ((float) promo) / 100 != 0){
            validateTV.setAlpha(0.5f);
        }else{
            validateTV.setAlpha(1.0f);
        }

        String totalPrice = convertPriceToString(CommandHandler.get().getTotalPrice() - promo);
        validateTV.setText("Pay (" + totalPrice + ")");
    }

    private void initCommandRecap() {
        String recapCommand;
        if (command.getProduct().equals("ALBUM")) {
            int albums = command.getAlbumOptions().getBookQuantity();
            String albumName;
            if (albums == 1) {
                albumName = " " + getString(R.string.SINGLE_ALBUM);
            } else {
                albumName = " " + getString(R.string.ALBUM);
            }
            recapCommand = String.valueOf(albums) + albumName;
        } else {
            int prints = CommandHandler.get().currentCommand.getEditorPics().size();
            String tirageName;
            if (prints == 1) {
                tirageName = " " + getString(R.string.SINGLE_PRINT);
            } else {
                tirageName = " " + getString(R.string.PRINT);
            }
            recapCommand = String.valueOf(prints) + tirageName;
        }

        recapCommandInfosTV.setText(recapCommand);
    }

    private void initDeliveryAddressInfos() {
        AddressData addressData = CommandHandler.get().getDeliveryDict();
        deliveryAddressNameTV.setText(addressData.getFullName());
        deliveryAddressAddressTV.setText(addressData.getAddress());
        deliveryAddressPostalCodeAndCityTV.setText(addressData.getPostalCodeAndCity());
        deliveryAddressCountryTV.setText(addressData.getCountry());
    }

    private void initBillingAddressInfos() {
        if (isSameDeliveryAndBillingAddresses()) {
            billingAddressTitle.setText(R.string.SAME_ADDRESS);
            billingAddressInfosLL.setVisibility(View.GONE);
        } else {
            billingAddressTitle.setText(R.string.BILLING_ADDRESS);

            AddressData addressData = CommandHandler.get().getBillingDict();
            billingAddressNameTV.setText(addressData.getFullName());
            billingAddressAddressTV.setText(addressData.getAddress());
            billingAddressPostalCodeAndCityTV.setText(addressData.getPostalCodeAndCity());
            billingAddressCountryTV.setText(addressData.getCountry());

            billingAddressInfosLL.setVisibility(View.VISIBLE);
        }
    }

    private boolean isSameDeliveryAndBillingAddresses() {
        AddressData deliveryAddress = CommandHandler.get().getDeliveryDict();
        AddressData billingAddress = CommandHandler.get().getBillingDict();

        if (deliveryAddress.getFullName().equals(billingAddress.getFullName()) &&
                deliveryAddress.getAddress().equals(billingAddress.getAddress()) &&
                deliveryAddress.getAdditionalAddress().equals(billingAddress.getAdditionalAddress()) &&
                deliveryAddress.getPostalCodeAndCity().equals(billingAddress.getPostalCodeAndCity()) &&
                deliveryAddress.getCountry().equals(billingAddress.getCountry()) &&
                deliveryAddress.getCivility().equals(billingAddress.getCivility())) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.recap_command_layout)
    public void onRecapCommandClick() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack(SummaryFragment.FRAGMENT_TRANSACTION_TAG, POP_BACK_STACK_INCLUSIVE);
    }

    @OnClick(R.id.recap_delivery_method)
    public void onRecapDelivryMethodClick() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack(DeliveryMethodFragment.FRAGMENT_TRANSACTION_TAG, POP_BACK_STACK_INCLUSIVE);
    }

    @OnClick(R.id.recap_delivery_address)
    public void onRecapDeliveryAddressClick() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack(DeliveryAddressFragment.FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG, POP_BACK_STACK_INCLUSIVE);
    }

    @OnClick(R.id.recap_billing_address)
    public void onRecapBillingAddressClick() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (isSameDeliveryAndBillingAddresses()) {
            fragmentManager.popBackStack(DeliveryAddressFragment.FRAGMENT_TRANSACTION_DELIVERY_ADDRESS_TAG, POP_BACK_STACK_INCLUSIVE);
        } else {
            fragmentManager.popBackStack(DeliveryAddressFragment.FRAGMENT_TRANSACTION_BILLING_ADDRESS_TAG, POP_BACK_STACK_INCLUSIVE);
        }
    }

    @OnClick(R.id.promocode_button)
    public void onPromoCodeClick() {
        Log.d("PAYMENT", "click");
        promoCode = promoCodeET.getText().toString();

        if (promoCode.equals("")) {
            return;
        } else {
            BackendAPI.addsponsorcode(UserInfo.getInt("id"), promoCode, prefs.getString("token", ""), new BackendAPI.ResponseListener<PromoCode>() {
                @Override
                public void perform(PromoCode obj, int s, String errmsg) {
                    if (obj != null) {
                        updatePromo(obj);
                        //Log.d(LOG_TAG, "code : " + obj.code);
                    } else {
                        Log.d(LOG_TAG, errmsg);

                        Log.d("PAYMENT", "empty");
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle(R.string.ERROR)
                                .setMessage(R.string.INVALID_CODE)
                                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                }
            });
        }
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {

        if((activity.getPaymentMethodSelected() == null || activity.getPaymentMethodSelected().getIdentifier().equals("Empty")) && ((float) CommandHandler.get().getTotalPrice()) / 100 - ((float) promo) / 100 != 0){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getActivity());
            }
            builder.setTitle(R.string.ERROR)
                    .setMessage("Please select a payment method")
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        activity.showLoading();

        int free = 0;

        if(command.getProduct().equals("PRINT")){
            int free_available = UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");


            ArrayList<EditorPic> pics = command.getEditorPics();
            for (EditorPic pic : pics) {
                FormatReference fm = new FormatReference();

                if(pic.getFormatReference() == null){
                    fm = PriceReferences.getDefaultformat();
                }else{
                    fm = pic.getFormatReference();
                }

                if (fm.getName().equals(PriceReferences.STANDARD_FORMAT)) {
                    if (!pic.isDuplicated()) {
                        if (free_available > free) {
                            free++;
                        }
                    }
                }
            }
        }else{
            free = UserInfo.getInt("book_available");
        }

        Log.i(LOG_TAG, "COMMAND ID : " + String.valueOf(command.getCommandID()));
        Log.i(LOG_TAG, "PRICE COMMAND : " + String.valueOf(((float) CommandHandler.get().getTotalPrice()) / 100 - ((float) promo) / 100));
        BackendAPI.addsale(command.getCommandID(), UserInfo.getInt("id"), command.getProduct(), ((float) CommandHandler.get().getTotalPrice()) / 100 - ((float) promo) / 100, free, "pending", promoCode, CommandHandler.get().getSaleAddress("delivery"), CommandHandler.get().getSaleAddress("billing"), prefs.getString("token", ""), new BackendAPI.ResponseListener<UserCommandCompleted>() {
            @Override
            public void perform(UserCommandCompleted obj, int s, String errmsg) {

                if(obj == null){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(activity);
                    }
                    builder.setTitle(R.string.ERROR)
                            .setMessage(R.string.GENERAL_ERROR)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else{
                    sendAllItems(obj.getId());

                    if(Float.parseFloat(obj.getAmount()) == 0.0){
                        sendFOCSale(obj.getIdentifier());
                    }else if(activity.getPaymentMethodSelected().getIdentifier().equals("CreditCard")){
                        activity.createCharge();
                    }else if(activity.getPaymentMethodSelected().getIdentifier().equals("PayPal")){
                        preparePayPalPayment();
                    }
                }
            }
        });
    }

    private void sendFOCSale(String identifier)
    {
        BackendAPI.focsale(identifier, prefs.getString("token", ""), new BackendAPI.ResponseListener<UserCommandCompleted>() {
            @Override
            public void perform(UserCommandCompleted obj, int s, String errmsg) {
                activity.sendConfirmation();
            }
        });
    }

    private void preparePayPalPayment()
    {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(activity, "production_63mx89zc_bqmhcqgc2dnbs3x4");

            float total = ((float) CommandHandler.get().getTotalPrice()) / 100 - ((float) promo) / 100;
            PayPalRequest request = new PayPalRequest("" + total)
                    .currencyCode("EUR")
                    .intent(PayPalRequest.INTENT_AUTHORIZE);

            PayPal.requestOneTimePayment(mBraintreeFragment, request);
            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }
    }

    private void sendAllItems(Integer cartid)
    {
        ArrayList<EditorPic> pics = activity.getPics();

        HashMap<String, Object> params = new HashMap<String, Object>();

        int i = 0;

        if(activity.getCommand().getProduct().equals("PRINT")){
            if (CommandHandler.get().articles.get("10x15").units > 0) {
                params.put("[" + i + "].name", "1796 : 1205");
                params.put("[" + i + "].price", ((float) CommandHandler.get().articles.get("10x15").priceInCts)/100);
                params.put("[" + i + "].sale_id", cartid);
                params.put("[" + i + "].quantity", CommandHandler.get().articles.get("10x15").units);

                i++;
            }

            if (CommandHandler.get().articles.get("10x10").units > 0) {
                params.put("[" + i + "].name", "1205 : 1205");
                params.put("[" + i + "].price", ((float) CommandHandler.get().articles.get("10x10").priceInCts)/100);
                params.put("[" + i + "].sale_id", cartid);
                params.put("[" + i + "].quantity", CommandHandler.get().articles.get("10x10").units);

                i++;
            }

            if (CommandHandler.get().articles.get("10x18").units > 0) {
                params.put("[" + i + "].name", "2138 : 1205");
                params.put("[" + i + "].price", ((float) CommandHandler.get().articles.get("10x18").priceInCts)/100);
                params.put("[" + i + "].sale_id", cartid);
                params.put("[" + i + "].quantity", CommandHandler.get().articles.get("10x18").units);

                i++;
            }
        }

        SummaryCategory stickers= new SummaryCategory("Stickers", R.drawable.smiley_blanc, PriceReferences.STICKERS);
        SummaryCategory backgrounds = new SummaryCategory("Fonds", R.drawable.fonds_blanc, PriceReferences.BACKGROUNDS);
        int albumQuantity = (command.getAlbumOptions() == null) ? 1 : command.getAlbumOptions().getBookQuantity();

        for (EditorPic editorPic : pics) {
            stickers.addUnits(editorPic.getStickersSize() * editorPic.getCopy() * albumQuantity);
            try {
                stickers.addPrice(editorPic.getStickersPrice() * editorPic.getCopy() * albumQuantity);

                if (editorPic.getBackgroundReference() != null && !isDefaultBackground(editorPic.getBackgroundReference())) {
                    backgrounds.addUnits(editorPic.getCopy() * albumQuantity);
                    backgrounds.addPrice(editorPic.getBackgroundPrice() * albumQuantity);
                }
            } catch (PriceSecurityException e) {
                e.printStackTrace();
            }
        }

        if (stickers.units > 0) {
            params.put("[" + i + "].name", "Sticker");
            params.put("[" + i + "].price", ((float) stickers.priceInCts)/100);
            params.put("[" + i + "].sale_id", cartid);
            params.put("[" + i + "].quantity", stickers.units);

            i++;
        }

        if (backgrounds.units > 0) {
            params.put("[" + i + "].name", "Background");
            params.put("[" + i + "].price", ((float) backgrounds.priceInCts)/100);
            params.put("[" + i + "].sale_id", cartid);
            params.put("[" + i + "].quantity", backgrounds.units);

            i++;
        }

        BackendAPI.insertitems(params, prefs.getString("token", ""), new BackendAPI.ResponseListener<HashMap<String, Object>>() {
            @Override
            public void perform(HashMap<String, Object> obj, int s, String errmsg) {

            }
        });
    }

    private boolean isDefaultBackground(BackgroundReference backgroundReference) {
        if (backgroundReference.getName().equals(PriceReferences.getDefaultBackground().getName())) {
            return true;
        }
        return false;
    }

    private void updatePromo(PromoCode code) {
        String msg = "";

        if (code.type.equals("delivery")) {
            promo = activity.getDeliveryMethodSelected().getPrice();
            msg = getString(R.string.DELIVERY_CODE);

        } else if (code.type.equals("option")) {
            try {
                promo = Math.min(command.getAllPicsPrice(), Math.round(code.quantity*100));
            } catch (PriceSecurityException e) {
                e.printStackTrace();
            }
            msg = getString(R.string.DISCOUNT_CODE) + convertPriceToString(promo) + " !";

        } else if (code.type.equals("cash")) {
            promo = Math.min(CommandHandler.get().getTotalPrice(), Math.round(code.quantity*100));
            msg = getString(R.string.DISCOUNT_CODE) + convertPriceToString(promo) + " !";

        } else if (code.type.equals("page")) {
            //promo = (self.photoAlbum) ? (Double(min(max(0, PictureHandler.sharedInstance.currentCommand.count - 22), Int(JSON["code"]?["quantity"] as! Float)))*CurrentConfig.sharedInstance.getSettingWithName(name: "page").curprice) : 0
            msg = getString(R.string.DISCOUNT_CODE) + convertPriceToString(promo) + " !";

        } else if (code.type.equals("full")) {
            try {
                Log.d("PAYMENT", "Total price : " + command.getAllPicsPrice());
                Log.d("PAYMENT", "Delevry : " + activity.getDeliveryMethodSelected().getPrice());

                promo = CommandHandler.get().getTotalPrice();

                Log.d("PAYMENT", "Promo : " + promo);

                msg = getString(R.string.FREE_CODE);
            } catch (PriceSecurityException e) {
                e.printStackTrace();
            }
        }


        reductionTV.setVisibility(View.VISIBLE);
        reductionTV.setText(getString(R.string.DISCOUNT)+": -" + convertPriceToString(promo));

        promoCode = code.code;

        initTotalPrice();

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(R.string.CONGRATS)
                .setMessage(getString(R.string.THANKS_CODE) + msg)
                .setPositiveButton(R.string.UNDERSTAND, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void updateUser(User user) {
        // TODO: 11/09/2017 Update user

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(R.string.CONGRATS)
                .setMessage(R.string.THANKS_CODE + getString(R.string.PRINT_CODE, 5))
                .setPositiveButton(R.string.UNDERSTAND, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
