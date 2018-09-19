package com.photoweb.piiics.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dnizard on 02/07/2017.
 */

public class TransformationHandler {

    private static final String LOG_TAG = "TransformationHandler";
    private static TransformationHandler instance;

    public static TransformationHandler get() {
        if (instance == null) instance = new TransformationHandler();
        return instance;
    }

    public static Bitmap generateThumbnail(Bitmap bMap, int size) {

        int width;
        int height;

        if(bMap == null)
            return bMap;

        if(bMap.getWidth() < bMap.getHeight())
        {
            width = size;
            height = size * bMap.getHeight() / bMap.getWidth();

            /*if(height < size){
                height = size;
                width = size * bMap.getWidth() / bMap.getHeight();
            }*/
        }else{
            height = size;
            width = size * bMap.getWidth() / bMap.getHeight();

            /*if(width < size){
                width = size;
                height = size * bMap.getHeight() / bMap.getWidth();
            }*/
        }

        Log.d(LOG_TAG, "origin size : " + bMap.getWidth() + " - " + bMap.getHeight());
        Log.d(LOG_TAG, "final size : " + width + " - " + height);

        return Bitmap.createScaledBitmap(bMap, width, height, true);

    }

    public static Bitmap initCrop(String url, String product){

        if(product.equals("PRINT")){
           /* Bitmap bMap = BitmapFactory.decodeFile(url);

            if(bMap.getHeight() == bMap.getWidth()){
                return squarePrint(url);
            }*/

            return standardPrint(url);
        }else{
            return standardAlbum(url);
        }

    }

    public static Bitmap standardAlbum(String url){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeFile(url, options);

        options.inSampleSize = calculateInSampleSize(options, 2500, 2500);
        options.inJustDecodeBounds = false;
        Bitmap bMap = BitmapFactory.decodeFile(url, options);

        Log.d(LOG_TAG, "bMap size : " + bMap.getWidth() + " - " + bMap.getHeight());

        int width;
        int height;

        if(bMap.getWidth() < bMap.getHeight())
        {
            height = (int)Math.round(Utils.pageHeight*0.9);
            width = Math.round(bMap.getWidth() * height / bMap.getHeight());

            if(width > Utils.pageWidth){
                width = (int)Math.round(Utils.pageWidth*0.9);
                height = Math.round(bMap.getHeight() * width / bMap.getWidth());
            }
        }else if(bMap.getWidth() > bMap.getHeight()){
            width = (int)Math.round(Utils.pageWidth*0.9);
            height = Math.round(bMap.getHeight() * width / bMap.getWidth());

            if(height > Utils.pageHeight){
                height = (int)Math.round(Utils.pageHeight*0.9);
                width = Math.round(bMap.getWidth() * height / bMap.getHeight());
            }
        }else{
            width = (int)Math.round(Utils.pageHeight*0.9);
            height = (int)Math.round(Utils.pageHeight*0.9);
        }

        Log.d(LOG_TAG, "new size : " + width + " - " + height);

        return Bitmap.createScaledBitmap(bMap, width, height, true);

    }

    public static Bitmap drawOnPage(Bitmap bMap, String style)
    {
        int margin = 5;

        if(bMap.getWidth() > 1000){
            margin = 80;
        }

        float radius = 100;

        //BitmapFactory.Options opts = new BitmapFactory.Options();
        //opts.inJustDecodeBounds = false;
        //opts.inPreferredConfig = Bitmap.Config.RGB_565;
        //opts.inDither = true;

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        switch (style){
            case "LeftThirdHalfAlbumGabarit":
            case "MarginBottomAlbumGabarit":
                canvas.drawBitmap(bMap, 0, 0, null);
                break;
            case "RotateAlbumGabarit":
                Matrix matrix = new Matrix();
                matrix.setTranslate((Utils.pageWidth - bMap.getWidth()) / 2, (Utils.pageHeight - bMap.getHeight()) / 2);
                matrix.postRotate(-10, bMap.getWidth() / 2, bMap.getHeight() / 2);

                canvas.drawBitmap(bMap, matrix, new Paint());
                break;
            /*case "RoundedRectAlbumGabarit":
                canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);

                final int color = Color.RED;
                final Paint paint = new Paint();
                final Rect rect = new Rect(margin, margin, bMap.getWidth(), bMap.getHeight());
                final RectF rectF = new RectF(rect);

                paint.setAntiAlias(true);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, radius, radius, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bMap, rect, rect, paint);

                break;*/
            default:
                canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);
                break;
        }

        return result;
    }

    public static Bitmap drawOnPage(Bitmap bMap, String style, Bitmap bg)
    {
        int margin = 5;

        if(bMap.getWidth() > 1000){
            margin = 80;
        }

        float radius = 100;

        //BitmapFactory.Options opts = new BitmapFactory.Options();
        //opts.inJustDecodeBounds = false;
        //opts.inPreferredConfig = Bitmap.Config.RGB_565;
        //opts.inDither = true;

        Bitmap result = Bitmap.createScaledBitmap(bg, Utils.pageWidth, Utils.pageHeight, false);
        Canvas canvas = new Canvas(result);

        switch (style){
            case "LeftThirdHalfAlbumGabarit":
            case "MarginBottomAlbumGabarit":
                canvas.drawBitmap(bMap, 0, 0, null);
                break;
            case "RotateAlbumGabarit":
                Matrix matrix = new Matrix();
                matrix.setTranslate((Utils.pageWidth - bMap.getWidth()) / 2, (Utils.pageHeight - bMap.getHeight()) / 2);
                matrix.postRotate(-10, bMap.getWidth() / 2, bMap.getHeight() / 2);

                canvas.drawBitmap(bMap, matrix, new Paint());
                break;
            /*case "RoundedRectAlbumGabarit":
                canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);

                final int color = Color.RED;
                final Paint paint = new Paint();
                final Rect rect = new Rect(margin, margin, bMap.getWidth(), bMap.getHeight());
                final RectF rectF = new RectF(rect);

                paint.setAntiAlias(true);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, radius, radius, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bMap, rect, rect, paint);

                break;*/
            default:
                canvas.drawBitmap(bMap, (result.getWidth() - bMap.getWidth())/2, (result.getHeight() - bMap.getHeight())/2, null);
                break;
        }

        return result;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap standardPrint(String url){
        //Bitmap bMap = BitmapFactory.decodeFile(url);

        //int imageHeight = options.outHeight;
        //int imageWidth = options.outWidth;

        return standardPrintBitmap(url);
    }

    public static Bitmap standardPrintBitmap(Bitmap bmp) {
        return createStandardBitmap(bmp, bmp.getWidth(), bmp.getHeight());
    }

    public static Bitmap standardPrintBitmap(String url){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeFile(url, options);

        options.inSampleSize = calculateInSampleSize(options, 2000, 2000);
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(url, options);

        return createStandardBitmap(bmp, options.outWidth, options.outHeight);
    }

    private static Bitmap createStandardBitmap(Bitmap bmp, int bMapWidth, int bMapHeight) {
        int width;
        int height;

        if(bMapWidth < bMapHeight)
        {
            width = Utils.stdSmall;
            height = Utils.stdSmall * bMapHeight / bMapWidth;

            if(height < Utils.stdBig){
                height = Utils.stdBig;
                width = Utils.stdBig * bMapWidth / bMapHeight;
            }
        }else{
            height = Utils.stdSmall;
            width = Utils.stdSmall * bMapWidth / bMapHeight;

            if(width < Utils.stdBig){
                width = Utils.stdBig;
                height = Utils.stdBig * bMapHeight / bMapWidth;
            }
        }

        Bitmap resized = Bitmap.createScaledBitmap(bmp, width, height, true);
        bmp.recycle();

        if(bMapWidth > bMapHeight)
        {
            width = Utils.stdBig;
            height = Utils.stdSmall;
        }else{
            width = Utils.stdSmall;
            height = Utils.stdBig;
        }

        Log.i(LOG_TAG, "width : " + String.valueOf(width) + ", height : " + String.valueOf(height));
        Bitmap croppedBitmap = Bitmap.createBitmap(
                resized,
                (resized.getWidth() - width)/2,
                (resized.getHeight() - height)/2,
                width,
                height
        );
        resized.recycle();
        return croppedBitmap;
    }

    public static Bitmap squarePrint(String url){
        Bitmap bMap = BitmapFactory.decodeFile(url);

        return squarePrintBitmap(bMap);
    }

    public static Bitmap squarePrintBitmap(Bitmap bMap){
        int width;
        int height;

        if(bMap.getWidth() < bMap.getHeight())
        {
            width = Utils.stdSmall;
            height = Utils.stdSmall * bMap.getHeight() / bMap.getWidth();

        }else if(bMap.getWidth() > bMap.getHeight()){
            height = Utils.stdSmall;
            width = Utils.stdSmall * bMap.getWidth() / bMap.getHeight();
        }else{
            width = Utils.stdSmall;
            height = Utils.stdSmall;
        }

        Bitmap resized = Bitmap.createScaledBitmap(bMap, width, height, true);

        width = Utils.stdSmall;
        height = Utils.stdSmall;

        return Bitmap.createBitmap(
                resized,
                (resized.getWidth() - width)/2,
                (resized.getHeight() - height)/2,
                width,
                height
        );
    }

    public static Bitmap panoPrintBitmap(Bitmap bMap){
        int width;
        int height;

        if(bMap.getWidth() < bMap.getHeight())
        {
            width = Utils.stdSmall;
            height = Utils.stdSmall * bMap.getHeight() / bMap.getWidth();

            if(height < Utils.panoBig){
                height = Utils.panoBig;
                width = Utils.panoBig * bMap.getWidth() / bMap.getHeight();
            }
        }else{
            height = Utils.stdSmall;
            width = Utils.stdSmall * bMap.getWidth() / bMap.getHeight();

            if(width < Utils.panoBig){
                width = Utils.panoBig;
                height = Utils.panoBig * bMap.getHeight() / bMap.getWidth();
            }
        }

        Bitmap resized = Bitmap.createScaledBitmap(bMap, width, height, true);

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = Utils.panoBig;
            height = Utils.stdSmall;
        }else{
            width = Utils.stdSmall;
            height = Utils.panoBig;
        }

        Log.i(LOG_TAG, "width : " + String.valueOf(width) + ", height : " + String.valueOf(height));
        Bitmap croppedBitmap = Bitmap.createBitmap(
                resized,
                (resized.getWidth() - width)/2,
                (resized.getHeight() - height)/2,
                width,
                height
        );
        resized.recycle();///
        return croppedBitmap;
    }

    public static Bitmap addBorder(Bitmap bMap, int color, boolean hasBorder)
    {
        Bitmap newbMap;
        int borderSize = 80;

        if(hasBorder)
        {
            newbMap = Bitmap.createBitmap(bMap, borderSize, borderSize, bMap.getWidth()-2*borderSize, bMap.getHeight()-2*borderSize);
            Log.d("Transfo", "Here");
        }else{
            newbMap = Bitmap.createScaledBitmap(bMap, bMap.getWidth()-2*borderSize, bMap.getHeight()-2*borderSize, true);
            Log.d("Transfo", "There");
        }

        Canvas canvas = new Canvas(bMap);
        canvas.drawColor(color);
        canvas.drawBitmap(newbMap, borderSize, borderSize, null);

        newbMap.recycle();

        return bMap;
    }

    public static Bitmap removeBorder(Bitmap bMap)
    {
        int borderSize = 80;

        Bitmap newbMap = Bitmap.createBitmap(bMap, borderSize, borderSize, bMap.getWidth()-2*borderSize, bMap.getHeight()-2*borderSize);

        return Bitmap.createScaledBitmap(newbMap, bMap.getWidth(), bMap.getHeight(), true);
    }

    public static Bitmap applyGabarit(String filter, Bitmap org, Bitmap bg){
        try {
            Method m = TransformationHandler.class.getMethod(filter, Bitmap.class, Bitmap.class);

            return (Bitmap) m.invoke(TransformationHandler.get(), org, bg);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap lastPage(Boolean show, Bitmap logo)
    {
        Bitmap resize = logo.createScaledBitmap(logo, 317, 209, true);

        Bitmap result = Bitmap.createBitmap(Utils.pageWidth, Utils.pageHeight, logo.getConfig());
        Canvas canvas = new Canvas(result);

        if(show)
        {
            canvas.drawBitmap(resize, (Utils.pageWidth-317)/2, Utils.pageHeight-300, new Paint());
        }

        return result;
    }

    public static Bitmap DefaultGabarit(Bitmap bMap, Bitmap bg)
    {
        return bMap;
    }

    public static Bitmap RoundedRectGabarit(Bitmap bMap, Bitmap bg)
    {
        int margin = 10;
        float radius = 15;

        if(bMap.getWidth() > 300){
            margin = 80;
            radius = 100;
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                margin,
                margin,
                bMap.getWidth() - margin,
                bMap.getHeight() - margin
        );

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(margin, margin, dstBmp.getWidth(), dstBmp.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(dstBmp, rect, rect, paint);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);
        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap SquareLeftGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = bMap.getHeight();
            height = bMap.getHeight();
        }else{
            width = bMap.getWidth();
            height = bMap.getWidth();
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(dstBmp, 0, 0, null);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap RoundGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = (float) (bMap.getHeight() * 0.9);
            height = (float) (bMap.getHeight() * 0.9);
        }else{
            width = (float) (bMap.getWidth() * 0.9);
            height = (float) (bMap.getWidth() * 0.9);
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);
        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawCircle(Math.round((bMap.getWidth())/2), Math.round((bMap.getHeight())/2), width/2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(dstBmp, Math.round((bMap.getWidth() - width)/2), Math.round((bMap.getHeight() - height)/2), paint);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap DiamondGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        int margin = 8;
        float radius = 15;

        if(bMap.getWidth() > 300){
            margin = 80;
            radius = 100;
        }

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = (float) ((bMap.getHeight() - 2*margin)/Math.sqrt(2));
            height = width;
        }else{
            width = (float) ((bMap.getWidth() - 2*margin)/Math.sqrt(2));
            height = width;
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);
        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.save();
        canvas.translate(bMap.getWidth()/2, bMap.getHeight()/2);
        canvas.rotate(45);
        canvas.translate(-bMap.getWidth()/2, -bMap.getHeight()/2);
        canvas.drawRoundRect(Math.round((bMap.getWidth() - width)/2), Math.round((bMap.getHeight() - height)/2), Math.round(bMap.getWidth() - (bMap.getWidth() - width)/2), Math.round(bMap.getHeight() - (bMap.getHeight() - height)/2), radius, radius, paint);

        canvas.restore();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bMap, 0, 0, paint);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap RotateGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = (float) (bMap.getHeight() * 4/3);
            height = (float) (bMap.getHeight());
        }else{
            width = (float) (bMap.getWidth());
            height = (float) (bMap.getWidth() * 4/3);
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        Matrix matrix = new Matrix();
        matrix.setTranslate((bMap.getWidth() - width)/2, (bMap.getHeight() - height)/2);
        matrix.postRotate(-10,bMap.getWidth()/2,bMap.getHeight()/2);

        canvas.drawBitmap(dstBmp, matrix, new Paint());

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap ReverseRotate(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        width = (float) (bMap.getWidth());
        height = (float) (bMap.getWidth() * 3/4);

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        Matrix matrix = new Matrix();
        matrix.setTranslate((bMap.getWidth() - width)/2, (bMap.getHeight() - height)/2);
        matrix.postRotate(-10,bMap.getWidth()/2,bMap.getHeight()/2);

        canvas.drawBitmap(dstBmp, matrix, new Paint());

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap PolaroidGabarit(Bitmap bMap, Bitmap bg)
    {
        int margin = 5;

        if(bMap.getWidth() > 1000){
            margin = 80;
        }

        float width;
        float height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            height = (float) (bMap.getHeight() - 2*margin);
            width = (height * 4/3);

        }else{
            width = (float) (bMap.getWidth() - 2*margin);
            height = (width * 4/3);
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(dstBmp, margin, margin, new Paint());

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap PanaPolaroidGabarit(Bitmap bMap, Bitmap bg)
    {
        int margin = 5;

        if(bMap.getWidth() > 1000){
            margin = 80;
        }

        float width;
        float height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            height = (float) (bMap.getHeight() - 2*margin);
            width = (height * 3/2);

        }else{
            width = (float) (bMap.getWidth() - 2*margin);
            height = (width * 3/2);
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(dstBmp, margin, margin, new Paint());

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap SquarePolaroidGabarit(Bitmap bMap, Bitmap bg)
    {
        int margin = 5;

        if(bMap.getWidth() > 1000){
            margin = 80;
        }

        float width;
        float height;

        width = (float) (bMap.getWidth() - 2*margin);
        height = (width * 3/4);

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        //Bitmap result = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(dstBmp, margin, margin, new Paint());

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap MarginBottomGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        width = (float) (bMap.getWidth());
        height = (width * 9/16);

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(dstBmp, 0, 0, null);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap FourTierGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        if(bMap.getWidth() > bMap.getHeight())
        {
            width = (float) (bMap.getHeight() * 4/3);
            height = (float) (bMap.getHeight());
        }else{
            width = (float) (bMap.getWidth());
            height = (float) (bMap.getWidth() * 4/3);
        }

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(dstBmp, Math.round((bMap.getWidth() - width)/2), Math.round((bMap.getHeight() - height)/2), null);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap ThirdHalfGabarit(Bitmap bMap, Bitmap bg)
    {
        float width;
        float height;

        width = (float) (bMap.getWidth());
        height = (width * 2/3);

        Bitmap dstBmp = Bitmap.createBitmap(
                bMap,
                Math.round((bMap.getWidth() - width)/2),
                Math.round((bMap.getHeight() - height)/2),
                Math.round(width),
                Math.round(height)
        );

        Bitmap result = Bitmap.createBitmap(bMap.getWidth(), bMap.getHeight(), bMap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(dstBmp, Math.round((bMap.getWidth() - width)/2), Math.round((bMap.getHeight() - height)/2), null);

        dstBmp.recycle();

        //Bitmap finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getHeight(), false);

        Bitmap finale;

        if (bMap.getWidth() >= bMap.getHeight()){

            finale = Bitmap.createScaledBitmap(bg, bMap.getWidth(), bMap.getWidth(), false);

        }else{

            finale = Bitmap.createScaledBitmap(bg, bMap.getHeight(), bMap.getHeight(), false);
        }

        finale = Bitmap.createBitmap(finale, (finale.getWidth() - bMap.getWidth())/2, (finale.getHeight() - bMap.getHeight())/2, bMap.getWidth(), bMap.getHeight());

        Canvas finalCanvas = new Canvas(finale);

        finalCanvas.drawBitmap(result, 0,0,null);

        return finale;
    }

    public static Bitmap DefaultAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getWidth() < bMap.getHeight())
        {
            height = (int)Math.round(Utils.pageHeight*0.9);
            width = Math.round(bMap.getWidth() * height / bMap.getHeight());

            if(width > Utils.pageWidth){
                width = (int)Math.round(Utils.pageWidth*0.9);
                height = Math.round(bMap.getHeight() * width / bMap.getWidth());
            }
        }else if(bMap.getWidth() < bMap.getHeight()){
            width = (int)Math.round(Utils.pageWidth*0.9);
            height = Math.round(bMap.getHeight() * width / bMap.getWidth());

            if(height > Utils.pageHeight){
                height = (int)Math.round(Utils.pageHeight*0.9);
                width = Math.round(bMap.getWidth() * height / bMap.getHeight());
            }
        }else{
            width = (int)Math.round(Utils.pageHeight*0.9);
            height = (int)Math.round(Utils.pageHeight*0.9);
        }

        Bitmap resized = Bitmap.createScaledBitmap(bMap, width, height, true);

        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "DefaultAlbumGabarit");
    }

    public static Bitmap MarginBottomAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth()*3/4)
        {
            height = Math.round(bMap.getWidth()*3/4);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*4/3);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, Utils.pageWidth, Utils.pageWidth*3/4, true);

        crop.recycle();
        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "MarginBottomAlbumGabarit");
        //return resized;
    }

    public static Bitmap CenterThirdHalfAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth()*2/3)
        {
            height = Math.round(bMap.getWidth()*2/3);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*3/2);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageWidth*0.9), (int)Math.round(Utils.pageWidth*0.9*3/4), true);

        crop.recycle();
        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "CenterThirdHalfAlbumGabarit");
    }

    public static Bitmap FullScreenAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth())
        {
            height = bMap.getWidth();
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight();
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(bMap, Utils.pageWidth, Utils.pageWidth, true);

        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "FullScreenAlbumGabarit");
    }

    public static Bitmap RotateAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth()*9/16)
        {
            height = Math.round(bMap.getWidth()*9/16);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*16/9);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageWidth*0.9), (int)Math.round(Utils.pageWidth*0.9*3/4), true);

        crop.recycle();
        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "RotateAlbumGabarit");
    }

    public static Bitmap RoundedRectAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        float radius = 100;

        if(bMap.getHeight() > bMap.getWidth()*9/16)
        {
            height = Math.round(bMap.getWidth()*9/16);
            width = bMap.getWidth();

        }else{
            width = Math.round(bMap.getHeight()*16/9);
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageWidth*0.9), (int)Math.round(Utils.pageWidth*0.9*3/4), true);

        crop.recycle();
        //bMap.recycle();


        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, resized.getWidth(), resized.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);

        resized.recycle();

        return TransformationHandler.drawOnPage(result, "RoundedRectAlbumGabarit");
    }

    public static Bitmap LeftThirdHalfAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getWidth() > bMap.getHeight()*2/3)
        {
            width = Math.round(bMap.getHeight()*2/3);
            height = bMap.getHeight();

        }else{
            height = Math.round(bMap.getWidth()*3/2);
            width = bMap.getWidth();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(crop, (int)Math.round(Utils.pageHeight*2/3), Utils.pageHeight, true);

        crop.recycle();
        //bMap.recycle();

        return TransformationHandler.drawOnPage(resized, "LeftThirdHalfAlbumGabarit");
    }

    public static Bitmap RoundAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        if(bMap.getHeight() > bMap.getWidth())
        {
            height = bMap.getWidth();
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight();
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(bMap, Utils.pageWidth/2, Utils.pageWidth/2, true);

        crop.recycle();
        //bMap.recycle();

        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawCircle(Math.round(Utils.pageWidth/4), Math.round(Utils.pageWidth/4), Utils.pageWidth/4, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, 0, 0, paint);

        resized.recycle();

        return TransformationHandler.drawOnPage(result, "RoundAlbumGabarit");
    }

    public static Bitmap RoundedSquareAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        float radius = 100;

        if(bMap.getHeight() > bMap.getWidth())
        {
            height = bMap.getWidth();
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight();
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(bMap, Utils.pageWidth/2, Utils.pageWidth/2, true);

        crop.recycle();
        //bMap.recycle();

        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);

        resized.recycle();

        return TransformationHandler.drawOnPage(result, "RoundedSquareAlbumGabarit");
    }

    public static Bitmap DiamondAlbumGabarit(Bitmap bMap, Bitmap bg)
    {
        int width;
        int height;

        float radius = 100;

        if(bMap.getHeight() > bMap.getWidth())
        {
            height = bMap.getWidth();
            width = bMap.getWidth();

        }else{
            width = bMap.getHeight();
            height = bMap.getHeight();

        }

        Bitmap crop = Bitmap.createBitmap(
                bMap,
                (bMap.getWidth() - width)/2,
                (bMap.getHeight() - height)/2,
                width,
                height
        );

        Bitmap resized = Bitmap.createScaledBitmap(bMap, Utils.pageWidth, Utils.pageWidth, true);

        crop.recycle();
        //bMap.recycle();

        Bitmap result = Bitmap.createBitmap(resized.getWidth(), resized.getHeight(), resized.getConfig());

        Canvas canvas = new Canvas(result);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(new Rect(result.getWidth() - (int)Math.round(result.getWidth()/Math.sqrt(2)), result.getHeight() - (int)Math.round(result.getHeight()/Math.sqrt(2)), (int)Math.round(result.getWidth()/Math.sqrt(2)), (int)Math.round(result.getHeight()/Math.sqrt(2))));

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.save();
        canvas.translate(resized.getWidth()/2, resized.getHeight()/2);
        canvas.rotate(45);
        canvas.translate(-resized.getWidth()/2, -resized.getHeight()/2);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        canvas.restore();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resized, rect, rect, paint);

        resized.recycle();

        return TransformationHandler.drawOnPage(result, "DiamondAlbumGabarit");
    }
}
