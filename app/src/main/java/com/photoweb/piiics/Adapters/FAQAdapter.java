package com.photoweb.piiics.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.model.FAQReceiver;

import java.util.ArrayList;

/**
 * Created by thomas on 06/09/2017.
 */

public class FAQAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FAQReceiver faqReceiver;

    public FAQAdapter() {
        this.faqReceiver = null;
    }

    public void updateModel(FAQReceiver faqReceiver) {
        this.faqReceiver = faqReceiver;
        notifyDataSetChanged();
    }

    @Override
    public FAQItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        FAQItem vh;

        // create a new view
        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_item, parent, false);

        vh = new FAQItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FAQItem faqItem = (FAQItem) holder;
        FAQReceiver.Content content = faqReceiver.getContents().get(position);

        faqItem.questionTV.setText(content.getQuestion());
        faqItem.answerTV.setText(content.getAnswer());

        faqItem.answerLL.setVisibility(View.GONE);

        faqItem.questionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faqItem.answerLL.getVisibility() == View.GONE) {
                    faqItem.arrowIV.animate().rotation(180).start();
                    faqItem.answerLL.setVisibility(View.VISIBLE);
                    faqItem.answerLL.requestFocus();

                } else {
                    faqItem.arrowIV.animate().rotation(0).start();
                    faqItem.answerLL.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (faqReceiver == null) {
            return 0;
        } else {
            return faqReceiver.getContents().size();
        }
    }

    public static class FAQItem extends RecyclerView.ViewHolder {

        LinearLayout mainView;

        RelativeLayout questionRL;
        TextView questionTV;
        ImageView arrowIV;

        LinearLayout answerLL;
        TextView answerTV;

        public FAQItem(LinearLayout mainView) {
            super(mainView);
            this.mainView = mainView;
            this.questionRL = (RelativeLayout) mainView.findViewById(R.id.questionRL);
            this.questionTV = (TextView) mainView.findViewById(R.id.questionTV);
            this.arrowIV = (ImageView) mainView.findViewById(R.id.arrowIV);
            this.answerLL = (LinearLayout) mainView.findViewById(R.id.responseLL);
            this.answerTV = (TextView) mainView.findViewById(R.id.responseTV);
        }
    }
}
