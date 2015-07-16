package org.masonapps.materialize3d;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.masonapps.materialize3d.utils.ImageInfo;
import org.masonapps.materialize3d.utils.ThumbnailWorkerTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThumbnailFragment extends Fragment {

    private final static String MAIN_DIRECTORY_STRING = "use directory list";
    private static final String DIRECTORY_KEY = "directoryKey";
    private GridAdapter adapter;
    private OnThumbnailSelectedListener listener = null;
    private HashMap<String, List<ImageInfo>> imageMap;
    private List<ImageInfo> directoryList;
    private boolean useDirectoryList;
    private String currentDirectory = MAIN_DIRECTORY_STRING;
    private GridView gridView;
    private int numColumns;
    private int numColumnsDirectory;

    public ThumbnailFragment() {
        imageMap = new HashMap<>();
        directoryList = new ArrayList<>();
    }

    public static ThumbnailFragment newInstance() {
        final ThumbnailFragment fragment = new ThumbnailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_thumbnail, container, false);
        adapter = new GridAdapter(getActivity().getApplicationContext(), null);
        gridView = (GridView) view.findViewById(R.id.thumbnail_gridView);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        numColumns = (int) Math.floor(metrics.widthPixels / metrics.density / 100);
        numColumns = Math.max(2, numColumns);
        numColumnsDirectory = (int) Math.floor(metrics.widthPixels / metrics.density / 160);
        numColumnsDirectory = Math.max(1, numColumnsDirectory);
        gridView.setNumColumns(numColumnsDirectory);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (useDirectoryList) {
                    final String key = directoryList.get(position).getDirectory();
                    adapter.setList(imageMap.get(key));
                    useDirectoryList = false;
                    currentDirectory = key;
                    gridView.setNumColumns(numColumns);
                    getActivity().setTitle(getActivity().getResources().getString(R.string.select_photo));
                } else {
                    final ImageInfo info = (ImageInfo) adapter.getItem(position);
                    if (listener != null && info != null) {
                        listener.thumbnailSelected(info);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadListsTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentDirectory = savedInstanceState.getString(DIRECTORY_KEY, MAIN_DIRECTORY_STRING);
        } else {
            currentDirectory = MAIN_DIRECTORY_STRING;
        }
        Log.d(this.getClass().getSimpleName(), "currentDirectory = " + currentDirectory);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DIRECTORY_KEY, currentDirectory);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnThumbnailSelectedListener) {
            listener = (OnThumbnailSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        if (listener != null) listener = null;
        super.onDetach();
    }

    @Override
    public void onPause() {
        imageMap.clear();
        directoryList.clear();
        super.onPause();
    }

    public boolean blockBackPress() {
        if (!useDirectoryList) {
            getActivity().setTitle(getActivity().getResources().getString(R.string.select_folder));
            adapter.setList(directoryList);
            useDirectoryList = true;
            gridView.setNumColumns(numColumnsDirectory);
            currentDirectory = MAIN_DIRECTORY_STRING;
            return true;
        } else {
            return false;
        }
    }

    public void setOnThumbnailSelectedListener(OnThumbnailSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnThumbnailSelectedListener {
        void thumbnailSelected(ImageInfo info);
    }

    private class LoadListsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            imageMap.clear();
            directoryList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadImages(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            loadImages(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            return null;
        }

        private void loadImages(Uri storageUri) {
            String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED};
            Cursor cursor = getActivity().getContentResolver().query(storageUri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
            if (cursor.moveToFirst()) {
                final int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                final int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setId(cursor.getLong(idIndex));
                    imageInfo.setFilePath(cursor.getString(dataIndex));
                    imageInfo.setUri(Uri.withAppendedPath(storageUri, String.valueOf(imageInfo.getId())));
                    if (imageMap.containsKey(imageInfo.getDirectory())) {
                        imageMap.get(imageInfo.getDirectory()).add(imageInfo);
                    } else {
                        final ArrayList<ImageInfo> list = new ArrayList<>();
                        list.add(imageInfo);
                        imageMap.put(imageInfo.getDirectory(), list);
                    }
                }
                cursor.close();
                for (String s : imageMap.keySet()) {
                    directoryList.add(imageMap.get(s).get(0));
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (adapter != null) {
                if (!MAIN_DIRECTORY_STRING.equals(currentDirectory) && imageMap.containsKey(currentDirectory)) {
                    adapter.setList(imageMap.get(currentDirectory));
                    useDirectoryList = false;
                } else {
                    adapter.setList(directoryList);
                    useDirectoryList = true;
                }
                getActivity().setTitle(useDirectoryList ? getActivity().getResources().getString(R.string.select_folder) : getActivity().getResources().getString(R.string.select_photo));
                gridView.setNumColumns(useDirectoryList ? numColumnsDirectory : numColumns);
            }
        }
    }

    private class GridAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private final Context context;
        private final Bitmap placeholderBitmap;
        private List<ImageInfo> list;

        public GridAdapter(Context context, ArrayList list) {
            this.list = list;
            this.context = context;
            placeholderBitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);
            inflater = LayoutInflater.from(this.context);
        }

        public void setList(List<ImageInfo> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return list != null ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return list != null ? list.get(position).getId() : -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.thumbnail_item_layout, parent, false);
                holder = new ViewHolder();
                holder.nameText = (TextView) convertView.findViewById(R.id.thumbnail_nameText);
                holder.imageView = (ImageView) convertView.findViewById(R.id.thumbnail_imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (useDirectoryList) {
                final String text = list.get(position).getDirectory();
                holder.nameText.setVisibility(View.VISIBLE);
                holder.nameText.setText(text);
            } else {
                holder.nameText.setVisibility(View.GONE);
            }
            ThumbnailWorkerTask.loadThumbnail(context, list.get(position).getId(), holder.imageView, placeholderBitmap, useDirectoryList);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private class ViewHolder {
            TextView nameText;
            ImageView imageView;
        }
    }
}
