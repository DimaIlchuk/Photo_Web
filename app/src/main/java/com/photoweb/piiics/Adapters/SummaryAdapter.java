package com.photoweb.piiics.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.model.AlbumOptions;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.model.PriceReferences.BackgroundReference;
import com.photoweb.piiics.model.SummaryCategory;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.PriceReferences;
import com.photoweb.piiics.utils.Promotions;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by thomas on 11/08/2017.
 */

public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<EditorPic> pics;
    private Command command;
    private Context mContext;

    private SummaryCategory standardFormat;
    private SummaryCategory squareFormat;
    private SummaryCategory panoramicFormat;
    private SummaryCategory stickers;
    private SummaryCategory backgrounds;

    private SummaryCategory albumNoLogo;
    private SummaryCategory albumStrongCover;
    private SummaryCategory albumVarnishedPages;
    private SummaryCategory albumMateCover;
    private SummaryCategory additionalAlbums;
    private SummaryCategory pageFormat;

    private ArrayList<SummaryCategory> summaryCategories;

    public SummaryAdapter(ArrayList<EditorPic> pics, Context context) {
        this.pics = pics;
        this.command = CommandHandler.get().currentCommand;
        this.mContext = context;

        initSymmaryCategories();
        regroupSummaryCategories();
        fillSummaryCategories();
        removeEmptyCategories();
        computePromotions();

        if (command.getProduct().equals("ALBUM")) {
            addAlbumOptionsCategories();
        }
    }

    private void initSymmaryCategories() {
        standardFormat = new SummaryCategory(mContext.getString(R.string.CAT_STANDARD), R.drawable.standard_blanc, PriceReferences.STANDARD_FORMAT);
        squareFormat = new SummaryCategory(mContext.getString(R.string.CAT_SQUARE), R.drawable.carre_blanc, PriceReferences.SQUARE_FORMAT);
        panoramicFormat = new SummaryCategory(mContext.getString(R.string.CAT_PANO), R.drawable.panoramique_blanc, PriceReferences.PANORAMIC_FORMAT);
        pageFormat = new SummaryCategory(mContext.getString(R.string.PAGES), R.drawable.additional_page, PriceReferences.PAGE_FORMAT);
        stickers = new SummaryCategory(mContext.getString(R.string.STICKERS), R.drawable.smiley_blanc, PriceReferences.STICKERS);
        backgrounds = new SummaryCategory(mContext.getString(R.string.BACKGROUND), R.drawable.fonds_blanc, PriceReferences.BACKGROUNDS);

        albumNoLogo = new SummaryCategory(mContext.getString(R.string.REMOVELOGO), R.drawable.gabarits_blanc);
        albumStrongCover = new SummaryCategory(mContext.getString(R.string.PRESTIGE), R.drawable.gabarits_blanc);
        albumVarnishedPages = new SummaryCategory(mContext.getString(R.string.CAT_VARNISHED), R.drawable.gabarits_blanc);
        albumMateCover = new SummaryCategory(mContext.getString(R.string.CAT_MATE), R.drawable.gabarits_blanc);
        additionalAlbums = new SummaryCategory(mContext.getString(R.string.CAT_ADDITIONAL), R.drawable.gabarits_blanc);

    }

    private void regroupSummaryCategories() {
        summaryCategories = new ArrayList<>();

        summaryCategories.add(standardFormat);
        summaryCategories.add(squareFormat);
        summaryCategories.add(panoramicFormat);
        summaryCategories.add(pageFormat);
        summaryCategories.add(stickers);
        summaryCategories.add(backgrounds);
    }

    private void fillSummaryCategories() {
        try {
            int albumQuantity = (command.getAlbumOptions() == null) ? 1 : command.getAlbumOptions().getBookQuantity();
            for (EditorPic editorPic : pics) {
                stickers.addUnits(editorPic.getStickersSize() * editorPic.getCopy() * albumQuantity);
                stickers.addPrice(editorPic.getStickersPrice() * editorPic.getCopy() * albumQuantity);
                if (editorPic.getBackgroundReference() != null && !isDefaultBackground(editorPic.getBackgroundReference())) {
                    backgrounds.addUnits(editorPic.getCopy() * albumQuantity);
                    backgrounds.addPrice(editorPic.getBackgroundPrice() * albumQuantity);
                }

                if(CommandHandler.get().currentCommand.getProduct().equals("PRINT")){
                    if(editorPic.getFormatReference() == null){
                        fillSummaryCategoriesFormat(standardFormat, editorPic);
                    }else if (editorPic.getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)) {
                        fillSummaryCategoriesFormat(standardFormat, editorPic);
                    } else if (editorPic.getFormatReference().getName().equals(PriceReferences.SQUARE_FORMAT)) {
                        fillSummaryCategoriesFormat(squareFormat, editorPic);
                    } else if (editorPic.getFormatReference().getName().equals(PriceReferences.PANORAMIC_FORMAT)) {
                        fillSummaryCategoriesFormat(panoramicFormat, editorPic);
                    }

                    CommandHandler.get().articles.put("10x15", standardFormat);
                    CommandHandler.get().articles.put("10x10", squareFormat);
                    CommandHandler.get().articles.put("10x18", panoramicFormat);
                }else{
                    fillSummaryCategoriesFormat(pageFormat, editorPic);

                    CommandHandler.get().articles.put("page", pageFormat);
                }
            }

        } catch (PriceSecurityException pse) {
            pse.printStackTrace();
            //todo: error message
        }
    }

    private boolean isDefaultBackground(BackgroundReference backgroundReference) {
        if (backgroundReference == null || backgroundReference.getName().equals(PriceReferences.getDefaultBackground().getName())) {
            return true;
        }
        return false;
    }

    private void fillSummaryCategoriesFormat(SummaryCategory summaryCategoryFormat, EditorPic editorPic) throws PriceSecurityException {
        int albumQuantity = (command.getAlbumOptions() == null) ? 1 : command.getAlbumOptions().getBookQuantity();

        summaryCategoryFormat.addUnits(editorPic.getCopy() * albumQuantity);
        summaryCategoryFormat.pics.add(editorPic);

        int priceFormatInCts = editorPic.getFormatReference().getCurPrice();
        summaryCategoryFormat.addPrice(priceFormatInCts * editorPic.getCopy() * albumQuantity);
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

    private void computePromotions() {
        standardFormat.priceInCts = Promotions.checkFreeStandardFormatPromotion(standardFormat.priceInCts, pics);
        pageFormat.priceInCts = Promotions.checkFreePagePromotion(pageFormat.priceInCts, pics);
        if(pageFormat.priceInCts == 0){
            summaryCategories.remove(pageFormat);
        }
    }

    private void addAlbumOptionsCategories() {
        AlbumOptions albumOptions = command.getAlbumOptions();

        if (albumOptions.isHasNoLogo()) {
            try {
                albumNoLogo.addPrice(albumOptionTotalPrice(albumOptions.getNoLogoOptionPrice()));
                albumNoLogo.units = albumOptions.getBookQuantity();
                summaryCategories.add(albumNoLogo);
            } catch (PriceSecurityException pse) {
            }
        }
        if (albumOptions.isHasStrongCover()) {
            try {
                albumStrongCover.addPrice(albumOptionTotalPrice(albumOptions.getStrongCoverOptionPrice()));
                albumStrongCover.units = albumOptions.getBookQuantity();
                summaryCategories.add(albumStrongCover);
            } catch (PriceSecurityException pse) {
            }
        }
        if (albumOptions.isHasVarnishedPages()) {
            try {
                albumVarnishedPages.addPrice(albumOptionTotalPrice(albumOptions.getVarnishedPagesOptionPrice()));
                albumVarnishedPages.units = albumOptions.getBookQuantity();
                summaryCategories.add(albumVarnishedPages);
            } catch (PriceSecurityException pse) {
            }
        }
        if (albumOptions.isHasMateCover()) {
            try {
                albumMateCover.addPrice(albumOptionTotalPrice(albumOptions.getMateCoverOptionPrice()));
                albumMateCover.units = albumOptions.getBookQuantity();
                summaryCategories.add(albumMateCover);
            } catch (PriceSecurityException pse) {
            }
        }
        if (albumOptions.getBookQuantity() > 1) {
            try {
                int additionalAlbumsUnits = albumOptions.getBookQuantity() - UserInfo.getInt("book_available");
                additionalAlbums.addPrice(albumOptions.getAdditionalBooksPrice() * additionalAlbumsUnits);
                additionalAlbums.units = additionalAlbumsUnits;
                summaryCategories.add(additionalAlbums);
            } catch (PriceSecurityException pse) {
            }
        }
    }

    private int albumOptionTotalPrice(int unitPrice) {
        return unitPrice * ((command.getAlbumOptions() == null) ? 1 : command.getAlbumOptions().getBookQuantity());
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

        if (summaryCategory.units == 0) {
            summaryItem.elementUnits.setVisibility(View.GONE);
        } else {
            summaryItem.elementUnits.setVisibility(View.VISIBLE);
            summaryItem.elementUnits.setText(String.valueOf(summaryCategory.units));
        }

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

            elementIcon = itemView.findViewById(R.id.element_icon);
            elementName = itemView.findViewById(R.id.element_name);
            elementUnits = itemView.findViewById(R.id.element_units);
            elementPrice = itemView.findViewById(R.id.element_price);
        }
    }
}
