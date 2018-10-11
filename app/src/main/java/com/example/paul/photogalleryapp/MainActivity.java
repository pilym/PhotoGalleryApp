package com.example.paul.photogalleryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    public static final String IMAGES_DIRECTORY = Environment.DIRECTORY_PICTURES;
    public static final String IMAGEINFO_DIRECTORY = Environment.DIRECTORY_DOCUMENTS;
    public static final String DATE_FORMAT_PATTERN = "yyyyMMdd";
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    public static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    public static final Float MIN_LATITUDE = -90.0f;
    public static final Float MAX_LATITUDE = 90.0f;
    public static final Float MIN_LONGITUDE = -180.0f;
    public static final Float MAX_LONGITUDE = 180.f;
    static final int CAMERA_REQUEST_CODE = 1;
    private String currentPhotoPath = null;
    private String currentPhotoCaptionPath = null;
    private int currentPhotoIndex = 0;
    private ArrayList<String> photoGallery;
    private ArrayList<String> photoCaptions;
    private LocationManager locationManager;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                updateCaption(newCaption, currentPhotoIndex);
            }
        });

        photoCaptions = populateCaptionsByDate(MIN_DATE, MAX_DATE);
        photoGallery = populateGalleryByDate(MIN_DATE, MAX_DATE);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0) {
            if (currentPhotoIndex >= photoGallery.size()) {
                currentPhotoIndex = 0;
            }
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);
            displayPhoto(currentPhotoPath);
            displayPhotoInfo(currentPhotoCaptionPath);
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

    private void updateCaption(String newCaption, int currentPhotoIndex) {
        File imageDataFile = new File(photoCaptions.get(currentPhotoIndex));
        try {
            BufferedReader br = new BufferedReader(new FileReader(imageDataFile));
            String oldCaption = br.readLine();
            String lat = br.readLine();
            String lng = br.readLine();

            FileWriter writer = new FileWriter(imageDataFile);
            writer.write(String.format("%s\n%s\n%s", newCaption, lat, lng));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private ArrayList<String> populateGalleryByLocation(Date minDate, Date maxDate) {
        // get list of images
        File imagesDir = getExternalFilesDir(IMAGES_DIRECTORY);

        photoGallery = new ArrayList<String>();

        assert imagesDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of file names
        File[] fList = imagesDir.listFiles();
        if (fList != null) {
            for (File f : imagesDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    fileDate = fmt.parse(fileNameDate);

                    if (fileDate.after(minDate) && fileDate.before(maxDate)) {
                        photoGallery.add(f.getPath());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoGallery;
    }

    private ArrayList<String> populateCaptionsByLocation(Float topLeftLat, Float topLeftLong,
                                                        Float bottomRightLat, Float bottomRightLong) {
        // get list of captions
        File captionsDir = getExternalFilesDir(IMAGEINFO_DIRECTORY);

        ArrayList<String> photoCaptions = new ArrayList<>();

        assert captionsDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of captions
        File[] fList = captionsDir.listFiles();

        if (fList != null) {
            for (File f : captionsDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String caption = br.readLine();
                    Float lat = Float.parseFloat(br.readLine());
                    Float lng = Float.parseFloat(br.readLine());


                    if (lat <= topLeftLat && lat >= bottomRightLat
                            && lng >= topLeftLong && lng <= bottomRightLat) {
                        photoCaptions.add(f.getPath());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoCaptions;
    }

    private ArrayList<String> populateGalleryByDate(Date minDate, Date maxDate) {
        // get list of images
        File imagesDir = getExternalFilesDir(IMAGES_DIRECTORY);

        photoGallery = new ArrayList<String>();

        assert imagesDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of file names
        File[] fList = imagesDir.listFiles();
        if (fList != null) {
            for (File f : imagesDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    fileDate = fmt.parse(fileNameDate);

                    if (fileDate.after(minDate) && fileDate.before(maxDate)) {
                        photoGallery.add(f.getPath());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoGallery;
    }

    private ArrayList<String> populateCaptionsByDate(Date minDate, Date maxDate) {
        // get list of captions
        File captionsDir = getExternalFilesDir(IMAGEINFO_DIRECTORY);

        photoCaptions = new ArrayList<String>();

        assert captionsDir != null;

        // filter names
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // create list of captions
        File[] fList = captionsDir.listFiles();

        if (fList != null) {
            for (File f : captionsDir.listFiles()) {
                String fileName = f.getName();
                String fileNameDate = fileName.split("_")[1];
                Date fileDate;
                try {
                    fileDate = fmt.parse(fileNameDate);

                    if (fileDate.after(minDate) && fileDate.before(maxDate)) {
                        photoCaptions.add(f.getPath());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return photoCaptions;
    }

    // Displays the photo and date in the gallery given the path
    private void displayPhoto(String path) {
        ImageView iv = findViewById(R.id.ivMain);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
        TextView date = findViewById(R.id.photoTimestamp);
        try {
            String[] photoPathSplit = path.split("_");
            String photoDateUnformatted = photoPathSplit[1] + "_" + photoPathSplit[2];
            date.setText(fmt.parse(photoDateUnformatted).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Displays the photo caption given the path
    private void displayPhotoInfo(String path) {
        // display photo caption
        EditText photoCaptionEntry = findViewById(R.id.etPhotoCaption);
        TextView photoLocation = findViewById(R.id.photoLocation);

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String caption = br.readLine();
            photoCaptionEntry.setText(caption);

            String latitude = br.readLine();
            String longitude = br.readLine();
            if (latitude.equals("null") || longitude.equals("null")) {
                photoLocation.setText(R.string.tvLocationUnknown);
            } else {
                photoLocation.setText(String.format("%s: %s, %s: %s", getString(R.string.tvLcationLat), latitude, getString(R.string.tvLcationLong), longitude));
            }

        } catch (FileNotFoundException e) {
            photoCaptionEntry.setText("");
            photoLocation.setText(R.string.tvLocationUnknown);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        // find new photo index based on button pressed
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            default:
                break;
        }
        if (currentPhotoIndex < 0) {
            currentPhotoIndex = 0;
        }
        if (currentPhotoIndex >= photoGallery.size()) {
            currentPhotoIndex = photoGallery.size() - 1;
        }

        if (photoGallery.size() == 0) {
            return;
        }
        // get new photo and caption from index
        currentPhotoPath = photoGallery.get(currentPhotoIndex);
        currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);

        Log.d("phpotoleft, size", Integer.toString(photoGallery.size()));
        Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
        displayPhoto(currentPhotoPath);
        displayPhotoInfo(currentPhotoCaptionPath);
    }


    public void goToSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToDisplay(String x) {
        Intent i = new Intent(this, DisplayActivity.class);
        i.putExtra("DISPLAY_TEXT", x);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PhotoHelper.SEARCH_TYPE searchType = (PhotoHelper.SEARCH_TYPE) data.getSerializableExtra("SEARCHTYPE");

                switch (searchType) {
                    case SEARCH_BYTIME:
                        Log.d("createImageFile", data.getStringExtra("STARTDATE"));
                        Log.d("createImageFile", data.getStringExtra("ENDDATE"));

                        String fromDateString = data.getStringExtra("STARTDATE");
                        String toDateString = data.getStringExtra("ENDDATE");

                        filterPhotosByDate(fromDateString, toDateString);
                        break;
                    case SEARCH_BYLOCATION:
                        String topLeftLatString = data.getStringExtra("TOPLEFTLAT");
                        String topLeftLongString = data.getStringExtra("TOPLEFTLONG");
                        String bottomRightLatString = data.getStringExtra("BOTTOMRIGHTLAT");
                        String bottomRightLongString = data.getStringExtra("BOTTOMRIGHTLONG");

                        Float topLeftLat;
                        Float topLeftLong;
                        Float bottomRightLat;
                        Float bottomRightLong;
                        try {
                            topLeftLat = Float.parseFloat(topLeftLatString);
                            topLeftLong = Float.parseFloat(topLeftLongString);
                            bottomRightLat = Float.parseFloat(bottomRightLatString);
                            bottomRightLong = Float.parseFloat(bottomRightLongString);

                        } catch (NumberFormatException e) {
                            topLeftLat = MAX_LATITUDE;
                            topLeftLong = MIN_LONGITUDE;
                            bottomRightLat = MIN_LATITUDE;
                            bottomRightLong = MAX_LONGITUDE;
                        }

                        photoCaptions = populateCaptionsByLocation(topLeftLat, topLeftLong, bottomRightLat, bottomRightLong);
                        break;
                    case SEARCH_BYKEYWORDS:
                        break;
                    default:
                        photoGallery = populateGalleryByDate(MIN_DATE, MAX_DATE);
                        photoCaptions = populateCaptionsByDate(MIN_DATE, MAX_DATE);

                }

                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                photoGallery = populateGalleryByDate(MIN_DATE, MAX_DATE);
                photoCaptions = populateCaptionsByDate(MIN_DATE, MAX_DATE);

            }
        }

        currentPhotoIndex = 0;
        if (photoCaptions.size() > 0) {
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            currentPhotoCaptionPath = photoCaptions.get(currentPhotoIndex);
            displayPhoto(currentPhotoPath);
            displayPhotoInfo(currentPhotoCaptionPath);
        }
    }

    private void filterPhotosByDate(String fromDateString, String toDateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDate;
        Date toDate;
        try {
            fromDate = fmt.parse(fromDateString);
            toDate = fmt.parse(toDateString);

        } catch (ParseException e) {
            fromDate = MIN_DATE;
            toDate = MAX_DATE;
        }

        photoGallery = populateGalleryByDate(fromDate, toDate);
        photoCaptions = populateCaptionsByDate(fromDate, toDate);
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.paul.photogalleryapp.pictures.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File picturesDir = getExternalFilesDir(IMAGES_DIRECTORY);
        File imageInfoDir = getExternalFilesDir(IMAGEINFO_DIRECTORY);

        // create image file
        File image = File.createTempFile(imageFileName, ".jpg", picturesDir);
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", "image path: " + currentPhotoPath);

        // create empty caption file for image
        File imageData = File.createTempFile(imageFileName, ".txt", imageInfoDir);
        FileWriter writer = new FileWriter(imageData);
        writer.write('\n');
        writer.write(String.valueOf(location.getLatitude()));
        writer.write('\n');
        writer.write(String.valueOf(location.getLongitude()));
        writer.flush();
        writer.close();

        return image;
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
