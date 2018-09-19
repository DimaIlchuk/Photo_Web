package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.SummaryCategory;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.PriceReferences;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by thomas on 11/08/2017.
 */

public class CommandDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "CommandDetailsAdapter";

    UserCommandCompleted commandCompleted;

    private SummaryCategory standardFormat;
    private SummaryCategory squareFormat;
    private SummaryCategory panoramicFormat;
    private SummaryCategory stickers;
    private SummaryCategory backgrounds;
    private ArrayList<SummaryCategory> summaryCategories;

    Context mContext;

    public CommandDetailsAdapter(UserCommandCompleted commandCompleted, Context context) {
        this.commandCompleted = commandCompleted;
        this.mContext = context;

        initSymmaryCategories();
        regroupSummaryCategories();
        fillSummaryCategories();
        removeEmptyCategories();
    }

    private void initSymmaryCategories() {
        standardFormat = new SummaryCategory("Tirages standard", R.drawable.standard_blanc, PriceReferences.STANDARD_FORMAT);
        squareFormat = new SummaryCategory("Tirages carré", R.drawable.carre_blanc, PriceReferences.SQUARE_FORMAT);
        panoramicFormat = new SummaryCategory("Tirages panoramique", R.drawable.panoramique_blanc, PriceReferences.PANORAMIC_FORMAT);
        stickers = new SummaryCategory("Stickers", R.drawable.smiley_blanc, PriceReferences.STICKERS);
        backgrounds = new SummaryCategory("Fonds", R.drawable.fonds_blanc, PriceReferences.BACKGROUNDS);
    }

    private void regroupSummaryCategories() {
        summaryCategories = new ArrayList<>();

        summaryCategories.add(standardFormat);
        summaryCategories.add(squareFormat);
        summaryCategories.add(panoramicFormat);
        summaryCategories.add(stickers);
        summaryCategories.add(backgrounds);
    }

    private void fillSummaryCategories() {
        for (UserCommandCompleted.Item item : commandCompleted.getItems()) {
            Log.i(LOG_TAG, "Item name : " + item.getName());
            if (item.getName().equals("Sticker")) {
                stickers.addUnits(item.getQuantity());
                stickers.addPrice(item.getPrice());
            } else if (item.getName().equals("Background")) {
                backgrounds.addUnits(item.getQuantity());
                backgrounds.addPrice(item.getPrice());
            } else if (item.getName().equals("1796 : 1205")) {
                standardFormat.addUnits(item.getQuantity());
                standardFormat.addPrice(item.getPrice());
            } else if (item.getName().equals("1205 : 1205")) {
                squareFormat.addUnits(item.getQuantity());
                squareFormat.addPrice(item.getPrice());
            } else if (item.getName().equals("2138 : 1205")) {
                panoramicFormat.addUnits(item.getQuantity());
                panoramicFormat.addPrice(item.getPrice());
            } else {
                Log.i(LOG_TAG, "item name unknow !");
            }
        }
    }

    private boolean isDefaultBackground(BackgroundReference backgroundReference) {
        if (backgroundReference.getName().equals(PriceReferences.getDefaultBackground().getName())) {
            return true;
        }
        return false;
    }

    private void fillSummaryCategoriesFormat(SummaryCategory summaryCategoryFormat, EditorPic editorPic) throws PriceSecurityException {
        summaryCategoryFormat.addUnits(editorPic.getCopy());
        summaryCategoryFormat.pics.add(editorPic);

        int priceFormatInCts = editorPic.getFormatReference().getCurPrice();
        summaryCategoryFormat.addPrice(priceFormatInCts * editorPic.getCopy());
    }


    private void removeEmptyCategories() {
        Iterator it = summaryCategories.iterator();
        while (it.hasNext()) {
            SummaryCategory summaryCategory = (SummaryCategory) it.next();
            if (summaryCategory.units == 0) {
                it.remove();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        SummaryItem summaryItem;

        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_listview_item, parent, false);
        summaryItem = new SummaryItem(mainView);
        return summaryItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SummaryCategory summaryCategory = summaryCategories.get(position);
        SummaryItem summaryItem = (SummaryItem) holder;

        summaryItem.elementIcon.setImageResource(summaryCategory.categoryIconRessource);
        summaryItem.elementName.setText(summaryCategory.categoryName);
        summaryItem.elementUnits.setText(String.valueOf(summaryCategory.units));
        if (summaryCategory.priceInCts == 0) {
            summaryItem.elementPrice.setText(mContext.getString(R.string.FREE));
        } else {
            summaryItem.elementPrice.setText(Command.convertPriceToString(summaryCategory.priceInCts));
        }
    }

    @Override
    public int getItemCount() {
        return summaryCategories.size();
    }

    private class SummaryItem extends RecyclerView.ViewHolder {

        ImageView elementIcon;
        TextView elementName;
        TextView elementUnits;
        TextView elementPrice;

        public SummaryItem(View itemView) {
            super(itemView);

            elementIcon = (ImageView) itemView.findViewById(R.id.element_icon);
            elementName = (TextView) itemView.findViewById(R.id.element_name);
            elementUnits = (TextView) itemView.findViewById(R.id.element_units);
            elementPrice = (TextView) itemView.findViewById(R.id.element_price);
        }
    }
}
