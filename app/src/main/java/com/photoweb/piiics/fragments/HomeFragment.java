package com.photoweb.piiics.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ad4screen.sdk.A4S;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BookManagerActivity;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.activities.ProductPageActivity;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.DraftsUtils;
import com.photoweb.piiics.utils.UserInfo;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thomas on 14/04/2017.
 */

public class HomeFragment extends BaseFragment {

    //private Toolbar toolbar;

    MainActivity activity;

    @BindView(R.id.tv_free_book)
    TextView freeBook;

    @BindView(R.id.tv_free_print)
    TextView freePrint;

    @BindView(R.id.bottomButton)
    TextView bottomButton;

    @BindView(R.id.relative_cover)
    RelativeLayout cover;

    @BindView(R.id.text_popup)
    TextView textPopup;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity) getActivity());

        A4S.get(activity).setView("home");

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setToolbar();
        Log.d("HOME", "ID : " + UserInfo.getInt("id"));

        if(UserInfo.getInt("id") == 0){
            freeBook.setText(getString(R.string.ONE_LEFT, 1, ""));
            //freeBook.setText("Bientôt disponible");

            freePrint.setText(getString(R.string.LEFT, 50, ""));
        }else{
            freeBook.setText(getString(R.string.ONE_LEFT, UserInfo.getInt("book_available"), ""));
            //freeBook.setText("Bientôt disponible");

            int free = UserInfo.getInt("print_available") + UserInfo.getInt("print_bonus");

            freePrint.setText(free > 1 ? getString(R.string.LEFT, free, "") : getString(R.string.ONE_LEFT, free, ""));
        }

        bottomButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/FredokaOne-Regular.ttf"));

        textPopup.setText(Html.fromHtml(getString(R.string.HOME_POPUP)));
    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Piiics");
    }

    @OnClick(R.id.photo_prints_background_pic)
    public void onPhotoPrintButtonClick() {

        if(DraftsUtils.getPrintDirectoryPath() != null){
            long lastModified = 0;
            File lastFile = new File(DraftsUtils.getPrintDirectoryPath());

            File f = new File(DraftsUtils.getPrintDirectoryPath());
            File[] files = f.listFiles();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    // is directory
                    Log.d("HOME", inFile.getName() + " : " + inFile.lastModified());
                    if(inFile.lastModified() > lastModified){

                        File f2 = new File(inFile.getAbsolutePath());
                        File[] files2 = f2.listFiles();
                        for (File inFile2 : files2) {
                            if(inFile2.getName().endsWith(".json")){
                                lastFile = inFile;
                                lastModified = inFile.lastModified();
                            }
                        }

                    }
                }
            }

            final File selectedFile = lastFile;

            if(lastModified > 0){
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle(R.string.CREATION)
                        .setMessage(R.string.LAST_CREATION)
                        .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                CommandHandler.get().init(selectedFile.getName(), "PRINT");
                                try {
                                    CommandHandler.get().currentCommand.loadCommand(getContext());

                                    Intent intent = new Intent(getActivity(), EditorActivity.class);
                                    intent.putExtra("launchedBy","MainActivity");
                                    startActivity(intent);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.NEW_CREATION, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                                intent.putExtra("launchedBy","MainActivity");
                                intent.putExtra("PRODUCT", "PRINT");

                                getActivity().startActivity(intent);
                            }
                        })
                        .setNeutralButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                intent.putExtra("launchedBy","MainActivity");
                intent.putExtra("PRODUCT", "PRINT");

                getActivity().startActivity(intent);
            }
        }else{
            Intent intent = new Intent(getActivity(), ProductPageActivity.class);
            intent.putExtra("launchedBy","MainActivity");
            intent.putExtra("PRODUCT", "PRINT");

            getActivity().startActivity(intent);

        }


    }

    @OnClick(R.id.photo_book_background_pic)
    public void onPhotoBookButtonClick() {
        if(DraftsUtils.getAlbumDirectoryPath() != null){
            long lastModified = 0;
            File lastFile = new File(DraftsUtils.getAlbumDirectoryPath());

            File f = new File(DraftsUtils.getAlbumDirectoryPath());
            File[] files = f.listFiles();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    // is directory
                    Log.d("HOME", inFile.getName() + " : " + inFile.lastModified());
                    if(inFile.lastModified() > lastModified){

                        File f2 = new File(inFile.getAbsolutePath());
                        File[] files2 = f2.listFiles();
                        for (File inFile2 : files2) {
                            if(inFile2.getName().endsWith(".json")){
                                lastFile = inFile;
                                lastModified = inFile.lastModified();
                            }
                        }

                    }
                }
            }

            final File selectedFile = lastFile;

            if(lastModified > 0){
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle(R.string.CREATION)
                        .setMessage(R.string.LAST_CREATION)
                        .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                CommandHandler.get().init(selectedFile.getName(), "ALBUM");
                                try {
                                    CommandHandler.get().currentCommand.loadCommand(getContext());

                                    Intent intent = new Intent(getActivity(), BookManagerActivity.class);
                                    intent.putExtra("launchedBy","MainActivity");
                                    startActivity(intent);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.NEW_CREATION, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                                intent.putExtra("launchedBy","MainActivity");
                                intent.putExtra("PRODUCT", "ALBUM");

                                getActivity().startActivity(intent);
                            }
                        })
                        .setNeutralButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                Intent intent = new Intent(getActivity(), ProductPageActivity.class);
                intent.putExtra("launchedBy","MainActivity");
                intent.putExtra("PRODUCT", "ALBUM");

                getActivity().startActivity(intent);
            }
        }else{
            Intent intent = new Intent(getActivity(), ProductPageActivity.class);
            intent.putExtra("launchedBy","MainActivity");
            intent.putExtra("PRODUCT", "ALBUM");

            getActivity().startActivity(intent);

        }
    }

    @OnClick(R.id.bottomButton)
    public void onBottomClick() {
        cover.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.validate)
    public void onValidateClick() {
        cover.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

      /*  TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Piiics");

        ImageView toolbarInfoImage = (ImageView) toolbar.findViewById(R.id.burger_menu_icon);
        toolbarInfoImage.setVisibility(View.VISIBLE);*/

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
