package com.example.paul.photogalleryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paul.photogalleryapp.database.DatabaseHelper;
import com.example.paul.photogalleryapp.database.model.Photo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    public static final Double MIN_LATITUDE = -90.0;
    public static final Double MAX_LATITUDE = 90.0;
    public static final Double MIN_LONGITUDE = -180.0;
    public static final Double MAX_LONGITUDE = 180.0;
    static final int CAMERA_REQUEST_CODE = 1;
    private int galleryIndex = 0;
    private List<Photo> photoGallery;
    private LocationManager locationManager;
    private Location location;

    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        db = new DatabaseHelper(this);

        // set up buttons
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnFilter = findViewById(R.id.btnFilter);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnFilter.setOnClickListener(filterListener);

        // set up caption
        EditText photoCaptionEntry = findViewById(R.id.etPhotoCaption);
        photoCaptionEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newCaption = s.toString();
                updateCaption(newCaption, galleryIndex);
            }
        });

        photoGallery = db.readAllPhotos();
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0) {
            if (galleryIndex >= photoGallery.size()) {
                galleryIndex = 0;
            }
            displayPhoto(galleryIndex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    private void updateCaption(String newCaption, int galleryIndex) {
        Photo photo = photoGallery.get(galleryIndex);
        long photoId = photo.getId();

        // sync database with gallery
        photo.setCaption(newCaption);
        db.updatePhotoCaption(photoId, newCaption);
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    // Displays the photo in the gallery
    private void displayPhoto(int galleryId) {
        Photo photo = photoGallery.get(galleryId);
        byte[] imageByteArray = photo.getImage();

        ImageView iv = findViewById(R.id.ivMain);
        iv.setImageBitmap(PhotoHelper.covertBytesToBitmap(imageByteArray));

        // display date
        SimpleDateFormat fmt = new SimpleDateFormat(PhotoHelper.DATE_FORMAT);
        TextView date = findViewById(R.id.photoTimestamp);
        try {
            date.setText(fmt.parse(photo.getTimestamp()).toString());
        } catch (ParseException e) {
            date.setText(R.string.tvDateUnknown);
        }

        // display location
        TextView photoLocation = findViewById(R.id.photoLocation);
        double latitude = photo.getLatitude();
        double longitude = photo.getLongitude();

        photoLocation.setText(String.format("%s: %s, %s: %s", getString(R.string.tvLcationLat), latitude, getString(R.string.tvLcationLong), longitude));

        // display caption
        EditText photoCaptionEntry = findViewById(R.id.etPhotoCaption);
        String photoCaption = photo.getCaption();

        photoCaptionEntry.setText(photoCaption);
    }

    public void onClick(View v) {
        // find new photo index based on button pressed
        switch (v.getId()) {
            case R.id.btnLeft:
                --galleryIndex;
                break;
            case R.id.btnRight:
                ++galleryIndex;
                break;
            default:
                break;
        }
        if (galleryIndex < 0) {
            galleryIndex = 0;
        }
        if (galleryIndex >= photoGallery.size()) {
            galleryIndex = photoGallery.size() - 1;
        }

        if (photoGallery.size() == 0) {
            galleryIndex = 0;
            return;
        }

        displayPhoto(galleryIndex);
    }

    public void onLeftBtnClick() {
        if (photoGallery.size() == 0 || galleryIndex <= 0) {
            galleryIndex = 0;
            return;
        }

        --galleryIndex;

        displayPhoto(galleryIndex);
    }

    public void onRightBtnClick() {
        int gallerySize = photoGallery.size() - 1;
        if (photoGallery.size() == 0 || galleryIndex >= gallerySize) {
            galleryIndex = gallerySize - 1;
            return;
        }

        ++galleryIndex;

        displayPhoto(galleryIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PhotoHelper.SEARCH_TYPE searchType = (PhotoHelper.SEARCH_TYPE) data.getSerializableExtra("SEARCHTYPE");

                switch (searchType) {
                    case SEARCH_BYTIME:
                        populateGalleryByTime(data);
                        break;
                    case SEARCH_BYLOCATION:
                        populateGalleryByLocation(data);
                        break;
                    case SEARCH_BYKEYWORDS:
                        populateGalleryByKeywords(data);
                        break;
                    default:
                        photoGallery = db.readAllPhotos();

                }

                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap newPhoto = (Bitmap) data.getExtras().get("data");
                byte[] photoByteArray = PhotoHelper.convertBitmapToBytes(newPhoto);
                db.createPhoto(location.getLatitude(), location.getLongitude(), photoByteArray);

                photoGallery = db.readAllPhotos();
            }
        }

        galleryIndex = 0;
        if (photoGallery.size() > 0) {
            displayPhoto(galleryIndex);
        }
    }

    private void populateGalleryByTime(Intent data) {
        SimpleDateFormat fmt = new SimpleDateFormat(PhotoHelper.DATE_FORMAT);
        try {
            Date fromDate = fmt.parse(data.getStringExtra("STARTDATE"));
            Date toDate = fmt.parse(data.getStringExtra("ENDDATE"));
            photoGallery = db.readPhotosDateFilter(fromDate, toDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateGalleryByLocation(Intent data) {
        String topLeftLatString = data.getStringExtra("TOPLEFTLAT");
        String topLeftLongString = data.getStringExtra("TOPLEFTLONG");
        String bottomRightLatString = data.getStringExtra("BOTTOMRIGHTLAT");
        String bottomRightLongString = data.getStringExtra("BOTTOMRIGHTLONG");

        Double topLeftLat;
        Double topLeftLong;
        Double bottomRightLat;
        Double bottomRightLong;
        try {
            topLeftLat = Double.parseDouble(topLeftLatString);
            topLeftLong = Double.parseDouble(topLeftLongString);
            bottomRightLat = Double.parseDouble(bottomRightLatString);
            bottomRightLong = Double.parseDouble(bottomRightLongString);

        } catch (NumberFormatException e) {
            topLeftLat = MAX_LATITUDE;
            topLeftLong = MIN_LONGITUDE;
            bottomRightLat = MIN_LATITUDE;
            bottomRightLong = MAX_LONGITUDE;
        }

        photoGallery = db.readPhotosLocationFilter(topLeftLat, topLeftLong, bottomRightLat, bottomRightLong);
    }

    private void populateGalleryByKeywords(Intent data) {
        String keywords = data.getStringExtra("KEYWORDS");
        photoGallery = db.readPhotosKeywordFilter(keywords);
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return;
    }
}
