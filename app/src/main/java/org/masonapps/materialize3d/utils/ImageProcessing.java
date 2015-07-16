package org.masonapps.materialize3d.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Bob on 6/6/2015.
 */
public class ImageProcessing {

    public static void flipY(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pixels = new int[h * w];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        int index1, index2, temp1, temp2;
        for (int y = 0; y < h / 2; y++) {
            for (int x = 0; x < w; x++) {
                index1 = y * w + x;
                index2 = ((h - 1) - y) * w + x;
                temp1 = pixels[index1];
                temp2 = pixels[index2];
                pixels[index1] = temp2;
                pixels[index2] = temp1;
            }
        }
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
    }

    public static void updateHistogram(int[] grayPixels, int[] histogram){
        if(histogram.length != 256) throw new IllegalArgumentException("histogram array must have a length of 256");
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
        }
        for (int i = 0; i < grayPixels.length; i++) {
            histogram[Math.max(Math.min(grayPixels[i], 255), 0)] ++;
        }
    }

    public static void toGrayScale(int[] pixels){
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = grayScaleLuminance(pixels[i]);
        }
    }

    public static void threshold(int[] grayPixels, int threshold){
        for (int i = 0; i < grayPixels.length; i++) {
            grayPixels[i] = grayPixels[i] > threshold ? 255 : 0;
        }
    }

    public static void equalize(int[] grayPixels, int[] histogram){
        updateHistogram(grayPixels, histogram);
        float total = grayPixels.length;
        int[] lut = new int[histogram.length];
        float sum = 0f;
        for (int i = 0; i < histogram.length; i++) {
            sum += (float) histogram[i] / total;
            lut[i] = (int) Math.floor(255f * sum + 0.5f);
        }

        for (int i = 0; i < grayPixels.length; i++) {
            grayPixels[i] = lut[grayPixels[i]];
        }
        updateHistogram(grayPixels, histogram);
    }

    public static void boxBlur(int[] grayPixels, int w, int h) throws OutOfMemoryError{
        if(w * h != grayPixels.length) throw new IllegalArgumentException("pixel array length size and dimensions do not match");
        int[] outPixels = new int[w * h];
        float topLeft, top, topRight, left, right, bottomLeft, bottom, bottomRight, center;
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                center = grayPixels[y * w + x] / 9f;
                topLeft = grayPixels[(y - 1) * w + (x - 1)] / 9f;
                top = grayPixels[(y - 1) * w + x] / 9f;
                topRight = grayPixels[(y - 1) * w + (x + 1)] / 9f;
                left = grayPixels[y * w + (x - 1)] / 9f;
                right = grayPixels[y * w + (x + 1)] / 9f;
                bottomLeft = grayPixels[(y + 1) * w + (x - 1)] / 9f;
                bottom = grayPixels[(y + 1) * w + x] / 9f;
                bottomRight = grayPixels[(y + 1) * w + (x + 1)] / 9f;

                outPixels[y * w + x] = Math.round(center + topLeft + top + topRight + left + right + bottomLeft + bottom + bottomRight);
            }
        }
        for (int i = 0; i < grayPixels.length; i++) {
            grayPixels[i] = outPixels[i];
        }
    }

    public static void generateHeightMap(Bitmap bitmap, int[] grayPixels) throws OutOfMemoryError{
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if(w * h != grayPixels.length) throw new IllegalArgumentException("pixel array length and bitmap dimensions do not match");
        int[] outPixels = new int[w * h];

        for (int i = 0; i < grayPixels.length; i++) {
            outPixels[i] = Color.rgb(grayPixels[i], grayPixels[i], grayPixels[i]);
        }
        bitmap.setPixels(outPixels, 0, w, 0, 0, w, h);
    }

    public static void generateNormalMap(Bitmap bitmap, int[] grayPixels) throws OutOfMemoryError{
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if(w * h != grayPixels.length) throw new IllegalArgumentException("pixel array length and bitmap dimensions do not match");
        int[] outPixels = new int[w * h];

        double zStrength = 1.2;

        double topLeft, top, topRight, left, right, bottomLeft, bottom, bottomRight, dx, dy, z, x0, y0, z0, mag;
        int red, green, blue;

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                topLeft = grayPixels[(y - 1) * w + (x - 1)] / 255.;
                top = grayPixels[(y - 1) * w + x] / 255.;
                topRight = grayPixels[(y - 1) * w + (x + 1)] / 255.;
                left = grayPixels[y * w + (x - 1)] / 255.;
                right = grayPixels[y * w + (x + 1)] / 255.;
                bottomLeft = grayPixels[(y + 1) * w + (x - 1)] / 255.;
                bottom = grayPixels[(y + 1) * w + x] / 255.;
                bottomRight = grayPixels[(y + 1) * w + (x + 1)] / 255.;

//                dx = topRight + 2. * right + bottomRight - topLeft - 2. * left - bottomLeft;
                dx = 1. * topLeft + 1. * left + 1. * bottomLeft - 1. * topRight - 1. * right - 1. * bottomRight;

                dy = 1. * bottomLeft + 1. * bottom + 1. * bottomRight - 1. * topLeft - 1. * top -  1. * topRight;
//                dy = topLeft + 2. * top + topRight - bottomLeft - 2. * bottom - bottomRight;

                z = Math.max(zStrength * (1. - Math.sqrt(dx * dx + dy * dy)), 0.5d);
                mag = Math.sqrt(dx * dx + dy * dy + z * z);

                x0 = dx / mag;
                y0 = dy / mag;
                z0 = z / mag;

                red = (int) Math.round(x0 * 127. + 127.);
                green = (int) Math.round(y0 * 127. + 127.);
                blue = (int) Math.round(z0 * 127. + 127.);

                outPixels[y * w + x] = Color.rgb(red, green, blue);
            }
        }

        bitmap.setPixels(outPixels, 0, w, 0, 0, w, h);
    }

    public static int grayScaleLuminance(int pixel){
        return Math.round(0.299f * (pixel >> 16 & 0xFF) + 0.587f * (pixel >> 8 & 0xFF) + 0.114f * (pixel & 0xFF));
    }

    public static int grayScaleAverage(int pixel){
        return Math.round(((pixel >> 16 & 0xFF) + (pixel >> 8 & 0xFF) + (pixel & 0xFF)) / 3f);
    }
}
