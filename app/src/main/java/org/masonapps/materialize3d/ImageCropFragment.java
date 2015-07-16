package org.masonapps.materialize3d;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.masonapps.materialize3d.utils.BitmapHelper;
import org.masonapps.materialize3d.views.CropView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class ImageCropFragment extends Fragment {

    private static final String ARG_IMAGE = "imageid";
    public static final String TAG = "Preview Image";
    private CropView cropView;
    private int width = 1024;
    private int height = 1024;
    private OnCropDoneListener listener = null;
    private Uri imageUri;

    public static ImageCropFragment newInstance(Uri uri) {
        ImageCropFragment fragment = new ImageCropFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, uri);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageCropFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable(ARG_IMAGE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_image_crop, container, false);
        cropView = (CropView) view.findViewById(R.id.preview_imageView);
        cropView.setLocked(false);
//        ((ImageSelectActivity) getActivity()).setProgressVisibility(true);
        if (imageUri != null) {
            new LoadImageTask().execute(imageUri);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_crop_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done_crop) {
            RectF rect = cropView.getCropDimens();
            if (rect != null && listener != null) {
                listener.cropDone(cropView.getBitmap(), rect);
                cropView.setLocked(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCropDoneListener) {
            listener = (OnCropDoneListener) activity;
        }
        activity.setTitle("Crop Image");
    }

    @Override
    public void onPause() {
        listener = null;
        super.onPause();
    }

    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Uri... params) {
            Bitmap result = null;
            InputStream inputStream = null;
            final ContentResolver contentResolver = getActivity().getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            final Uri uri = params[0];
            try {
                inputStream = contentResolver.openInputStream(uri);

                if (inputStream != null) {
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    options.inSampleSize = BitmapHelper.calculateInSampleSize(options, width, height);

                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    inputStream = contentResolver.openInputStream(uri);

//                    Log.d(TAG, "options.inSampleSize = " + options.inSampleSize);
                    options.inJustDecodeBounds = false;
                    options.inMutable = true;
                    result = BitmapFactory.decodeStream(inputStream, null, options);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            final ImageSelectActivity activity = (ImageSelectActivity) getActivity();
            if (bitmap != null && cropView != null && activity != null) {
                cropView.setBitmap(bitmap);
            } else {
                Log.e(TAG, bitmap == null ? "bitmap is null" : "bitmap is good");
                Log.e(TAG, cropView == null ? "cropView is null" : "cropView is good");
                Log.e(TAG, activity == null ? "activity is null" : "activity is good");
            }
        }
    }

    public void setListener(OnCropDoneListener listener) {
        this.listener = listener;
    }

    public interface OnCropDoneListener {
        void cropDone(Bitmap bitmap, RectF rect);
    }
}
