package com.photoweb.piiics.Adapters;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments.CommandCompletedDetailsFragment;
import com.photoweb.piiics.model.FAQReceiver;
import com.photoweb.piiics.model.UserCommandCompleted;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by thomas on 06/09/2017.
 */

public class CommandCompletedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "CommandCompletedAdapter";

    MainActivity activity;
    ArrayList<UserCommandCompleted> userCommandCompleteds;

    public CommandCompletedAdapter(MainActivity activity) {
        this.userCommandCompleteds = null;
        this.activity = activity;
    }

    public void updateModel(ArrayList<UserCommandCompleted> userCommandCompleteds) {
        this.userCommandCompleteds = userCommandCompleteds;
        notifyDataSetChanged();
    }

    @Override
    public CommandItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout mainView;
        CommandItem vh;

        // create a new view
        mainView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_account_command_completed, parent, false);

        vh = new CommandItem(mainView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CommandItem commandItem = (CommandItem) holder;
        final UserCommandCompleted commandCompleted = userCommandCompleteds.get(position);

        int commandNumber = userCommandCompleteds.size() - (position);
        final String commandTitle = activity.getString(R.string.CMD_NB) + String.valueOf(commandNumber);
        commandItem.commandTitleTV.setText(commandTitle);

        String formattedDate = commandCompleted.getFormattedDate();
        if (formattedDate != null) {
            commandItem.dateTV.setText(formattedDate);
        } else {
            commandItem.dateTV.setText("Inconnu");
        }

        commandItem.priceTV.setText(commandCompleted.getFormattedPrice());

        commandItem.mainLayoutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "OnClick");

                commandCompleted.setCommandTitle(commandTitle);
                activity.setSelectedCommand(commandCompleted);
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new CommandCompletedDetailsFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (userCommandCompleteds == null) {
            return 0;
        } else {
            return userCommandCompleteds.size();
        }
    }

    public static class CommandItem extends RecyclerView.ViewHolder {

        LinearLayout mainView;

        RelativeLayout mainLayoutRL;
        TextView commandTitleTV;
        TextView dateTV;
        TextView priceTV;

        public CommandItem(LinearLayout mainView) {
            super(mainView);
            this.mainView = mainView;
            this.mainLayoutRL = (RelativeLayout) mainView.findViewById(R.id.mainLayout);
            this.commandTitleTV = (TextView) mainView.findViewById(R.id.commandTitle);
            this.dateTV = (TextView) mainView.findViewById(R.id.date);
            this.priceTV = (TextView) mainView.findViewById(R.id.price);
        }
    }
}
