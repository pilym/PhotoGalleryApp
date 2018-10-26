package com.example.paul.photogalleryapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.paul.photogalleryapp.database.model.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "photo_gallery";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Photo.CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Photo.TABLE_NAME);
        onCreate(db);
    }

    public long createPhoto(double latitude, double longitude, byte[] image) {
        // get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // fill out value to insert
        ContentValues values = new ContentValues();
        values.put(Photo.COLUMN_LAT, latitude);
        values.put(Photo.COLUMN_LONG, longitude);
        values.put(Photo.COLUMN_IMAGE, image);

        // insert row
        long id = db.insert(Photo.TABLE_NAME, null, values);

        // close db connection
        db.close();

        return id;
    }

    public Photo readPhoto(long id) {
        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Photo.TABLE_NAME,
                new String[]{Photo.COLUMN_ID, Photo.COLUMN_TIMESTAMP, Photo.COLUMN_LAT, Photo.COLUMN_LONG, Photo.COLUMN_CAPTION, Photo.COLUMN_IMAGE},
                Photo.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // prepare photo object
        assert cursor != null;
        Photo photo = new Photo(
                cursor.getInt(cursor.getColumnIndex(Photo.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Photo.COLUMN_TIMESTAMP)),
                cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LAT)),
                cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LONG)),
                cursor.getString(cursor.getColumnIndex(Photo.COLUMN_CAPTION)),
                cursor.getBlob(cursor.getColumnIndex(Photo.COLUMN_IMAGE))
        );

        // close db connection
        cursor.close();
        db.close();

        return photo;
    }

    public List<Photo> readAllPhotos() {
        List<Photo> photos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Photo.TABLE_NAME + " ORDER BY " +
                Photo.COLUMN_TIMESTAMP + " ASC";

        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getInt(cursor.getColumnIndex(Photo.COLUMN_ID)));
                photo.setTimestamp(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_TIMESTAMP)));
                photo.setLatitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LAT)));
                photo.setLongitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LONG)));
                photo.setCaption(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_CAPTION)));
                photo.setImage(cursor.getBlob(cursor.getColumnIndex(Photo.COLUMN_IMAGE)));

                photos.add(photo);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        return photos;
    }

    public List<Photo> readPhotosDateFilter(Date fromDate, Date toDate) {
        List<Photo> photos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Photo.TABLE_NAME +
                " WHERE " + Photo.COLUMN_TIMESTAMP +
                " BETWEEN " + fromDate.toString() +
                " AND " + toDate.toString() +
                " ORDER BY " + Photo.COLUMN_TIMESTAMP + " ASC";

        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getInt(cursor.getColumnIndex(Photo.COLUMN_ID)));
                photo.setTimestamp(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_TIMESTAMP)));
                photo.setLatitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LAT)));
                photo.setLongitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LONG)));
                photo.setCaption(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_CAPTION)));
                photo.setImage(cursor.getBlob(cursor.getColumnIndex(Photo.COLUMN_IMAGE)));

                photos.add(photo);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        return photos;
    }

    public List<Photo> readPhotosLocationFilter(double topLeftLat, double topleftLong, double bottomRightLat, double bottomRightLong) {
        List<Photo> photos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Photo.TABLE_NAME +
                " WHERE " + Photo.COLUMN_LAT +
                " BETWEEN " + topLeftLat +
                " AND " + bottomRightLat +
                " AND " + Photo.COLUMN_LONG +
                " BETWEEN " + bottomRightLong +
                " AND " + topleftLong +
                " ORDER BY " + Photo.COLUMN_TIMESTAMP + " ASC";

        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getInt(cursor.getColumnIndex(Photo.COLUMN_ID)));
                photo.setTimestamp(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_TIMESTAMP)));
                photo.setLatitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LAT)));
                photo.setLongitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LONG)));
                photo.setCaption(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_CAPTION)));
                photo.setImage(cursor.getBlob(cursor.getColumnIndex(Photo.COLUMN_IMAGE)));

                photos.add(photo);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        return photos;
    }

    public List<Photo> readPhotosKeywordFilter(String keyword) {
        List<Photo> photos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Photo.TABLE_NAME +
                " WHERE " + Photo.COLUMN_CAPTION +
                " LIKE '%" + keyword + "%'" +
                " ORDER BY " + Photo.COLUMN_TIMESTAMP + " ASC";

        // get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getInt(cursor.getColumnIndex(Photo.COLUMN_ID)));
                photo.setTimestamp(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_TIMESTAMP)));
                photo.setLatitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LAT)));
                photo.setLongitude(cursor.getDouble(cursor.getColumnIndex(Photo.COLUMN_LONG)));
                photo.setCaption(cursor.getString(cursor.getColumnIndex(Photo.COLUMN_CAPTION)));
                photo.setImage(cursor.getBlob(cursor.getColumnIndex(Photo.COLUMN_IMAGE)));

                photos.add(photo);
            } while (cursor.moveToNext());
        }

        // close db connection
        cursor.close();
        db.close();

        return photos;
    }

    public int getPhotoCount() {
        String countQuery = "SELECT  * FROM " + Photo.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public long updatePhotoCaption(long id, String newCaption) {
        // get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Photo.COLUMN_CAPTION, newCaption);

        // update row
        db.update(Photo.TABLE_NAME, values, Photo.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        // close db connection
        db.close();

        return id;
    }

    public long updatePhotoImage(long id, byte[] newImage) {
        // get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Photo.COLUMN_IMAGE, newImage);

        // update row
        db.update(Photo.TABLE_NAME, values, Photo.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        // close db connection
        db.close();

        return id;
    }

    public void deletePhoto(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Photo.TABLE_NAME, Photo.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        // close db connection
        db.close();
    }
}
