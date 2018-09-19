package com.photoweb.piiics.utils;

import android.content.ClipData;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by dnizard on 04/02/2018.
 */

public class MyDragListener implements View.OnDragListener {

    int imagePosition;

    public MyDragListener(int position) {
        imagePosition = position;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.i("DRAG Started", "" + imagePosition);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i("DRAG Entered", "" + imagePosition);

                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.i("DRAG Exited", "" + imagePosition);
                break;
            case DragEvent.ACTION_DROP:
                Log.i("DRAG Dropped", "" + imagePosition);

                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);

                // Displays a message containing the dragged data.
                Log.d("Result", "Dragged data is " + item.getText());

                CommandHandler.get().currentCommand.switchPages(Integer.parseInt(item.getText().toString()), imagePosition);
                Intent intent = new Intent("PAGE_CHANGE");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.i("DRAG Ended", "" + imagePosition);
            default:
                break;
        }
        return true;
    }

}