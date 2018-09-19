package com.photoweb.piiics.fragments.BasketFragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.SummaryAdapter;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.FormatReference;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PopUps;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 13/07/2017.
 */

public class SummaryFragment extends BaseFragment {
    public static final String FRAGMENT_TRANSACTION_TAG = "SummaryFragment";
    BasketActivity activity;
    Command command;

    @BindView(R.id.commandType)
    TextView commandTypeTV;

    @BindView(R.id.total_price)
    TextView totalPriceTV;

    @BindView(R.id.prints_total_price)
    TextView printsTotalPriceTV;

    @BindView(R.id.prints_list_view)
    RecyclerView printsRV;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_summary;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BasketActivity) getActivity());
        command = activity.getCommand();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.updateStepsBar(this);
        activity.setAlbumOptionsMode(false);

        initTotalPriceTV();
        initCommandTypeTV();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        printsRV.setLayoutManager(layoutManager);
        SummaryAdapter summaryAdapter = new SummaryAdapter(activity.getPics(), getContext());
        printsRV.setAdapter(summaryAdapter);

        sendCart();

    }

    private void initTotalPriceTV() {
        try {
            String totalPriceStr;
            if (command.getProduct().equals("ALBUM")) {
                int totalPrice = command.getAllPicsPrice() + command.getAlbumOptions().getOptionsTotalPrice();
                totalPriceStr = Command.convertPriceToString(totalPrice);
            } else {
                totalPriceStr = activity.getCommand().getAllPicsPriceStr();
            }
            totalPriceTV.setText(totalPriceStr);
            printsTotalPriceTV.setText(totalPriceStr);
        } catch (PriceSecurityException pse) {
            PopUps.popUpFatalError(activity, PriceSecurityException.getErrorTitle(), PriceSecurityException.getErrorMessage());
        }
    }

    private void initCommandTypeTV() {
        if (command.getProduct().equals("PRINT")) {
            commandTypeTV.setText(R.string.PRINT);
        } else {
            commandTypeTV.setText(R.string.SINGLE_ALBUM);
        }
    }

    private void sendCart()
    {
        SharedPreferences prefs = activity.getSharedPreferences("USER_INFO", MODE_PRIVATE);

        int free = 0;

        if(command.getProduct().equals("PRINT")){
            int free_available = UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");


            ArrayList<EditorPic> pics = command.getEditorPics();
            for (EditorPic pic : pics) {
                FormatReference fm;

                if(pic.getFormatReference() == null){
                    fm = PriceReferences.getDefaultformat();
                }else{
                    fm = pic.getFormatReference();
                }

                if (fm != null && fm.getName().equals(PriceReferences.STANDARD_FORMAT)) {
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

        BackendAPI.addsale(command.getCommandID(), UserInfo.getInt("id"), command.getProduct(), ((float) CommandHandler.get().getTotalPrice()) / 100, free, "pending", "", CommandHandler.get().getSaleAddress("delivery"), CommandHandler.get().getSaleAddress("billing"), prefs.getString("token", ""), new BackendAPI.ResponseListener<UserCommandCompleted>() {
            @Override
            public void perform(UserCommandCompleted obj, int s, String errmsg) {


            }
        });
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        DeliveryAddressFragment deliveryAddressFragment = new DeliveryAddressFragment();
        ft.replace(R.id.fragment_container, deliveryAddressFragment, activity.getDeliveryAddressTag());
        ft.addToBackStack(FRAGMENT_TRANSACTION_TAG);
        ft.commit();
    }
}
