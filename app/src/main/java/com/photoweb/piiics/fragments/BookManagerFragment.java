package com.photoweb.piiics.fragments;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.photoweb.piiics.Adapters.BookManagerGridAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BookManagerActivity;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 22/09/2017.
 */

public class BookManagerFragment extends BaseFragment {
    private final static String LOG_TAG = "BookManagerFragment";

    @BindView(R.id.frontCoverIV)
    ImageView frontCoverIV;

    @BindView(R.id.bgFrontCoverIV)
    ImageView bgfrontCoverIV;

    @BindView(R.id.frontCoverPB)
    ProgressBar frontCoverPB;

    @BindView(R.id.backCoverIV)
    ImageView backCoverIV;

    @BindView(R.id.bgBackCoverIV)
    ImageView bgbackCoverIV;

    @BindView(R.id.backCoverPB)
    ProgressBar backCoverPB;

    @BindView(R.id.gridview)
    GridView gridView;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    BookManagerActivity activity;
    Command command;
    ArrayList<EditorPic> pics;
    BookManagerGridAdapter bookManagerGridAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_book_manager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((BookManagerActivity) getActivity());
        command = activity.command;

        if(command == null){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(activity);
            }
            builder.setTitle(R.string.ERROR)
                    .setMessage(R.string.GENERAL_ERROR)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            activity.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        pics = command.getEditorPics();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(pics != null){
            bookManagerGridAdapter = new BookManagerGridAdapter(getContext(), pics, gridView);
            Log.i(LOG_TAG, "adapter pics size : " + String.valueOf(bookManagerGridAdapter.getCount()));

            gridView.setAdapter(bookManagerGridAdapter);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                    new IntentFilter(CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER));

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    return true;
                }
            });

            scrollView.fullScroll(View.FOCUS_UP);

            initAlbumCovers();

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mPageReceiver,
                    new IntentFilter("PAGE_CHANGE"));
        }


    }

    private void initAlbumCovers() {
        setFrontCoverPic();
        setBackCoverPic();
    }

    private void setFrontCoverPic() {
        if(frontCoverIV != null && command != null){
            EditorPic frontCoverPic = command.getAlbumFrontCover();

            if(frontCoverPic.getFinalBitmapPath() != null){
                Picasso.with(getContext())
                        .load(new File(frontCoverPic.getFinalBitmapPath()))
                        .skipMemoryCache()
                        .fit()
                        .into(frontCoverIV);

                if (frontCoverPic.getBackgroundReference() != null && frontCoverPic.getBackgroundReference().getBackgroundFile() != null) {
                    Picasso.with(getContext())
                            .load(frontCoverPic.getBackgroundReference().getBackgroundFile())
                            .fit()
                            .into(bgfrontCoverIV);

                }
            }
        }

    }

    private void setBackCoverPic() {
        if(backCoverIV != null && command != null){
            EditorPic backCoverPic = command.getAlbumBackCover();
            if (backCoverPic.getFinalBitmapPath() != null) {
                Picasso.with(getContext())
                        .load(new File(backCoverPic.getFinalBitmapPath()))
                        .skipMemoryCache()
                        .fit()
                        .into(backCoverIV);

                if (backCoverPic.getBackgroundReference() != null && backCoverPic.getBackgroundReference().getBackgroundFile() != null){
                    Picasso.with(getContext())
                            .load(backCoverPic.getBackgroundReference().getBackgroundFile())
                            .fit()
                            .into(bgbackCoverIV);
                }
            }
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bookManagerGridAdapter.notifyDataSetChanged();
            initAlbumCovers();
            Log.i(LOG_TAG, "BroadCastReceiver for FINISH PIC");
        }
    };

    private BroadcastReceiver mPageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bookManagerGridAdapter.update();
        }
    };

    @OnClick(R.id.frontCoverIV)
    public void onFrontCoverClick(){
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtra("FROM", BookManagerActivity.LOG_TAG);
        intent.putExtra("PAGE_POSITION", "FRONT_COVER");
        startActivity(intent);
    }

    @OnClick(R.id.backCoverIV)
    public void onBackCoverClick(){
        Intent intent = new Intent(getContext(), EditorActivity.class);
        intent.putExtra("FROM", BookManagerActivity.LOG_TAG);
        intent.putExtra("PAGE_POSITION", "BACK_COVER");
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        setFrontCoverPic();
        setBackCoverPic();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(bookManagerGridAdapter != null)
            bookManagerGridAdapter.notifyDataSetChanged();
        setFrontCoverPic();
        setBackCoverPic();
    }
}
