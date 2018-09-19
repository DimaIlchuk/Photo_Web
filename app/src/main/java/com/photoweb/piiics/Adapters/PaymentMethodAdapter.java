package com.photoweb.piiics.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.fragments.BasketFragments.PaymentFragment;
import com.photoweb.piiics.model.PaymentMethod;

import java.util.ArrayList;

public class PaymentMethodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "PaymengtMethodAdapter";

    private BasketActivity activity;
    private ArrayList<PaymentMethod> paymentMethods;
    private PaymentFragment fragment;
    private static int positionSelected = -1;

    public PaymentMethodAdapter(BasketActivity activity, PaymentFragment fragment, ArrayList<PaymentMethod> paymentMethods){
        this.activity = activity;
        this.paymentMethods = paymentMethods;
        this.fragment = fragment;
        if (positionSelected == -1) {
            Log.i(LOG_TAG, "positionSelected = -1");
            positionSelected = findPositionSelected(paymentMethods);

            if(positionSelected != -1)
                activity.setPaymentMethodSelected(paymentMethods.get(positionSelected));
        }
    }

    private int findPositionSelected(ArrayList<PaymentMethod> paymentMethods) {
        int i = 0;

        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.isDefaultPayment()) {
                return i;
            }
            i++;
        }

        return -1;
    }

    @NonNull
    @Override
    public PaymentMethodItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout mainView;
        PaymentMethodItem vh;

        // create a new view
        mainView = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_method, parent, false);

        vh = new PaymentMethodItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final PaymentMethodItem paymentMethodItem = (PaymentMethodItem) holder;
        final PaymentMethod paymentMethodSelected = paymentMethods.get(position);

        paymentMethodItem.paymentNameTV.setText(paymentMethodSelected.getPaymentName());

        if(paymentMethodSelected.getPaymentMethodInfos().equals("")){
            paymentMethodItem.paymentInfosTV.setVisibility(View.GONE);
        }else{
            paymentMethodItem.paymentInfosTV.setText(paymentMethodSelected.getPaymentMethodInfos());
            paymentMethodItem.paymentInfosTV.setVisibility(View.VISIBLE);
        }

        if (position == positionSelected) {
            paymentMethodItem.checkIV.setImageResource(R.drawable.confirmation);
        } else {
            paymentMethodItem.checkIV.setImageResource(R.drawable.bouton);
        }

        paymentMethodItem.mainLayoutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setPaymentMethodSelected(paymentMethodSelected);

                positionSelected = position;
                notifyDataSetChanged();

                if(position == 0){
                    activity.startStripePayment();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public static class PaymentMethodItem extends RecyclerView.ViewHolder {

        public RelativeLayout mainLayoutRL;

        public TextView paymentNameTV;
        public TextView paymentInfosTV;

        public ImageView checkIV;

        public PaymentMethodItem(RelativeLayout mainView) {
            super(mainView);

            mainLayoutRL = mainView;
            paymentNameTV = mainView.findViewById(R.id.payment_name);
            paymentInfosTV = mainView.findViewById(R.id.payment_method_infos);
            checkIV = mainView.findViewById(R.id.check);
        }
    }
}
