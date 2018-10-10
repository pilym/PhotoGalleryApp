package com.example.paul.photogalleryapp;

public class Photo {
    private String fileName = "";
    private String date = "";
    private String caption = "";
    private String latitude = "";
    private String longitude = "";

    public Photo(String fileName, String date, String caption, String latitude, String longitude) {
        this.fileName = fileName;
        this.date = date;
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
