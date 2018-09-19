package com.photoweb.piiics.fragments.BasketFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.Adapters.DeliveryMethodAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.DeliveryMethod;
import com.photoweb.piiics.utils.AWSHandler;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.UserInfo;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 13/07/2017.
 */

public class DeliveryMethodFragment extends BaseFragment {
    private static final String LOG_TAG = "DeliveryMethodFragment";
    public static final String FRAGMENT_TRANSACTION_TAG = "DeliveryMethodFragment";

    BasketActivity activity;
    Command command;

    ArrayList<DeliveryMethod> deliveryMethods;

    DeliveryMethod mondialRelais;
    DeliveryMethod laPoste;
    DeliveryMethod collissimo;

    //DE
    DeliveryMethod standard;
    DeliveryMethod express;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.delivery_price)
    TextView deliveryPriceTV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_delivery_method;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        command = activity.getCommand();

        // Log.i(LOG_TAG, "delivery address : " + CommandHandler.get().getDeliveryDict().toString());
        // Log.i(LOG_TAG, "billing address : " + CommandHandler.get().getBillingDict().toString());

        initDeliveryMethods();
        computeShippingPrices();
        saveDefaultDelivery();
    }

    private void saveDefaultDelivery() {
        for (DeliveryMethod deliveryMethod : deliveryMethods) {
            if (deliveryMethod.isDefaultDelivery()) {
                activity.setDeliveryMethodSelected(deliveryMethod);
                activity.setDeliveryMethodPriceInCts(deliveryMethod.getPrice());
                break;
            }
        }
    }

    private void initDeliveryMethods() {
        laPoste = new DeliveryMethod(getString(R.string.DELIVERY_STANARD), getString(R.string.DELIVERY_STANDARD_DESC), "ECOPLIFR");
        collissimo = new DeliveryMethod(getString(R.string.DELIVERY_EXPRESS), getString(R.string.DELIVERY_EXPRESS_DESC), "COFR");
        mondialRelais = new DeliveryMethod(getString(R.string.DELIVERY_MR), getString(R.string.DELIVERY_MR_DESC), "MRPRL");

        standard = new DeliveryMethod(getString(R.string.DELIVERY_STANARD), getString(R.string.DELIVERY_STANDARD_DESC), "DELIVPRIO");
        express = new DeliveryMethod(getString(R.string.DELIVERY_EXPRESS), getString(R.string.DELIVERY_EXPRESS_DESC), "DELIVSUIVI");

        deliveryMethods = new ArrayList<>();
    }

    private void computeShippingPrices() {
        if (command.getProduct().equals("ALBUM")) {
            albumShippingPrices();
        } else {
            printShippingPrices();
        }
    }

    /*
        Recupère la bonne catégorie des prix des transporteurs en fonction de la commande
        ne gere pas plusieurs albums différents (seulement un album, avec 1 ou plusieurs copies/duplicatas)
     */
    private void albumShippingPrices() {
        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        if(cCode.equals("FR")){
            mondialRelais.setDefaultDelivery(true);
        }else{
            standard.setDefaultDelivery(true);
        }

        int albums = command.getAlbumOptions().getBookQuantity();
        int totalPages = command.getEditorPics().size() * albums;
        boolean isHasStrongCover = command.getAlbumOptions().isHasStrongCover();

        if (isHasStrongCover) {
            if (albums == 1) {
                if (totalPages <= 80) {
                    albumDeliveryPrice2();
                } else {
                    albumDeliveryPrice4();
                }
            } else if (albums == 2) {
                if (totalPages <= 90) {
                    albumDeliveryPrice3();
                } else {
                    albumDeliveryPrice4();
                }
            } else {
                albumDeliveryPrice4();
            }
        } else {
            if (albums == 1) {
                if (totalPages <= 50) {
                    albumDeliveryPrice1();
                } else if (totalPages <= 80) {
                    albumDeliveryPrice2();
                } else {
                    albumDeliveryPrice4();
                }
            } else if (albums == 2) {
                if (totalPages <= 80) {
                    albumDeliveryPrice2();
                } else if (totalPages <= 100) {
                    albumDeliveryPrice3();
                } else {
                    albumDeliveryPrice4();
                }
            } else if (albums == 3) {
                if (totalPages <= 160) {
                    albumDeliveryPrice3();
                } else {
                    albumDeliveryPrice4();
                }
            } else {
                albumDeliveryPrice4();
            }
        }

    }

    private void albumDeliveryPrice1() {

        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        switch (cCode) {
            case "FR":
                setDeliveryPricesFR(699, 799, 899);
                break;
            case "GB":
                setDeliveryPricesINTL(599, 799);
                break;
            case "EE":
                setDeliveryPricesINTL(699, 949);
                break;
            case "DE":
            case "ES":
            case "IT":
                setDeliveryPricesINTL(799, 999);
                break;
            case "IE":
                setDeliveryPricesINTL(699, 1149);
                break;
            case "MQ":
            case "GY":
            case "RE":
            case "PM":
            case "YT":
                setDeliveryPricesINTL(899, 1699);
                break;
            case "TF":
            case "WF":
            case "PF":
            case "NC":
                setDeliveryPricesINTL(899, 1899);
                break;
            default:
                setDeliveryPricesINTL(699, 899);
                break;
        }

    }

    private void albumDeliveryPrice2() {

        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        switch (cCode) {
            case "FR":
                setDeliveryPricesFR(699, -1, 899);
                break;
            case "GB":
                setDeliveryPricesINTL(599, 799);
                break;
            case "EE":
                setDeliveryPricesINTL(699, 949);
                break;
            case "DE":
            case "ES":
            case "IT":
                setDeliveryPricesINTL(799, 999);
                break;
            case "IE":
                setDeliveryPricesINTL(699, 1149);
                break;
            case "MQ":
            case "GY":
            case "RE":
            case "PM":
            case "YT":
                setDeliveryPricesINTL(899, 1699);
                break;
            case "TF":
            case "WF":
            case "PF":
            case "NC":
                setDeliveryPricesINTL(899, 1899);
                break;
            default:
                setDeliveryPricesINTL(699, 899);
                break;
        }

    }

    private void albumDeliveryPrice3() {

        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        switch (cCode) {
            case "FR":
                setDeliveryPricesFR(799, -1, 999);
                break;
            case "GB":
                setDeliveryPricesINTL(599, 799);
                break;
            case "EE":
                setDeliveryPricesINTL(699, 949);
                break;
            case "DE":
            case "ES":
            case "IT":
                setDeliveryPricesINTL(799, 999);
                break;
            case "IE":
                setDeliveryPricesINTL(699, 1149);
                break;
            case "MQ":
            case "GY":
            case "RE":
            case "PM":
            case "YT":
                setDeliveryPricesINTL(1099, 1899);
                break;
            case "TF":
            case "WF":
            case "PF":
            case "NC":
                setDeliveryPricesINTL(1099, 2099);
                break;
            default:
                setDeliveryPricesINTL(699, 899);
                break;
        }

    }

    private void albumDeliveryPrice4() {

        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        switch (cCode) {
            case "FR":
                setDeliveryPricesFR(899, -1, 1099);
                break;
            case "GB":
                setDeliveryPricesINTL(599, 799);
                break;
            case "EE":
                setDeliveryPricesINTL(699, 949);
                break;
            case "DE":
            case "ES":
            case "IT":
                setDeliveryPricesINTL(799, 999);
                break;
            case "IE":
                setDeliveryPricesINTL(699, 1149);
                break;
            case "MQ":
            case "GY":
            case "RE":
            case "PM":
            case "YT":
                setDeliveryPricesINTL(1199, 1999);
                break;
            case "TF":
            case "WF":
            case "PF":
            case "NC":
                setDeliveryPricesINTL(1199, 2199);
                break;
            default:
                setDeliveryPricesINTL(699, 899);
                break;
        }

    }

    private void printShippingPrices() {
        int tirages = command.getTotalTirages();

        int standardPrice;
        int expressPrice;

        String cCode = CommandHandler.get().getCountryCodeWithDOM(CommandHandler.get().getDeliveryDict().getCountryCode(), CommandHandler.get().getDeliveryDict().getPostalCode());

        if (tirages >= 100) {

            if(cCode.equals("FR")){
                mondialRelais.setDefaultDelivery(true);
                setDeliveryPricesFR(699, -1, 999);
            }else{
                //standard
                switch (cCode) {
                    case "GB":
                        standardPrice = 499;
                        break;
                    case "DK":
                        standardPrice = 899;
                        break;
                    case "ES":
                    case "IT":
                        standardPrice = 599;
                        break;
                    case "EE":
                    case "FI":
                    case "GR":
                    case "IE":
                        standardPrice = 799;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 899;
                        break;
                    default:
                        standardPrice = 699;
                        break;
                }

                //express
                switch (cCode) {
                    case "AT":
                    case "FI":
                        expressPrice = 699;
                        break;
                    case "DE":
                    case "BE":
                    case "LU":
                    case "PT":
                        expressPrice = 749;
                        break;
                    case "IT":
                    case "SK":
                    case "SI":
                    case "SE":
                        expressPrice = 799;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        expressPrice = 899;
                        break;
                    case "IE":
                        expressPrice = 949;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1699;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1899;
                        break;
                    default:
                        expressPrice = 849;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }

        } else if (tirages >= 60) {

            if(cCode.equals("FR")){
                mondialRelais.setDefaultDelivery(true);
                setDeliveryPricesFR(699, -1, 899);
            }else{
                //standard
                switch (cCode) {
                    case "GB":
                        standardPrice = 399;
                        break;
                    case "AT":
                    case "BE":
                    case "EE":
                    case "FI":
                    case "IE":
                    case "IT":
                    case "LU":
                    case "PT":
                        standardPrice = 599;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        standardPrice = 699;
                        break;
                    case "DK":
                        standardPrice = 749;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 899;
                        break;
                    default:
                        standardPrice = 649;
                        break;
                }

                //express
                switch (cCode) {
                    case "AT":
                    case "FI":
                        expressPrice = 699;
                        break;
                    case "DE":
                    case "BE":
                    case "LU":
                    case "PT":
                        expressPrice = 749;
                        break;
                    case "IT":
                    case "SK":
                    case "SI":
                    case "SE":
                        expressPrice = 799;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        expressPrice = 899;
                        break;
                    case "IE":
                        expressPrice = 949;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1699;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1899;
                        break;
                    default:
                        expressPrice = 849;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }

        } else if (tirages >= 51) {

            if(cCode.equals("FR")){
                mondialRelais.setDefaultDelivery(true);
                setDeliveryPricesFR(599, 699, 799);
            }else{
                //standard
                switch (cCode) {
                    case "GB":
                        standardPrice = 399;
                        break;
                    case "AT":
                    case "BE":
                    case "EE":
                    case "FI":
                    case "IE":
                    case "IT":
                    case "LU":
                    case "PT":
                        standardPrice = 599;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        standardPrice = 699;
                        break;
                    case "DK":
                        standardPrice = 749;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 799;
                        break;
                    default:
                        standardPrice = 649;
                        break;
                }

                //express
                switch (cCode) {
                    case "GB":
                        expressPrice = 599;
                        break;
                    case "AT":
                    case "FI":
                        expressPrice = 699;
                        break;
                    case "DE":
                    case "BE":
                    case "LU":
                    case "PT":
                        expressPrice = 749;
                        break;
                    case "IT":
                    case "SK":
                    case "SI":
                    case "SE":
                        expressPrice = 799;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        expressPrice = 899;
                        break;
                    case "IE":
                        expressPrice = 949;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1599;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1799;
                        break;
                    default:
                        expressPrice = 849;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }

        } else if (tirages >= 30) {

            if(cCode.equals("FR")){
                mondialRelais.setDefaultDelivery(true);
                setDeliveryPricesFR(599, 699, 799);
            }else{
                //standard
                switch (cCode) {
                    case "GB":
                        standardPrice = 399;
                        break;
                    case "AT":
                        standardPrice = 549;
                        break;
                    case "BE":
                    case "EE":
                    case "FI":
                    case "IE":
                    case "IT":
                    case "LU":
                    case "PT":
                        standardPrice = 599;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        standardPrice = 699;
                        break;
                    case "DK":
                        standardPrice = 749;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 799;
                        break;
                    default:
                        standardPrice = 649;
                        break;
                }

                //express
                switch (cCode) {
                    case "GB":
                        expressPrice = 599;
                        break;
                    case "AT":
                    case "FI":
                        expressPrice = 699;
                        break;
                    case "DE":
                    case "BE":
                    case "LU":
                    case "PT":
                        expressPrice = 749;
                        break;
                    case "IT":
                    case "SK":
                    case "SI":
                    case "SE":
                        expressPrice = 799;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        expressPrice = 899;
                        break;
                    case "IE":
                        expressPrice = 949;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1599;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1799;
                        break;
                    default:
                        expressPrice = 849;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }

        } else if (tirages >= 11) {

            if(cCode.equals("FR")){
                laPoste.setDefaultDelivery(true);
                setDeliveryPricesFR(599, 499, 699);
            }else{
                //standard
                switch (cCode) {
                    case "GB":
                        standardPrice = 299;
                        break;
                    case "AT":
                    case "BE":
                    case "EE":
                    case "FI":
                    case "IE":
                    case "IT":
                    case "LU":
                    case "PT":
                        standardPrice = 499;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        standardPrice = 599;
                        break;
                    case "DK":
                        standardPrice = 649;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 799;
                        break;
                    default:
                        standardPrice = 549;
                        break;
                }

                //express
                switch (cCode) {
                    case "GB":
                        expressPrice = 499;
                        break;
                    case "DE":
                    case "BE":
                        expressPrice = 649;
                        break;
                    case "AT":
                    case "FI":
                    case "IT":
                    case "LU":
                    case "PT":
                        expressPrice = 699;
                        break;
                    case "NL":
                    case "BG":
                    case "CY":
                    case "HR":
                        expressPrice = 799;
                        break;
                    case "IE":
                        expressPrice = 849;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1599;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1799;
                        break;
                    default:
                        expressPrice = 749;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }

        } else {

            if(cCode.equals("FR")){
                laPoste.setDefaultDelivery(true);
                setDeliveryPricesFR(499, 399, 599);
            }else{
                //standard
                switch (cCode) {
                    case "IT":
                    case "GB":
                    case "BE":
                        standardPrice = 299;
                        break;
                    case "DE":
                        standardPrice = 399;
                        break;
                    case "DK":
                        standardPrice = 549;
                        break;
                    case "NL":
                        standardPrice = 599;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        standardPrice = 699;
                        break;
                    default:
                        standardPrice = 499;
                        break;
                }

                //express
                switch (cCode) {
                    case "BE":
                        expressPrice = 449;
                        break;
                    case "IT":
                    case "GB":
                        expressPrice = 499;
                        break;
                    case "DE":
                        expressPrice = 449;
                        break;
                    case "DK":
                        expressPrice = 749;
                        break;
                    case "NL":
                        expressPrice = 799;
                        break;
                    case "MQ":
                    case "GY":
                    case "RE":
                    case "PM":
                    case "YT":
                        expressPrice = 1499;
                        break;
                    case "TF":
                    case "WF":
                    case "PF":
                    case "NC":
                        expressPrice = 1699;
                        break;
                    default:
                        expressPrice = 699;
                        break;
                }

                standard.setDefaultDelivery(true);
                setDeliveryPricesINTL(standardPrice, expressPrice);
            }


        }
    }

    private void setDeliveryPricesFR(int mondialRelaisPrice, int laPostePrice, int collissimoPrice) {
        mondialRelais.setPrice(mondialRelaisPrice);
        laPoste.setPrice(laPostePrice);
        collissimo.setPrice(collissimoPrice);

        if (mondialRelaisPrice != -1) {
            deliveryMethods.add(mondialRelais);
        }
        if (laPostePrice != -1) {
            deliveryMethods.add(laPoste);
        }
        if (collissimoPrice != -1) {
            deliveryMethods.add(collissimo);
        }
    }

    private void setDeliveryPricesINTL(int laPostePrice, int collissimoPrice) {
        standard.setPrice(laPostePrice);
        express.setPrice(collissimoPrice);

        deliveryMethods.add(standard);
        deliveryMethods.add(express);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);

        updateDeliveryPrice();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryMethodAdapter deliveryMethodAdapter = new DeliveryMethodAdapter(activity, this, deliveryMethods);
        recyclerView.setAdapter(deliveryMethodAdapter);
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {
        Log.i(LOG_TAG, "user EMAIL : " + UserInfo.get("email"));

        if(activity.getDeliveryMethodSelected() == null){
            Toast.makeText(activity, "Please select a method", Toast.LENGTH_SHORT).show();
            return;
        }

        startPaymentFragment(activity, FRAGMENT_TRANSACTION_TAG);
    }

    public static void startPaymentFragment(BasketActivity activity, String fragmentTransactionTag) {
        if(activity.getDeliveryMethodSelected().getIdentifier().equals("MRPRL") && CommandHandler.get().getRelayId().equals("")){
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            RelayWebViewFragment fragment = new RelayWebViewFragment();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();

            return;
        }

        CommandHandler.get().delivery = activity.getDeliveryMethodSelected();

        try {
            CommandHandler.get().initUserAddress();
            CommandHandler.get().initArticles();
            CommandHandler.get().finishXML();

            AWSHandler.get().uploadXMLOrder();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (AWSHandler.get().getPercentUpload() != 100) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            UploadCommandFragment uploadCommandFragment = new UploadCommandFragment();
            ft.replace(R.id.fragment_container, uploadCommandFragment);
            ft.addToBackStack(fragmentTransactionTag);
            ft.commit();
        } else {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            PaymentFragment paymentFragment = new PaymentFragment();
            ft.replace(R.id.fragment_container, paymentFragment);
            ft.addToBackStack(fragmentTransactionTag);
            ft.commit();
        }
    }

    public void updateDeliveryPrice() {
        String deliveryPriceStr = Command.convertPriceToString(activity.getDeliveryMethodPriceInCts());
        deliveryPriceTV.setText(deliveryPriceStr);
    }
}
