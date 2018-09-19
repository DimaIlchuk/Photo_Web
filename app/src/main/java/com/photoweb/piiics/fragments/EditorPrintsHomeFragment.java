package com.photoweb.piiics.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.PriceSecurityException;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.fragments.EditorFragments.BordersFragment;
import com.photoweb.piiics.fragments.EditorFragments.CropFragment;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CircularViewPagerHandler;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.CreateEditorPicsBitmapsAsync;
import com.photoweb.piiics.utils.PopUps;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thomas on 25/04/2017.
 */

public class EditorPrintsHomeFragment extends BaseFragment {
    private static final String LOG_TAG = "EditorPrintsHomeFrag";

    private EditorActivity activity;
    private ArrayList<EditorPic> pics;

    @BindView(R.id.price)
    TextView textViewPrice;

    @BindView(R.id.viewPagePics)
    public ViewPager mViewPager;

    @BindView(R.id.pics_counter)
    TextView picsCounter;

    @BindView(R.id.pics_number)
    TextView picCopyNumber;

    @BindView(R.id.linear_format)
    LinearLayout lFormat;

    @BindView(R.id.linear_border)
    LinearLayout lBorder;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_viewpager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (EditorActivity) getActivity();
        pics = ((EditorActivity) getActivity()).getPics();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(mViewPager != null)
            {
                int position = intent.getIntExtra("EditorActivityAdapterPosition", -42);
                Log.i(LOG_TAG, "EditorHomeReceiver, POSITION : " + String.valueOf(position));
                Log.i(LOG_TAG, "mViewPager POSITION CURENT ITEM : " + String.valueOf(mViewPager.getCurrentItem()));

                int min = mViewPager.getCurrentItem() - 2;
                int max = mViewPager.getCurrentItem() + 2;
                if (position >= min && position <= max) {
                    //  ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();

                    try {
                        ViewGroup container = (ViewGroup) getView().findViewWithTag(position);

                        ProgressBar progressBar = (ProgressBar) container.findViewById(R.id.progressBar);
                        ImageView mImageView = (ImageView) container.findViewById(R.id.imageView);

                        final String bitmapPath = pics.get(position).getFinalBitmapPath();
                        progressBar.setVisibility(View.GONE);
                        Picasso.with(activity)
                                .load(new File(bitmapPath))
                                .fit()
                                .centerInside()
                                .skipMemoryCache()
                                .into(mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d("LOAD", "failed to load " + bitmapPath);
                                    }
                                });
                    } catch (NullPointerException npe) {
                        Log.d(LOG_TAG, "BroadCastReceiver : NullPointerException");
                    }
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPagePics);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new CircularViewPagerHandler(mViewPager));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setToolbarDefault();

        if(activity.getCommand() == null){
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

        initViewPager();
        initPicsCounter(0);
        initPicCopyNumber();
        initTotalPrice();

        ((EditorActivity) getActivity()).setCurrentPicPosition(0);

/*        if (pics.size() > 1 && pics.get(1).getFormatReference().getName().equals(PriceReferences.STANDARD_FORMAT)) {//????
            Bitmap bg = BitmapFactory.decodeFile(pics.get(0).getBackgroundReference().getBackgroundFile().getAbsolutePath());

            Drawable d = new BitmapDrawable(getResources(), bg);
            mViewPager.setBackground(d);

            lFormat.setVisibility(GONE);
            lBorder.setVisibility(GONE);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (pics.size() > 1 && pics.get(0).getFormatReference().getName().equals(PriceReferences.PAGE_FORMAT)) {
                    Bitmap bg = BitmapFactory.decodeFile(pics.get(position).getBackgroundReference().getBackgroundFile().getAbsolutePath());
                    Log.d("EDIT", pics.get(position).getBackgroundReference().getBackgroundFile().getAbsolutePath());

                    Drawable d = new BitmapDrawable(getResources(), bg);
                    mViewPager.setBackground(d);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        ImageView previousPicButton = (ImageView) getActivity().findViewById(R.id.previous_pic_button);
        previousPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = mViewPager.getCurrentItem();
                if (itemPosition > 0) {
                    mViewPager.setCurrentItem(itemPosition - 1, true);
                }else{
                    mViewPager.setCurrentItem(pics.size() - 1, true);
                }
            }
        });

        ImageView nextPicButton = (ImageView) getActivity().findViewById(R.id.next_pic_button);
        nextPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = mViewPager.getCurrentItem();
                if (itemPosition < pics.size() - 1) {
                    mViewPager.setCurrentItem(itemPosition + 1, true);
                }else{
                    mViewPager.setCurrentItem(0, true);
                }
            }
        });

        ImageView formatButton = (ImageView) getActivity().findViewById(R.id.format_button);
        formatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CropFragment(), "FROM_EDITOR_HOME")
                        .addToBackStack(null)
                        .commit();
            }
        });


        ImageView bordersButton = (ImageView) getActivity().findViewById(R.id.borders_button);
        bordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new BordersFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView filtersButton = (ImageView) getActivity().findViewById(R.id.filters_button);
        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new FiltersFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView gabaritsButton = (ImageView) getActivity().findViewById(R.id.gabarits_button);
        gabaritsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GabaritsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView backgroundButton = (ImageView) getActivity().findViewById(R.id.background_button);
        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new BackgroundFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView decrementCopyButton = (ImageView) getActivity().findViewById(R.id.decrement_copy);
        decrementCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItemPosition = mViewPager.getCurrentItem();
                EditorPic picItem = pics.get(currentItemPosition);
                if (picItem.getCopy() == 1) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(activity);
                    }
                    builder.setTitle(R.string.PAGE_DELETE_TITLE)
                            .setMessage(R.string.PHOTO_DELETE)
                            .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    deleteItem();

                                }
                            })
                            .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    picItem.decrementCopy();
                }
                initPicCopyNumber();
                initTotalPrice();

                try {
                    CommandHandler.get().currentCommand.saveCommand();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView incrementCopyButton = (ImageView) getActivity().findViewById(R.id.increment_copy);
        incrementCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditorPic picItem = pics.get(mViewPager.getCurrentItem());
                picItem.incrementCopy();
                initPicCopyNumber();
                initTotalPrice();

                try {
                    CommandHandler.get().currentCommand.saveCommand();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        TextView duplicateButton = (TextView) getActivity().findViewById(R.id.duplicate_button);
        duplicateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        if (((EditorActivity) getActivity()).getCommand().getProduct().equals("ALBUM")) {
            formatButton.setVisibility(View.GONE);
            bordersButton.setVisibility(View.GONE);
            filtersButton.setVisibility(View.GONE);
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(CreateEditorPicsBitmapsAsync.FINISH_PIC_FILTER));

       // activity.setCurrentPicPosition(mViewPager.getCurrentItem());
    }

    public void deleteItem()
    {
        int currentItemPosition = mViewPager.getCurrentItem();

        if (pics.size() == 1) {
            CommandHandler.get().deleteCurrentCommand();
            activity.finish();
            ((EditorActivity) getActivity()).onSupportNavigateUp();
            return;
        }

        try{
            File f = new File(pics.get(currentItemPosition).getFinalBitmapPath());
            f.delete();

            f = new File(pics.get(currentItemPosition).getCropBitmapPath());
            f.delete();
        }catch (NullPointerException e){
            Log.d(LOG_TAG, "error");
        }

        pics.remove(currentItemPosition);

        try {
            CommandHandler.get().currentCommand.saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((EditorActivity) getActivity()).getEditorPicsAdapter().notifyDataSetChanged();
        mViewPager.setAdapter(((EditorActivity) getActivity()).getEditorPicsAdapter());
        //currentItemPosition = mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(currentItemPosition, true);
        initPicsCounter(currentItemPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }


    private void initTotalPrice() {
        try {
            String totalPriceStr = activity.getCommand().getAllPicsPriceStr();
            textViewPrice.setText(totalPriceStr);
        } catch (PriceSecurityException pse) {
            PopUps.popUpFatalError(activity, PriceSecurityException.getErrorTitle(), PriceSecurityException.getErrorMessage());
        }
    }

    /*
        Show the number of copy of the pic
    */
    private void initPicCopyNumber() {
        if(mViewPager.getCurrentItem() < pics.size()) {
            EditorPic picItem = pics.get(mViewPager.getCurrentItem());
            picCopyNumber.setText(String.valueOf(picItem.getCopy()));
        }
    }

    /*
    show the pics counter below the slider
    */
    private void initPicsCounter(int position) {
        String str = String.valueOf(position + 1) + "/" + String.valueOf(pics.size());
        picsCounter.setText(str);
    }

    private void initViewPager() {
        mViewPager.setAdapter(((EditorActivity) getActivity()).getEditorPicsAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                initPicsCounter(position);
                initPicCopyNumber();
                ((EditorActivity) getActivity()).setCurrentPicPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private EditorPic getCurrentPic() {
        int currentItemPosition = mViewPager.getCurrentItem();
        EditorPic picItem = pics.get(currentItemPosition);
        return picItem;
    }

    @OnClick(R.id.stickers_layout)
    public void onStickersLayoutClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new StickersFragment())
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.text_button)
    public void onTextButtonClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TextFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(activity.getEditorPicsAdapter() != null)
            activity.getEditorPicsAdapter().notifyDataSetChanged();
    }

    @OnClick(R.id.viewPagePics)
    public void onViewPagePicsClick() {
        Toast.makeText(getActivity(), "On CLICK", Toast.LENGTH_LONG).show();
    }
}
