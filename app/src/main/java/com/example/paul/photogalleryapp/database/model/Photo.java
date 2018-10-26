package com.example.paul.photogalleryapp.database.model;

public class Photo {
    public static final String TABLE_NAME = "photos";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "tstamp";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_IMAGE = "image";

    private int id;
    private String timestamp;
    private double latitude;
    private double longitude;
    private String caption;
    private byte[] image;


    public static final String CREATE_TABLE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_LAT + " REAL,"
                    + COLUMN_LONG + " REAL,"
                    + COLUMN_CAPTION + " TEXT,"
                    + COLUMN_IMAGE + " BLOB"
                    + ")";

    public Photo() {

    }

    public Photo(int id, String timestamp, double lat, double lng, String caption, byte[] image) {
        this.id = id;
        this.timestamp = timestamp;
        this.latitude = lat;
        this.longitude = lng;
        this.caption = caption;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
