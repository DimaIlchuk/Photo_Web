package com.photoweb.piiics.fragments.BurgerMenuFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.photoweb.piiics.Adapters.CreationAdapter;
import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.MainActivity;
import com.photoweb.piiics.fragments.BaseFragment;

import butterknife.BindView;

/**
 * Created by thomas on 06/09/2017.
 */

public class MyCreationsFragment extends BaseFragment {

    CreationAdapter creationAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView commandsRV;

    MainActivity activity;

    TextView tvEdit;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_creations;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((MainActivity) getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbar();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        commandsRV.setLayoutManager(layoutManager);
        creationAdapter = new CreationAdapter(((MainActivity) getActivity()));
        commandsRV.setAdapter(creationAdapter);
    }

    private void setToolbar() {
        Toolbar toolbar = activity.getToolbar();

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.CREATION);

        tvEdit = (TextView) toolbar.findViewById(R.id.edit_icon);

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creationAdapter.setStateDelete();
                creationAdapter.notifyDataSetChanged();

                if(creationAdapter.getStateDelete()){
                    tvEdit.setText(R.string.BACK);
                }else{
                    tvEdit.setText(R.string.EDIT);
                }
            }
        });

    }
}
