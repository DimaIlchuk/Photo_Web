package com.photoweb.piiics.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.Adapters.AlbumEditorPicsAdapter;
import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync;
import com.photoweb.piiics.utils.PopUps;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 25/04/2017.
 */

public class EditorAlbumHomeFragment extends BaseFragment {
    private static final String LOG_TAG = "EditorAlbumHomeFrag";

    private EditorActivity activity;
    private ArrayList<EditorPic> pics;
    private String position;
    private Command command;

    private AlbumEditorPicsAdapter albumEditorPicsAdapter;

    @BindView(R.id.price)
    TextView textViewPrice;

    @BindView(R.id.viewPagePics)
    public ViewPager mViewPager;

    @BindView(R.id.pics_counter)
    TextView picsCounter;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_editor_album_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (EditorActivity) getActivity();
        pics = ((EditorActivity) getActivity()).getPics();
        position = activity.getIntent().getStringExtra("PAGE_POSITION");
        command = CommandHandler.get().currentCommand;
    }

    /*private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("Votre photo est en cours de traitement, merci de patienter quelques instants");
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    };*/

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "receive");
            activity.refreshPics();

            albumEditorPicsAdapter.setPics(activity.getPics());
            albumEditorPicsAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("EditorActivityAdapterPosition", -42);
            Log.i(LOG_TAG, "EditorHomeReceiver, POSITION : " + String.valueOf(position));
            Log.i(LOG_TAG, "mViewPager POSITION CURENT ITEM : " + String.valueOf(mViewPager.getCurrentItem()));

            int min = mViewPager.getCurrentItem() - 2;
            int max = mViewPager.getCurrentItem() + 2;
            if (position >= min && position <= max) {
                //  ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();

                try {
                    ViewGroup container = getView().findViewWithTag(position);
                    String bitmapPath;

                    if (command.getProduct().equals("ALBUM")) {
                        int frontCover = 1;
                        int backCover = 1;

                        if (position == pics.size() + frontCover + backCover - 1) {
                            bitmapPath = command.getAlbumBackCover().getFinalBitmapPath();
                        } else {
                            bitmapPath = pics.get(position).getFinalBitmapPath();
                        }
                    } else {
                        bitmapPath = pics.get(position).getFinalBitmapPath();
                    }

                    ProgressBar progressBar = container.findViewById(R.id.progressBar);
                    ImageView mImageView = container.findViewById(R.id.imageView);

                    progressBar.setVisibility(View.GONE);
                    Picasso.with(activity)
                            .load(new File(bitmapPath))
                            .fit()
                            .centerInside()
                            .skipMemoryCache()
                            .into(mImageView);
                } catch (NullPointerException npe) {
                    Log.d(LOG_TAG, "BroadCastReceiver : NullPointerException");
                }
            }

            if(activity.dialog != null && activity.dialog.isShowing())
                activity.dialog.dismiss();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mViewPager.setOffscreenPageLimit(1);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setToolbarDefault();

        initViewPager();
        initTotalPrice();

        //((EditorActivity) getActivity()).setCurrentPicPosition(Integer.parseInt(position));

/*        if (pics.size() > 1 && pics.get(1).getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)) {//????
            Bitmap bg = BitmapFactory.decodeFile(pics.get(0).getBackgroundReference().getBackgroundFile().getAbsolutePath());

            Drawable d = new BitmapDrawable(getResources(), bg);
            mViewPager.setBackground(d);

            lFormat.setVisibility(GONE);
            lBorder.setVisibility(GONE);
        }*/

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateReceiver,
                new IntentFilter("SHOULD_UPDATE"));

       // activity.setCurrentPicPosition(mViewPager.getCurrentItem());
    }

    private void initViewPager() {
        albumEditorPicsAdapter = new AlbumEditorPicsAdapter(activity);
        mViewPager.setAdapter(albumEditorPicsAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                initPicsCounter(position);
                ((EditorActivity) getActivity()).setCurrentPicPosition(position);
                /*
                               if (pics.size() > 1 && pics.get(0).getFormatReference().getName().equals(PriceReferences.PAGE_FORMAT)) {
                    Bitmap bg = BitmapFactory.decodeFile(pics.get(position).getBackgroundReference().getBackgroundFile().getAbsolutePath());
                    Log.d("EDIT", pics.get(position).getBackgroundReference().getBackgroundFile().getAbsolutePath());

                    Drawable d = new BitmapDrawable(getResources(), bg);
                    mViewPager.setBackground(d);
                }
                 */
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (position == null) {
            mViewPager.setCurrentItem(AlbumEditorPicsAdapter.frontCoverSize);
            initPicsCounter(0);
            ((EditorActivity) getActivity()).setCurrentPicPosition(0);
        } else if (position.equals("FRONT_COVER")) {
            mViewPager.setCurrentItem(0);
            initPicsCounter(0);
        } else if (position.equals("BACK_COVER")) {
            int backCoverPosition = pics.size() + AlbumEditorPicsAdapter.frontCoverSize + AlbumEditorPicsAdapter.backCoverSize - 1;
            mViewPager.setCurrentItem(backCoverPosition);
            initPicsCounter(pics.size() - 1);
        } else {
            mViewPager.setCurrentItem(Integer.valueOf(position));
            initPicsCounter(Integer.valueOf(position));
        }
    }

    /*
        show the pics counter below the slider
    */
    private void initPicsCounter(int viewPagerPosition) {
        String str;
        if (viewPagerPosition == 0) {
            str = getString(R.string.COVER);
        } else if (viewPagerPosition > pics.size()-2) {
            str = getString(R.string.BACK_COVER);
        } else {
            String currentPositionName = String.valueOf(viewPagerPosition + 1 - AlbumEditorPicsAdapter.frontCoverSize);
            str = currentPositionName + "/" + String.valueOf(pics.size()-2);
        }
        picsCounter.setText(str);
    }

   /* private void initPicsCounter(String position) {
        String positionNumber;
        if (position == null) {
            positionNumber = "1";
        } else if (position.equals("FRONT_COVER")) {
            mViewPager.setCurrentItem(0);
        } else if (position.equals("BACK_COVER")) {
            int backCoverPosition = pics.size() + AlbumEditorPicsAdapter.frontCoverSize + AlbumEditorPicsAdapter.backCoverSize - 1;
            mViewPager.setCurrentItem(backCoverPosition);
        } else {
            mViewPager.setCurrentItem(Integer.valueOf(position) + AlbumEditorPicsAdapter.frontCoverSize);
        }

        String str = String.valueOf(position + 1) + "/" + String.valueOf(pics.size() + AlbumEditorPicsAdapter.frontCoverSize + AlbumEditorPicsAdapter.backCoverSize);
        picsCounter.setText(str);
    }*/

    private void initTotalPrice() {
        try {
            String totalPriceStr = activity.getCommand().getAllPicsPriceStr();
            textViewPrice.setText(totalPriceStr);
        } catch (PriceSecurityException pse) {
            PopUps.popUpFatalError(activity, PriceSecurityException.getErrorTitle(), PriceSecurityException.getErrorMessage());
        } catch (NullPointerException e) {
            PopUps.popUpFatalError(activity, "Error", e.getLocalizedMessage());
        }
    }

    @OnClick(R.id.previous_pic_button)
    public void onPreviousPicClick() {
        int itemPosition = mViewPager.getCurrentItem();
        if (itemPosition > 0) {
            mViewPager.setCurrentItem(itemPosition - 1, true);
        }
    }

    @OnClick(R.id.next_pic_button)
    public void onNextPicClick() {
        int itemPosition = mViewPager.getCurrentItem();
        if (itemPosition < pics.size() - 1) {
            mViewPager.setCurrentItem(itemPosition + 1, true);
        }
    }


    @OnClick(R.id.duplicate_button)
    public void onDuplicateClick() {
        int currentItemPosition = mViewPager.getCurrentItem();
        EditorPic picItem = getCurrentPic();
        EditorPic clonePicItem = null;
        Log.i(LOG_TAG, "ACTIONS DUPLCIATE BUTTON  :");
        picItem.showActions();
        clonePicItem = picItem.duplicateEditorPic();

        if (clonePicItem != null) {
            pics.add(currentItemPosition + 1, clonePicItem);
            ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();
            initPicsCounter(currentItemPosition);
            initTotalPrice();

            //activity.checkOnGoingThread();
        }

        try {
            CommandHandler.get().currentCommand.saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.backgroundLL)
    public void onBackgroundClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new BackgroundFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.stickersLL)
    public void onStickersClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new StickersFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.textLL)
    public void onTextClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TextFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.gabaritsLL)
    public void onGabaritsClick() {
        if(activity.getCurrentPic().getPhotoID().equals("LAST")){
            Toast.makeText(getContext(),"Le dos de couverture n'accepte pas les gabarits.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(activity.getCurrentPic().getPhotoID().equals("FIRST") && activity.getCurrentPic().getAsset() == null){
            Toast.makeText(getContext(),"Merci de sélectionne d'abord une photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new GabaritsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private EditorPic getCurrentPic() {
        int currentItemPosition = mViewPager.getCurrentItem();
        EditorPic picItem = pics.get(currentItemPosition);
        return picItem;
    }

    @Override
    public void onResume() {
        super.onResume();
        albumEditorPicsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.viewPagePics)
    public void onViewPagePicsClick() {
        Toast.makeText(getActivity(), "On CLICK", Toast.LENGTH_LONG).show();
    }
}
