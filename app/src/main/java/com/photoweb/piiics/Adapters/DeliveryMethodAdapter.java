package com.photoweb.piiics.Adapters;

import android.support.v4.app.FragmentTransaction;
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
import com.photoweb.piiics.fragments.BasketFragments.DeliveryMethodFragment;
import com.photoweb.piiics.fragments.BasketFragments.RelayWebViewFragment;
import com.photoweb.piiics.model.DeliveryMethod;

import java.util.ArrayList;

/**
 * Created by thomas on 03/08/2017.
 */

public class DeliveryMethodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "DeliveryMethodAdapter";

    private BasketActivity activity;
    private ArrayList<DeliveryMethod> deliveryMethods;
    private DeliveryMethodFragment fragment;
    private int defaultDeliveryMethodPrice;
    private static int positionSelected = -1;

    public DeliveryMethodAdapter(BasketActivity activity, DeliveryMethodFragment fragment, ArrayList<DeliveryMethod> deliveryMethodsArgs) {
        this.activity = activity;
        deliveryMethods = deliveryMethodsArgs;
        this.fragment = fragment;
        this.defaultDeliveryMethodPrice = activity.getDeliveryMethodPriceInCts();
        if (positionSelected == -1) {
            Log.i(LOG_TAG, "positionSelected = -1");
            positionSelected = findPositionSelected(deliveryMethods);
            activity.setDeliveryMethodSelected(deliveryMethods.get(positionSelected));
        }
    }

    private int findPositionSelected(ArrayList<DeliveryMethod> deliveryMethods) {
        int i = 0;
        for (DeliveryMethod deliveryMethod : deliveryMethods) {
            if (deliveryMethod.isDefaultDelivery()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public DeliveryMethodItem onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout mainView;
        DeliveryMethodItem vh;

        // create a new view
        mainView = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_method, parent, false);

        vh = new DeliveryMethodItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DeliveryMethodItem deliveryMethodItem = (DeliveryMethodItem) holder;
        final DeliveryMethod deliveryMethodSelected = deliveryMethods.get(position);

        deliveryMethodItem.deliveryCompanyNameTV.setText(deliveryMethodSelected.getCompanyName());
        deliveryMethodItem.deliveryInfosTV.setText(deliveryMethodSelected.getDeliveryMethodInfos());

        int priceDifference = deliveryMethodSelected.getPrice() - defaultDeliveryMethodPrice;
        deliveryMethodItem.priceTV.setText(deliveryMethodSelected.getPriceStr(priceDifference));

        if (position == positionSelected) {
            deliveryMethodItem.checkIV.setImageResource(R.drawable.confirmation);
            deliveryMethodItem.priceTV.setVisibility(View.INVISIBLE);
        } else {
            deliveryMethodItem.checkIV.setImageResource(R.drawable.bouton);
            deliveryMethodItem.priceTV.setVisibility(View.VISIBLE);
        }

        deliveryMethodItem.mainLayoutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setDeliveryMethodSelected(deliveryMethodSelected);

                activity.setDeliveryMethodPriceInCts(deliveryMethodSelected.getPrice());
                fragment.updateDeliveryPrice();

                defaultDeliveryMethodPrice = deliveryMethodSelected.getPrice();
                positionSelected = position;
                notifyDataSetChanged();

                if(deliveryMethodSelected.getIdentifier().equals("MRPRL")){
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    RelayWebViewFragment fragment = new RelayWebViewFragment();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryMethods.size();
    }


    public static class DeliveryMethodItem extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout mainLayoutRL;

        public TextView deliveryCompanyNameTV;
        public TextView deliveryInfosTV;
        public TextView priceTV;

        public ImageView checkIV;

        public DeliveryMethodItem(RelativeLayout mainView) {
            super(mainView);

            mainLayoutRL = mainView;
            deliveryCompanyNameTV = mainView.findViewById(R.id.company_name);
            deliveryInfosTV = mainView.findViewById(R.id.delivery_method_infos);
            priceTV = mainView.findViewById(R.id.price);
            checkIV = mainView.findViewById(R.id.check);
        }
    }
}
