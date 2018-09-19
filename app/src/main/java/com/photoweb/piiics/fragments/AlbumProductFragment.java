package com.photoweb.piiics.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.ProductPageActivity;
import com.photoweb.piiics.activities.SelectPicsActivity;
import com.photoweb.piiics.utils.CommandHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dnizard on 26/11/2017.
 */

public class AlbumProductFragment extends BaseFragment  {

    @BindView(R.id.describe_print)
    TextView describe;

    ProductPageActivity activity;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_albumproduct;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((ProductPageActivity) getActivity());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        describe.setText(Html.fromHtml(getString(R.string.ALBUM_PAGE_DESCRIBE)));

        setToolbar();

    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();
        ImageView img = (ImageView) toolbar.findViewById(R.id.burger_menu_icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.back, getContext().getTheme()));
        } else {
            img.setImageDrawable(getResources().getDrawable(R.drawable.back));
        }
    }

    @OnClick(R.id.validate)
    public void onValidateClick()
    {
        CommandHandler.get().currentCommand = null;

        Intent intent = new Intent(getActivity(), SelectPicsActivity.class);
        intent.putExtra("PRODUCT", "ALBUM");
        intent.putExtra("launchedBy","MainActivity");

        getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}