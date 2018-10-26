package com.example.paul.photogalleryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class PhotoHelper {

    static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public enum SEARCH_TYPE {
        NONE,
        SEARCH_BYTIME,
        SEARCH_BYLOCATION,
        SEARCH_BYKEYWORDS
    }

    // convert from bitmap to byte array
    static byte[] convertBitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    static Bitmap covertBytesToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
