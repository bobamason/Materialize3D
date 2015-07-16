package org.masonapps.materialize3d.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by ims_2 on 2/25/2015.
 */
public class ThumbnailWorkerTask extends AsyncTask<Long, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private final boolean useLargeImage;
    public long data = 0;
    private Context context;

    public ThumbnailWorkerTask(Context context, ImageView imageView, boolean useLargeImage) {
        imageViewReference = new WeakReference<>(imageView);
        this.context = context;
        this.useLargeImage = useLargeImage;
    }

    private static ThumbnailWorkerTask getThumbnailWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncThumbDrawable) {
                final AsyncThumbDrawable asyncThumbDrawable = (AsyncThumbDrawable) drawable;
                return asyncThumbDrawable.getThumbnailWorkerTask();
            }
        }
        return null;
    }

    public static void loadThumbnail(Context context, long id, ImageView imageView, Bitmap placeholder, boolean useLargeImage) {
        if (cancelPotentialWork(id, imageView)) {
            final ThumbnailWorkerTask task = new ThumbnailWorkerTask(context, imageView, useLargeImage);
            final AsyncThumbDrawable asyncThumbDrawable = new AsyncThumbDrawable(context.getResources(), placeholder, task);
            imageView.setImageDrawable(asyncThumbDrawable);
            task.execute(id);
        }
    }

    private static boolean cancelPotentialWork(long id, ImageView imageView) {
        final ThumbnailWorkerTask thumbnailWorkerTask = getThumbnailWorkerTask(imageView);
        if (thumbnailWorkerTask != null) {
            final long bitmapData = thumbnailWorkerTask.data;
            if (bitmapData == 0 || bitmapData != id) {
                thumbnailWorkerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        data = params[0];
        return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), data, useLargeImage ? MediaStore.Images.Thumbnails.MINI_KIND : MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (imageViewReference != null && bitmap != null) {
            ImageView imageView = imageViewReference.get();
            final ThumbnailWorkerTask thumbnailWorkerTask = getThumbnailWorkerTask(imageView);
            if (this == thumbnailWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
