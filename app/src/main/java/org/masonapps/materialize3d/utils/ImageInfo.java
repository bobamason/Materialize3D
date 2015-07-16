package org.masonapps.materialize3d.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

/**
 * Created by ims_2 on 3/2/2015.
 */
public class ImageInfo {

    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_URI = "uri";
    private long id;
    private String directory;
    private String name;
    private String filePath;
    private Uri uri;

    public ImageInfo() {
        id = -1;
        directory = "";
        name = "";
        filePath = "";
        uri = null;
    }

    public ImageInfo(long id, String path, Uri uri) {
        this.id = id;
        this.uri = uri;
        filePath = path;
        name = nameFromPath(filePath);
        directory = directoryFromPath(filePath);
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String path) {
        filePath = path;
        name = nameFromPath(filePath);
        directory = directoryFromPath(filePath);
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public static String nameFromPath(String path) {
        final int startIndex = path.lastIndexOf("/");
        final int endIndex = path.lastIndexOf(".");
        String s = "";
        if (startIndex != -1) {
            s = path.substring(startIndex + 1, endIndex);
        }
        return s;
    }

    public static String directoryFromPath(String path) {
        final int end = path.lastIndexOf("/");
        String s = "";
        if (end != -1) {
            final int start = path.lastIndexOf("/", end - 1);
            if (start != -1) {
                s = path.substring(start + 1, end);
            }
        }
        return s;
    }

    public static ImageInfo fromBundle(Bundle bundle) {
        final long id = bundle.getLong(KEY_ID, -1);
        final String path = bundle.getString(KEY_PATH, "");
        final Uri uri = Uri.parse(bundle.getString(KEY_URI, ""));
        ImageInfo info = new ImageInfo(id, path, uri);
        return info;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ID, id);
        bundle.putString(KEY_PATH, filePath);
        bundle.putString(KEY_URI, uri.toString());
        return bundle;
    }

    public static ImageInfo fromUri(Context context, Uri uri) {
        ImageInfo imageInfo = null;
        String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                final int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imageInfo = new ImageInfo();
                imageInfo.setId(cursor.getLong(idIndex));
                imageInfo.setFilePath(cursor.getString(dataIndex));
                imageInfo.setUri(uri);
            }
            cursor.close();
        }
        return imageInfo;
    }

    public static ImageInfo fromPath(Context context, String path) {
        ImageInfo imageInfo = null;
        String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.DATA + " = ?", new String[]{path}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                final int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imageInfo = new ImageInfo();
                final long id = cursor.getLong(idIndex);
                imageInfo.setId(id);
                imageInfo.setFilePath(cursor.getString(dataIndex));
                imageInfo.setUri(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)));
            }
            cursor.close();
        }
        return imageInfo;
    }
}
