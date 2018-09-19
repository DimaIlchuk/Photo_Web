package com.photoweb.piiics.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photoweb.piiics.R;
import com.photoweb.piiics.activities.BookManagerActivity;
import com.photoweb.piiics.activities.EditorActivity;
import com.photoweb.piiics.model.Command;
import com.photoweb.piiics.model.EditorPic;
import com.photoweb.piiics.utils.CommandHandler;
import com.photoweb.piiics.utils.MyDragListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.view.View.GONE;

/**
 * Created by thomas on 18/04/2017.
 */

public class BookManagerGridAdapter extends BaseAdapter {
    private static final String LOG_TAG = "BookManagerGridAdapter";

    private int LAYOUT_PIC = 1;
    private int LAYOUT_ADD_BUTTON = 2;

    GridView gridView;
    ArrayList<EditorPic> pics;

    LayoutInflater inflater;
    Context context;

    int flyleafBeginning = 1;
    int addButton = 1;


    public BookManagerGridAdapter(Context context, ArrayList<EditorPic> pics, GridView gridView) {
        this.pics = pics;
        Collections.sort(pics, new Comparator<EditorPic>() {
            @Override
            public int compare(EditorPic t0, EditorPic t1) {
                return t0.index - t1.index;
            }
        });
        this.context = context;
        this.gridView = gridView;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pics.size() + flyleafBeginning + addButton;
    }

    @Override
    public EditorPic getItem(int position) {
        if (position == 0) {
            return null;
        } else if (position == (pics.size() + flyleafBeginning + addButton - 1)) {
            return null;
        } else {
            return pics.get(position - flyleafBeginning);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "POSITION : " + String.valueOf(position));
        Log.i(LOG_TAG, "CONDITION : " + String.valueOf((pics.size() + flyleafBeginning + addButton - 1)));

        if (position == (pics.size() + flyleafBeginning + addButton - 1)) {
            return createAddButtonView(convertView);
        } else {
            return createAlbumPicView(position, convertView);
        }
    }

    private View createAddButtonView(View convertView) {
        if (convertView == null || (convertView.getTag() != null && convertView.getTag().equals(LAYOUT_PIC))) {
            convertView = inflater.inflate(R.layout.item_book_manager_add_button, null);
        }
        convertView.setTag(LAYOUT_ADD_BUTTON);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.addButtonIV);
        Picasso.with(context)
                .load(R.drawable.ajout_photos_livre)
                .resize(500, 500)
                .centerInside()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(context, "Error picasso for CREATE ADD BUTTON", Toast.LENGTH_SHORT).show();
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Add Pic", Toast.LENGTH_SHORT).show();

                //Command command = CommandHandler.get().currentCommand;
                //ArrayList<EditorPic> commandPics = command.getEditorPics();
                CommandHandler.get().currentCommand.getEditorPics().add(new EditorPic(null, "ALBUM", pics.size()));
                updateModel(CommandHandler.get().currentCommand.getEditorPics());
            }
        });

        return convertView;
    }

    private void updateModel(ArrayList<EditorPic> commandPics) {
        this.pics = commandPics;

        try {
            CommandHandler.get().currentCommand.saveCommand();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    private View createAlbumPicView(int position, View convertView) {

        if (convertView == null || (convertView.getTag() != null && convertView.getTag().equals(LAYOUT_ADD_BUTTON))) {
            convertView = inflater.inflate(R.layout.item_book_manager_grid, null);
        }
        convertView.setTag(LAYOUT_PIC);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        //ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
        TextView pageNumberTV = convertView.findViewById(R.id.pageNumber);
        ImageView deleteIV = convertView.findViewById(R.id.delete);
        View view = convertView.findViewById(R.id.firstpage);

        if (position == 0) {
            //progressBar.setVisibility(GONE);
            imageView.setVisibility(GONE);
            view.setVisibility(View.VISIBLE);
            pageNumberTV.setText(R.string.GUARD);
            deleteIV.setVisibility(GONE);
        } else {

            EditorPic editorPic = pics.get(position - flyleafBeginning);
            /*if (editorPic.getAsset() == null) {
                progressBar.setVisibility(GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(android.R.color.transparent);
            } else {*/
                final String bitmapPath = editorPic.getFinalBitmapPath();
                if (bitmapPath == null) {
                    //progressBar.setVisibility(View.VISIBLE);
                    imageView.setVisibility(GONE);
                    view.setVisibility(View.VISIBLE);
                } else {
                    //progressBar.setVisibility(GONE);
                    imageView.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    Picasso.with(context)
                            .load(new File(bitmapPath))
                            .fit()
                            .centerInside()
                            .skipMemoryCache()
                            //.centerCrop()
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(context, "Error picasso - path file : " + bitmapPath, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            //}

            String pageNumber = context.getString(R.string.PAGE_NB) + " " + String.valueOf(position);
            pageNumberTV.setText(pageNumber);
            deleteIV.setVisibility(View.VISIBLE);

            setDeleteClickListener(deleteIV, position);
            setImageClickListener(imageView, position);
            setImageLongClickListener(imageView, position);
            imageView.setOnDragListener(new MyDragListener(position));
        }

        return convertView;
    }

    public void update()
    {
        Collections.sort(pics, new Comparator<EditorPic>() {
            @Override
            public int compare(EditorPic t0, EditorPic t1) {
                return t1.index - t0.index;
            }
        });
        notifyDataSetChanged();
    }

    private void setDeleteClickListener(ImageView deleteIV, final int adapterPosition) {
        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "DELETE", Toast.LENGTH_SHORT).show();

                if(pics.size() < 20){
                    Toast.makeText(context, context.getString(R.string.FORBIDDEN_DELETE_TITLE) + " : " + context.getString(R.string.FORBIDDEN_DELETE_BODY), Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }

                builder.setTitle(R.string.PAGE_DELETE_TITLE)
                        .setMessage(R.string.PAGE_DELETE_BODY)
                        .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pics.remove(adapterPosition - flyleafBeginning);
                                notifyDataSetChanged();
                                gridView.invalidateViews();
                            }
                        })
                        .setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });
    }

    private void setImageClickListener(ImageView imageView, final int adapterPosition) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "onImageClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, EditorActivity.class);
                intent.putExtra("FROM", BookManagerActivity.LOG_TAG);
                intent.putExtra("PAGE_POSITION", String.valueOf(adapterPosition));
                context.startActivity(intent);
            }
        });
    }

    private void setImageLongClickListener(ImageView imageView, final int adapterPosition) {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(LOG_TAG, "Hello");
                ClipData data = ClipData.newPlainText("position", "" + adapterPosition);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                } else {
                    view.startDrag(data, shadowBuilder, view, 0);
                }

                view.setVisibility(View.INVISIBLE);

                return true;
            }
        });
    }
}