package com.photoweb.piiics.fragments.BurgerMenuFragments.MyAccountViewPagerFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.CommandCompletedAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.UserCommandCompleted;
import com.photoweb.piiics.utils.BackendAPI;
import com.photoweb.piiics.utils.NetworkUtils;
import com.photoweb.piiics.utils.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by thomas on 06/09/2017.
 */

public class CommandsFragment extends BaseFragment {
    private static final String LOG_TAG = "CommandsFragment";

    CommandCompletedAdapter commandCompletedAdapter;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.recyclerView)
    RecyclerView commandsRV;

    @BindView(R.id.emptyView)
    TextView emptyViewTV;

    private static SharedPreferences prefs;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_account_commands;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = getActivity().getSharedPreferences("USER_INFO", MODE_PRIVATE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commandsRV.setLayoutManager(layoutManager);
        commandCompletedAdapter = new CommandCompletedAdapter(((MainActivity) getActivity()));
        commandsRV.setAdapter(commandCompletedAdapter);

        if (UserInfo.get().get("id") == null || (int) UserInfo.get().get("id") == 0) {
            commandsRV.setVisibility(View.GONE);
            emptyViewTV.setVisibility(View.VISIBLE);
        } else {
            queryUserCommands();
        }
    }

    private void queryUserCommands() {
        progressBar.setVisibility(View.VISIBLE);

        int userID = (int) UserInfo.get().get("id");
        Log.i(LOG_TAG, "userID : " + String.valueOf(userID));
        Log.i(LOG_TAG, "token : " + prefs.getString("token", ""));
        Log.i(LOG_TAG, "userID : " + String.valueOf(userID));

      //  userID =  9;//test

        BackendAPI.getUserCommands(String.valueOf(userID), prefs.getString("token", ""), new BackendAPI.ResponseListener<ArrayList<UserCommandCompleted>>() {
            @Override
            public void perform(ArrayList<UserCommandCompleted> obj, int s, String errmsg) {

                if(progressBar != null)
                    progressBar.setVisibility(View.INVISIBLE);

                if (obj != null) {
                    ArrayList<UserCommandCompleted> commandCompleteds = obj;

                    Collections.sort(commandCompleteds, new Comparator<UserCommandCompleted>() {
                        public int compare(UserCommandCompleted o1, UserCommandCompleted o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });

                    commandCompletedAdapter.updateModel(commandCompleteds);
                    Log.i(LOG_TAG, "GetUserCommandCompleted good");
                    //Log.i(LOG_TAG, "body : \n" + response.body().toString());

                    //   handleEmptyView();
                } else {
                    Log.i(LOG_TAG, "request auth failed with error code: " );

                }
            }
        });

    }
}
