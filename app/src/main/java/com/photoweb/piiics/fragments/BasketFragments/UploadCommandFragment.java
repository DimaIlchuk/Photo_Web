package com.photoweb.piiics.fragments.BasketFragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.UploadService;
import com.photoweb.piiics.activities.BasketActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 19/09/2017.
 */

public class UploadCommandFragment extends BaseFragment {
    private static final String LOG_TAG = "UploadCommandFragment";
    @BindView(R.id.percentage)
    TextView percentageTV;

    @BindView(R.id.textview_infos)
    TextView infos;

    @BindView(R.id.progress_bar)
    ProgressBar progress;

    @BindView(R.id.inprogress)
    LinearLayout llInProgress;

    @BindView(R.id.finished) LinearLayout llFinished;

    Boolean status = false;
    int count = 0;
    int nb = 0;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_upload_command;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("UPLOAD", "connectivity status : " + isOnline());

        if(isOnline()){
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onEventReceived,
                    new IntentFilter("AWS_Upload_Finish"));

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onErrorReceived,
                    new IntentFilter("AWS_Upload_Finish_With_Errors"));

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onProgressReceived,
                    new IntentFilter("AWS_Upload_Progress"));

            //updatePercentageTV();

            final BasketActivity activity = ((BasketActivity) getActivity());
            Command command = activity.getCommand();

            if(command == null){
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activity);
                }
                builder.setTitle(R.string.ERROR)
                        .setMessage(R.string.GENERAL_ERROR)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return;
            }

            ArrayList<EditorPic> pics = command.getEditorPics();
            ArrayList<String> arrayList = new ArrayList<>();

            for (EditorPic pic:pics) {
                Log.d(LOG_TAG, "Upload file : " + pic.getFinalBitmapPath());
                arrayList.add(pic.getFinalBitmapPath() + "P111c5" + "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/" + pic.getBitmapName());
            }

            if(CommandHandler.get().currentCommand.getProduct().equals("ALBUM")) {
                arrayList.add(CommandHandler.get().currentCommand.getAlbumFrontCover().getFinalBitmapPath() + "P111c5" + "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/" + CommandHandler.get().currentCommand.getAlbumFrontCover().getPhotoID() + ".png");
                arrayList.add(CommandHandler.get().currentCommand.getAlbumBackCover().getFinalBitmapPath() + "P111c5" + "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/" + CommandHandler.get().currentCommand.getAlbumBackCover().getPhotoID() + ".png");
            }

            String file = CommandHandler.get().getFinalXMl();
            arrayList.add(file + "P111c5" + "SEND/" + CommandHandler.get().currentCommand.getProduct() + "/" + CommandHandler.get().currentCommand.getCommandID() + "/order.xml");

            count = arrayList.size();

            Intent intent = new Intent(getContext(), UploadService.class);
            intent.putStringArrayListExtra("array", arrayList);
            ContextCompat.startForegroundService(activity, intent);
        }
    }

    private BroadcastReceiver onErrorReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onEventReceived: " + intent);
            Toast.makeText(getActivity(), "Des chargements ont échoués", Toast.LENGTH_SHORT).show();
            //updatePercentageTV();
            if(progress != null)
                progress.setVisibility(View.INVISIBLE);

            updatePercentageTV();

            llInProgress.setVisibility(View.GONE);
            llFinished.setVisibility(View.VISIBLE);
            status = true;
        }
    };

    private BroadcastReceiver onEventReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onEventReceived: " + intent);
            //updatePercentageTV();
            if(progress != null)
                progress.setVisibility(View.INVISIBLE);

            updatePercentageTV();

            if(llInProgress != null)
                llInProgress.setVisibility(View.GONE);

            if(llFinished != null)
                llFinished.setVisibility(View.VISIBLE);
            status = true;
        }
    };

    private BroadcastReceiver onProgressReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePercentageTV();
        }
    };

    @OnClick(R.id.buttonFinish)
    public void setOnClickInfos(){
        if(status){
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onEventReceived);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onErrorReceived);

            NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(UploadService.id);

            getActivity().stopService(new Intent(getContext(), UploadService.class));

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            PaymentFragment paymentFragment = new PaymentFragment();
            ft.replace(R.id.fragment_container, paymentFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void updatePercentageTV() {
        nb++;

        if(nb > count){
            nb--;
        }

        int percentage = Math.round(100*nb/count);
        String percentageStr = String.valueOf(percentage) + "%";

        if(percentageTV != null)
            percentageTV.setText(percentageStr);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
