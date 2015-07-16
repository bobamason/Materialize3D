package org.masonapps.materialize3d.utils;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by ims_2 on 2/25/2015.
 */
public class AsyncThumbDrawable extends BitmapDrawable {

    private final WeakReference<ThumbnailWorkerTask> thumbnailWorkerTaskReference;

    public AsyncThumbDrawable(Resources res, Bitmap placeholder, ThumbnailWorkerTask task) {
        super(res, placeholder);
        thumbnailWorkerTaskReference = new WeakReference<>(task);
    }

    public ThumbnailWorkerTask getThumbnailWorkerTask() {
        return thumbnailWorkerTaskReference.get();
    }
}
