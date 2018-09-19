package com.photoweb.piiics.utils;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ThumbnailFormat;
import com.dropbox.core.v2.files.ThumbnailSize;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * Created by dnizard on 29/06/2017.
 */

public class FileThumbnailRequestHandler extends RequestHandler {

    private static final String SCHEME =  "dropbox";
    private static final String HOST = "dropbox";
    private final DbxClientV2 mDbxClient;

    public FileThumbnailRequestHandler(DbxClientV2 dbxClient) {
        mDbxClient = dbxClient;
    }

    /**
     * Builds a {@link Uri} for a Dropbox file thumbnail suitable for handling by this handler
     */
    public static Uri buildPicassoUri(String file) {
        return new Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .path(file).build();
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME.equals(data.uri.getScheme()) && HOST.equals(data.uri.getHost());
    }

    @Override
    public Result load(Request data) throws IOException {
        try {
            DbxDownloader<FileMetadata> downloader =
                    mDbxClient.files().getThumbnailBuilder(data.uri.getPath())
                            .withFormat(ThumbnailFormat.JPEG)
                            .withSize(ThumbnailSize.W1024H768)
                            .start();

            return new Result(BitmapFactory.decodeStream(downloader.getInputStream()), Picasso.LoadedFrom.NETWORK);
        } catch (DbxException e) {
            Log.d("Dropbox", e.getLocalizedMessage());
            throw new IOException(e);

        }
    }
}